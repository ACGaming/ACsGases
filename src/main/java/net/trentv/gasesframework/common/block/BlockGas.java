package net.trentv.gasesframework.common.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.trentv.gases.common.configuration.GasesMainConfigurations;
import net.trentv.gasesframework.GasesFramework;
import net.trentv.gasesframework.api.*;
import net.trentv.gasesframework.api.reaction.block.IBlockReaction;
import net.trentv.gasesframework.api.reaction.entity.IEntityReaction;
import net.trentv.gasesframework.api.reaction.gas.IGasReaction;
import net.trentv.gasesframework.api.sample.ISample;
import net.trentv.gasesframework.common.CommonEvents;
import net.trentv.gasesframework.common.GasesFrameworkObjects;
import net.trentv.gasesframework.common.item.ItemGasBottle;

public class BlockGas extends Block implements ISample
{
	public static final PropertyInteger CAPACITY = PropertyInteger.create("capacity", 1, 16);
	public final GasType gasType;

	public BlockGas(GasType type)
	{
		super(MaterialGas.INSTANCE);
		this.gasType = type;
		disableStats();
		setBlockUnbreakable();
		setLightOpacity(type.opacity);
		setCreativeTab(type.creativeTab);
		setResistance(0);
		setTranslationKey(GasesFramework.MODID + ".gas_" + type.name);
		setDefaultState(blockState.getBaseState().withProperty(CAPACITY, 16));
	}

	// Block & block state

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		world.scheduleBlockUpdate(pos, this, GasesMainConfigurations.GASES.tickRate + RANDOM.nextInt(6), 1);
	}

	@Override
	public void updateTick(World world, BlockPos currentPosition, IBlockState state, Random rand)
	{
		if (!gasType.preTick(world, state, currentPosition)) return;

		// Reaction scan and ignition check
		for (EnumFacing facing : EnumFacing.VALUES)
		{
			BlockPos scanPos = currentPosition.offset(facing);
			IBlockState scanState = world.getBlockState(scanPos);
			Block scanBlock = scanState.getBlock();

			for (IBlockReaction r : gasType.getBlockReactions())
			{
				r.react(scanBlock, world, gasType, currentPosition, scanPos);
			}
			if (scanBlock instanceof BlockGas neighborGas)
			{
				for (IGasReaction r : gasType.getGasReactions())
				{
					r.react(gasType, world, neighborGas.gasType, currentPosition, scanPos);
				}
			}
			if (gasType.combustability != Combustibility.NONE && GFRegistrationAPI.isIgnitionSource(scanBlock.getDefaultState()))
			{
				ignite(currentPosition, world);
				return;
			}
		}

		// Dissipation, re-read state afterward so thisValue is current
		if (gasType.dissipationRate > 0 && rand.nextInt(16) < gasType.dissipationRate)
		{
			int newAmount = state.getValue(CAPACITY) - gasType.dissipationAmount;
			GFManipulationAPI.setGasLevel(currentPosition, world, gasType, newAmount);
			state = world.getBlockState(currentPosition);
			if (!(state.getBlock() instanceof BlockGas)) return;
		}

		int thisValue = state.getValue(CAPACITY);

		// If density is 0, we're going to be spreading out in a cloud
		if (gasType.density == 0)
		{
			// Iterate through all directions (up/down/left/right/front/back)
			for (EnumFacing facing : EnumFacing.VALUES)
			{
				BlockPos flowToBlock = currentPosition.offset(facing);
				// Checks if it can flow into the block AND the current gas capacity is over the cohesion level
				if (GFManipulationAPI.canPlaceGas(flowToBlock, world, gasType) && thisValue > gasType.cohesion)
				{
					int flowValue = GFManipulationAPI.getGasLevel(flowToBlock, world);
					if (flowValue + 1 < thisValue)
					{
						GFManipulationAPI.addGasLevel(flowToBlock, world, gasType, 1);
						thisValue--;
						GFManipulationAPI.setGasLevel(currentPosition, world, gasType, thisValue);
					}
				}
			}
		}
		// We're going to be flowing either up or down here because density != 0
		else
		{
			// Decide which direction we're going
			EnumFacing direction = gasType.density > 0 ? EnumFacing.UP : EnumFacing.DOWN;
			BlockPos nextPosition = scanForOpenBlock(world, this, currentPosition, direction);

			if (!nextPosition.equals(currentPosition)) // In this case, we'll be flowing somewhere above or below.
			{
				int remaining = GFManipulationAPI.addGasLevel(nextPosition, world, gasType, thisValue);
				if (thisValue != remaining)
				{
					GFManipulationAPI.setGasLevel(currentPosition, world, gasType, remaining);
				}
			}
			else if (thisValue > 1) // Can't flow above or below, so time to spill out on the ground
			{
				EnumFacing newDir = EnumFacing.SOUTH;
				for (int i = 0; i < 4; i++)
				{
					newDir = newDir.rotateY();
					BlockPos flowToBlock = nextPosition.offset(newDir);
					if (GFManipulationAPI.canPlaceGas(flowToBlock, world, gasType))
					{
						int flowValue = GFManipulationAPI.getGasLevel(flowToBlock, world);
						if (flowValue + 1 < thisValue)
						{
							GFManipulationAPI.addGasLevel(flowToBlock, world, gasType, 1);
							thisValue--;
							GFManipulationAPI.setGasLevel(nextPosition, world, gasType, thisValue);
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			ItemStack held = player.getHeldItem(hand);
			if (held.getItem() != Items.GLASS_BOTTLE) return false;
			int capacity = state.getValue(CAPACITY);
			if (capacity < 8) return false;
			// Conditions met, bottling...
			if (!player.capabilities.isCreativeMode)
			{
				held.shrink(1);
			}
			ItemStack filledBottle = new ItemStack(GasesFrameworkObjects.GAS_BOTTLE);
			ItemGasBottle.setGasType(filledBottle, gasType);
			if (!player.inventory.addItemStackToInventory(filledBottle))
			{
				player.dropItem(filledBottle, false);
			}
			// Reduce gas block capacity
			int newCapacity = capacity - 8;
			if (newCapacity <= 0)
			{
				world.setBlockToAir(pos);
			}
			else
			{
				GFManipulationAPI.setGasLevel(pos, world, gasType, newCapacity);
			}
			world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.BLOCKS, 0.6F, 2.0F);
			return true;
		}
		return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, CAPACITY);
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		double height = (double) state.getValue(CAPACITY) / 16;
		if (gasType.density > 0) return new AxisAlignedBB(0.0D, 1.0D - height, 0.0D, 1.0D, 1.0D, 1.0D);
		return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, height, 1.0D);
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos)
	{
		return NULL_AABB;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess world, BlockPos pos)
	{
		return true;
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

	// TODO: Caveats?
	@Override
	public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return true;
	}

	@Override
	public boolean isReplaceable(IBlockAccess blockAccess, BlockPos pos)
	{
		if (blockAccess instanceof World world)
		{
			for (EnumFacing facing : EnumFacing.VALUES)
			{
				BlockPos scanPos = pos.offset(facing);
				if (GFManipulationAPI.canPlaceGas(scanPos, world, gasType))
				{
					GFManipulationAPI.addGasLevel(scanPos, world, gasType, world.getBlockState(pos).getValue(CAPACITY));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}

	// Client Side

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		if (blockAccess.getBlockState(pos.offset(side)).getMaterial() == this.material)
		{
			return false;
		}
		else
		{
			return side == EnumFacing.UP || super.shouldSideBeRendered(blockState, blockAccess, pos, side);
		}
	}

	@SideOnly(Side.CLIENT)
	public float getAmbientOcclusionLightValue(IBlockState state)
	{
		return 1.0F;
	}

	// Gases relevant

	public void ignite(BlockPos pos, World world)
	{
		if (!(world instanceof WorldServer worldServer)) return;
		IBlockState state = world.getBlockState(pos);
		if (!(state.getBlock() instanceof BlockGas)) return;

		int capacity = state.getValue(CAPACITY);
		if (gasType.combustability.explosionPower > 0)
		{
			float explosionPower = (capacity / 16.0f) * gasType.combustability.explosionPower * 3;
			CommonEvents.scheduleExplosion(worldServer, pos, explosionPower, 5);
			GFManipulationAPI.setGasLevel(pos, world, GasesFrameworkAPI.AIR, 16);
		}
		if (gasType.combustability.fireSpreadRate > 0)
		{
			GFManipulationAPI.setGasLevel(pos, world, GasesFrameworkObjects.FIRE, capacity);
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
		return new ArrayList<>();
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		if (!(entity instanceof EntityLivingBase living)) return;
		if (living instanceof EntityPlayer player && (player.isCreative() || player.isSpectator())) return;
		IEntityReaction[] reactions = gasType.getEntityReactions();
		int protectedCount = 0;
		for (IEntityReaction r : reactions)
		{
			boolean hasProtected = false;
			if (protectedCount == 0)
			{
				for (ItemStack stack : living.getArmorInventoryList())
				{
					IGasEffectProtector prot = CommonEvents.getProtector(stack);
					if (prot != null && prot.apply(living, r, gasType, pos, stack))
					{
						hasProtected = true;
						++protectedCount;
						break;
					}
				}
			}
			if (!hasProtected)
			{
				r.react(entity, world, gasType, pos);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		gasType.randomDisplayTick(state, world, pos, rand);
	}

	@Override
	public GasType onSample(IBlockAccess access, BlockPos pos, GasType gas, EnumFacing side)
	{
		return gasType;
	}
}
