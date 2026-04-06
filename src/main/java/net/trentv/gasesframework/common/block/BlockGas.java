package net.trentv.gasesframework.common.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.trentv.gases.common.configuration.GasesMainConfigurations;
import net.trentv.gasesframework.api.*;
import net.trentv.gasesframework.api.reaction.block.IBlockReaction;
import net.trentv.gasesframework.api.reaction.entity.IEntityReaction;
import net.trentv.gasesframework.api.reaction.gas.IGasReaction;
import net.trentv.gasesframework.api.sample.ISample;
import net.trentv.gasesframework.common.CommonEvents;
import net.trentv.gasesframework.common.GasesFrameworkObjects;
import net.trentv.gasesframework.common.entity.EntityDelayedExplosion;

public class BlockGas extends Block implements ISample
{
	public static final PropertyInteger CAPACITY = PropertyInteger.create("capacity", 1, 16);
	public GasType gasType;

	public BlockGas(GasType type)
	{
		super(MaterialGas.INSTANCE);
		this.gasType = type;
		disableStats();
		setHardness(0.0f);
		setLightOpacity(type.opacity);
		setCreativeTab(type.creativeTab);
		setResistance(0);
		setTranslationKey("gas_" + type.name);
		this.setDefaultState(blockState.getBaseState().withProperty(CAPACITY, 16));
	}

	// Block & block state

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		worldIn.scheduleBlockUpdate(pos, this, GasesMainConfigurations.GASES.tickRate + RANDOM.nextInt(6), 1);
	}

	@Override
	public void updateTick(World world, BlockPos currentPosition, IBlockState state, Random rand)
	{
		// Considerations:
		//
		// Optimize to only run when the block next to it updates (after coming
		// to rest)
		// Look into MutableBlockPos to reduce GC overhead

		if (!gasType.preTick(world, state, currentPosition))
		{
			return;
		}

		for (int i = 0; i < EnumFacing.VALUES.length; i++)
		{
			BlockPos scanPos = currentPosition.offset(EnumFacing.VALUES[i]);
			IBlockState scanState = world.getBlockState(scanPos);
			for (IBlockReaction r : this.gasType.getBlockReactions())
			{
				r.react(scanState.getBlock(), world, this.gasType, currentPosition);
			}
			if (scanState.getBlock() instanceof BlockGas neighborGas)
			{
				for (IGasReaction r : this.gasType.getGasReactions())
				{
					r.react(this.gasType, world, neighborGas.gasType, currentPosition);
				}
			}
			if (gasType.combustability != Combustibility.NONE && GFRegistrationAPI.isIgnitionSource(scanState))
			{
				ignite(currentPosition, world);
				return;
			}
		}

		if (gasType.dissipationRate > 0)
		{
			if (rand.nextInt(16) < gasType.dissipationRate)
			{
				int newAmount = state.getValue(CAPACITY) - gasType.dissipationAmount;
				GFManipulationAPI.setGasLevel(currentPosition, world, gasType, newAmount);
			}
		}

		// If density is 0, we're going to be spreading out in a cloud
		if (gasType.density == 0)
		{
			EnumFacing newDir;
			// Iterate through all directions (up/down/left/right/front/back)
			for (int i = 0; i < EnumFacing.VALUES.length; i++)
			{
				newDir = EnumFacing.VALUES[i];
				BlockPos flowToBlock = currentPosition.offset(newDir);
				int thisValue = state.getValue(CAPACITY);
				// Checks if it can flow into the block AND the current gas
				// capacity is over the cohesion level
				if (GFManipulationAPI.canPlaceGas(flowToBlock, world, this.gasType) & thisValue > gasType.cohesion)
				{
					int flowValue = GFManipulationAPI.getGasLevel(flowToBlock, world);
					if (flowValue + 1 < thisValue)
					{
						GFManipulationAPI.addGasLevel(flowToBlock, world, this.gasType, 1);
						GFManipulationAPI.setGasLevel(currentPosition, world, this.gasType, GFManipulationAPI.getGasLevel(currentPosition, world) - 1);
					}
				}
			}
		}
		// We're going to be flowing either up or down here because density != 0
		else
		{
			// Decide which direction we're going
			EnumFacing direction = EnumFacing.DOWN;
			if (gasType.density > 0) direction = EnumFacing.UP;

			BlockPos nextPosition = scanForOpenBlock(world, this, currentPosition, direction);
			int thisValue = state.getValue(CAPACITY);
			if (!nextPosition.equals(currentPosition)) // In this case, we'll be
			// flowing somewhere
			// above or below.
			{
				int remaining = GFManipulationAPI.addGasLevel(nextPosition, world, this.gasType, thisValue);
				if (state.getValue(CAPACITY) != remaining)
				{
					GFManipulationAPI.setGasLevel(currentPosition, world, this.gasType, remaining);
				}
			}
			else // Can't flow above or below, so time to spill out on the
			// ground
			{
				if (thisValue > 1)
				{
					EnumFacing newDir = EnumFacing.SOUTH;
					for (int i = 0; i < 4; i++)
					{
						newDir = newDir.rotateY();
						BlockPos flowToBlock = nextPosition.offset(newDir);
						if (GFManipulationAPI.canPlaceGas(flowToBlock, world, this.gasType))
						{
							int flowValue = GFManipulationAPI.getGasLevel(flowToBlock, world);
							if (flowValue + 1 < thisValue)
							{
								GFManipulationAPI.addGasLevel(flowToBlock, world, this.gasType, 1);
								GFManipulationAPI.setGasLevel(nextPosition, world, this.gasType, GFManipulationAPI.getGasLevel(nextPosition, world) - 1);
							}
						}
					}
				}
			}
		}

		gasType.postTick(world, state, currentPosition);

		if (gasType.requiresNewTick(world, state, currentPosition))
		{
			world.scheduleBlockUpdate(currentPosition, this, GasesMainConfigurations.GASES.tickRate + RANDOM.nextInt(6), 1);
		}
	}

	public BlockPos scanForOpenBlock(World world, BlockGas gas, BlockPos pos, EnumFacing direction)
	{
		if (GFManipulationAPI.canPlaceGas(pos.offset(direction), world, gas.gasType))
		{
			return pos.offset(direction);
		}

		EnumFacing newDir = EnumFacing.NORTH;
		for (int i = 0; i < 4; i++)
		{
			newDir = newDir.rotateY();
			if (GFManipulationAPI.canPlaceGas(pos.offset(newDir), world, gas.gasType) & GFManipulationAPI.canPlaceGas(pos.offset(newDir).offset(direction), world, gas.gasType))
			{
				return pos.offset(newDir);
			}
		}

		return pos;
	}

	@Override
	public BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, CAPACITY);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return state;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(CAPACITY, 16 - meta);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return 16 - state.getValue(CAPACITY);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return NULL_AABB;
	}

	@Override
	public boolean canCollideCheck(IBlockState state, boolean fullHit)
	{
		return false;
	}

	// Necessary so you can walk through the block. Don't remove it, dumbass.
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return false;
	}

	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion)
	{
		ignite(pos, world);
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		return 0;
	}

	// Client Side

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	// Gases relevant

	public void ignite(BlockPos pos, World access)
	{
		if (!(access.getBlockState(pos).getBlock() instanceof BlockGas))
		{
			return;
		}
		if (gasType.combustability.explosionPower > 0)
		{
			float capacity = access.getBlockState(pos).getValue(CAPACITY);
			float explosionPower = (capacity / 16) * gasType.combustability.explosionPower * 3;
			EntityDelayedExplosion exploder = new EntityDelayedExplosion(access, 5, explosionPower, true, true);
			exploder.setPosition(pos.getX(), pos.getY(), pos.getZ());
			access.spawnEntity(exploder);
			GFManipulationAPI.setGasLevel(pos, access, GasesFrameworkAPI.AIR, 16);
		}
		if (gasType.combustability.fireSpreadRate > 0)
		{
			GFManipulationAPI.setGasLevel(pos, access, GasesFrameworkObjects.FIRE, access.getBlockState(pos).getValue(CAPACITY));
		}
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return null;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		List<ItemStack> ret = new ArrayList<ItemStack>();
		return ret;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		if (!(entity instanceof EntityLivingBase living))
		{
			return;
		}
		if (living instanceof EntityPlayer player)
		{
			if (player.isCreative() || player.isSpectator())
			{
				return;
			}
		}
		IEntityReaction[] reactions = this.gasType.getEntityReactions();
		for (IEntityReaction r : reactions)
		{
			boolean hasProtected = false;
			for (ItemStack stack : living.getArmorInventoryList())
			{
				if (!stack.isEmpty())
				{
					if (stack.getItem() instanceof IGasEffectProtector prot)
					{
						if (prot.apply(living, r, this.gasType, stack))
						{
							hasProtected = true;
							break;
						}
					}
					else if (CommonEvents.applyGasEffectProtection(living, r, stack))
					{
						hasProtected = true;
						break;
					}
				}
			}
			if (!hasProtected)
			{
				r.react(entity, world, this.gasType, pos);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		gasType.randomDisplayTick(stateIn, worldIn, pos, rand);
	}

	@Override
	public GasType onSample(IBlockAccess access, BlockPos pos, GasType in, EnumFacing side)
	{
		return this.gasType;
	}
}
