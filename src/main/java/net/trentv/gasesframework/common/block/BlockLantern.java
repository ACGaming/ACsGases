package net.trentv.gasesframework.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.trentv.gasesframework.api.Combustibility;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.block.IGasReceptor;
import net.trentv.gasesframework.api.block.IGasTransporter;
import net.trentv.gasesframework.api.lanterntype.LanternType;
import net.trentv.gasesframework.common.GasesFrameworkObjects;

public class BlockLantern extends Block implements IGasReceptor
{
	public static final PropertyInteger EXPIRATION = PropertyInteger.create("expiration", 0, 15);
	public static final AxisAlignedBB LANTERN_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 10.0D / 16.0D, 0.75D);
	public final LanternType type;

	public BlockLantern(LanternType type)
	{
		super(Material.WOOD);
		this.type = type;
		setHardness(0.25F);
		setLightLevel(type.lightLevel);
		setCreativeTab(type.creativeTab);
		if (type.expirationRate > 0)
		{
			setTickRandomly(true);
		}
		setRegistryName("lantern_" + type.name);
		setTranslationKey(type.getUnlocalizedName());
	}

	public boolean canBlockStay(World world, BlockPos pos)
	{
		return isValidConnection(world, pos.west()) || isValidConnection(world, pos.east()) || isValidConnection(world, pos.down()) || isValidConnection(world, pos.up()) || isValidConnection(world, pos.north()) || isValidConnection(world, pos.south());
	}

	public boolean isValidConnection(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		return block.isOpaqueCube(state) || block instanceof IGasTransporter;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(EXPIRATION, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(EXPIRATION);
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return LANTERN_AABB;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return NULL_AABB;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
	{
		if (type.expirationRate > 0 && random.nextInt(type.expirationRate) == 0)
		{
			int metadata = state.getValue(EXPIRATION) + 1;
			if (metadata >= 16)
			{
				world.setBlockState(pos, GasesFrameworkObjects.getLanternBlock(type.expirationLanternType).getDefaultState());
			}
			else
			{
				world.setBlockState(pos, state.withProperty(EXPIRATION, metadata));
			}
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighbor, BlockPos fromPos)
	{
		if (!world.isRemote && !this.canBlockStay(world, pos))
		{
			for (ItemStack drop : getDrops(world, pos, state, 0))
			{
				spawnAsEntity(world, pos, drop);
			}
			world.setBlockToAir(pos);
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		if (type.expirationLanternType != null)
		{
			return Item.getItemFromBlock(GasesFrameworkObjects.getLanternBlock(type.expirationLanternType));
		}
		else
		{
			return super.getItemDropped(state, rand, fortune);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		return canBlockStay(world, pos);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		ItemStack heldItem = entityPlayer.getHeldItem(hand);
		LanternType replacementType = GasesFrameworkObjects.getLanternTypeByInput(heldItem.getItem());
		ItemStack itemStackOut = new ItemStack(type.itemOut);

		if (replacementType == null)
		{
			replacementType = GasesFrameworkObjects.LANTERN_TYPE_EMPTY;
			if (!entityPlayer.capabilities.isCreativeMode)
			{
				heldItem.shrink(1);
				if (!itemStackOut.equals(ItemStack.EMPTY) && !entityPlayer.inventory.addItemStackToInventory(itemStackOut) && !world.isRemote)
				{
					spawnAsEntity(world, pos, itemStackOut);
				}
			}
		}
		else
		{
			if (world.getBlockState(pos).getBlock() != GasesFrameworkObjects.getLanternBlock(replacementType))
			{
				if (!entityPlayer.capabilities.isCreativeMode)
				{
					heldItem.shrink(1);
					if (!itemStackOut.equals(ItemStack.EMPTY) && !entityPlayer.inventory.addItemStackToInventory(itemStackOut) && !world.isRemote)
					{
						spawnAsEntity(world, pos, itemStackOut);
					}
				}
			}
		}

		world.setBlockState(pos, GasesFrameworkObjects.getLanternBlock(replacementType).getDefaultState());

		return true;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, EXPIRATION);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		ArrayList<ItemStack> ret = new ArrayList<>();

		ret.add(new ItemStack(GasesFrameworkObjects.getLanternBlock(GasesFrameworkObjects.LANTERN_TYPE_EMPTY)));
		if (type.itemOut != null)
		{
			ret.add(new ItemStack(type.itemOut));
		}

		return ret;
	}

	@Override
	public boolean connectToPipe(IBlockAccess blockaccess, int x, int y, int z, EnumFacing side)
	{
		return false;
	}

	@Override
	public boolean receiveGas(World world, int x, int y, int z, EnumFacing side, GasType gasType)
	{
		if (canReceiveGas(world, x, y, z, side, gasType))
		{
			world.setBlockState(new BlockPos(x, y, z), GasesFrameworkObjects.getLanternBlock(GasesFrameworkObjects.LANTERN_TYPES_GAS[gasType.combustability.burnRate]).getDefaultState());
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean canReceiveGas(World world, int x, int y, int z, EnumFacing side, GasType gasType)
	{
		return type == GasesFrameworkObjects.LANTERN_TYPE_GAS_EMPTY && gasType.combustability != Combustibility.NONE;
	}
}
