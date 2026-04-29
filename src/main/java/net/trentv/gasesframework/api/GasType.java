package net.trentv.gasesframework.api;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.trentv.gasesframework.api.reaction.block.IBlockReaction;
import net.trentv.gasesframework.api.reaction.entity.IEntityReaction;
import net.trentv.gasesframework.api.reaction.gas.IGasReaction;
import net.trentv.gasesframework.common.block.BlockGas;

public class GasType
{
	// Should be turned into a ForgeRegistry at some point.
	private static int maxGasID = 0;

	// Mandatory fields
	public final int gasID;
	public final String name;
	public final int color;
	public final int opacity;
	public final Combustibility combustability;
	public final int density;

	private ResourceLocation registryName;

	public Block block;
	public ItemBlock itemBlock;
	@SideOnly(Side.CLIENT)
	private GasColor gasColor;

	// Optional or circumstantial fields.
	public int dissipationRate = 0;
	public int dissipationAmount = 0;
	public int cohesion = 16;
	public float lightLevel = 0.0F;
	public CreativeTabs creativeTab;
	public boolean tintindex = true;

	public List<IBlockReaction> blockReactions = new ArrayList<>();
	public List<IEntityReaction> entityReactions = new ArrayList<>();
	public List<IGasReaction> gasReactions = new ArrayList<>();

	public GasType(String name, int color, int opacity, int density, Combustibility combustability)
	{
		if (!(opacity >= 0 & opacity <= 16))
		{
			opacity = 0;
			if (GasesFrameworkAPI.LOGGER != null)
				GasesFrameworkAPI.LOGGER.error("Incorrect opacity value for GasType: " + name + "! Valid values are [0-16]. Setting opacity to 0.");
		}
		this.gasID = maxGasID++;
		this.name = name;
		this.color = color;
		this.opacity = opacity;
		this.density = density;
		this.combustability = combustability;
	}

	public GasType setRegistryName(ResourceLocation location)
	{
		if (registryName == null)
		{
			registryName = location;
		}
		else
		{
			GasesFrameworkAPI.LOGGER.error("Trying to set registry name multiple times for gastype: " + registryName);
		}
		return this;
	}

	public GasType setTintIndex(boolean tintindex)
	{
		this.tintindex = tintindex;
		return this;
	}

	public GasType setDissipation(int dissipationRate, int dissipationAmount)
	{
		this.dissipationRate = dissipationRate;
		this.dissipationAmount = dissipationAmount;
		return this;
	}

	public GasType setCohesion(int cohesion)
	{
		this.cohesion = cohesion;
		return this;
	}

	public GasType setLightLevel(float lightLevel)
	{
		this.lightLevel = lightLevel;
		return this;
	}

	public GasType setCreativeTab(CreativeTabs creativeTab)
	{
		this.creativeTab = creativeTab;
		return this;
	}

	public ResourceLocation getRegistryName()
	{
		return registryName;
	}

	@SideOnly(Side.CLIENT)
	public GasColor getGasColor()
	{
		if (gasColor == null)
			gasColor = new GasColor(this);
		return gasColor;
	}

	@SideOnly(Side.CLIENT)
	public class GasColor implements IBlockColor, IItemColor
	{
		public GasType master;

		public GasColor(GasType master)
		{
			this.master = master;
		}

		@Override
		public int colorMultiplier(ItemStack stack, int tintIndex)
		{
			return master.color;
		}

		@Override
		public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex)
		{
			return master.color;
		}
	}

	public GasType registerBlockReaction(IBlockReaction... a)
	{
		blockReactions.addAll(Arrays.asList(a));
		return this;
	}

	public IBlockReaction[] getBlockReactions()
	{
		return blockReactions.toArray(new IBlockReaction[blockReactions.size()]);
	}

	public GasType registerEntityReaction(IEntityReaction... a)
	{
		entityReactions.addAll(Arrays.asList(a));
		return this;
	}

	public IEntityReaction[] getEntityReactions()
	{
		return entityReactions.toArray(new IEntityReaction[entityReactions.size()]);
	}

	public GasType registerGasReaction(IGasReaction... a)
	{
		gasReactions.addAll(Arrays.asList(a));
		return this;
	}

	public IGasReaction[] getGasReactions()
	{
		return gasReactions.toArray(new IGasReaction[gasReactions.size()]);
	}

	// Return false if you want to stop the rest of the tick processing.
	// Be sure to manually reschedule ticks
	public boolean preTick(World world, IBlockState state, BlockPos pos)
	{
		return true;
	}

	public void postTick(World world, IBlockState state, BlockPos pos)
	{

	}

	public boolean requiresNewTick(World world, IBlockState state, BlockPos pos)
	{
		return true;
	}

	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{

	}

	public boolean ignite(World world, BlockPos pos)
	{
		if (this.combustability.isFlammable() && this.block instanceof BlockGas blockGas)
		{
			blockGas.ignite(pos, world);
			return true;
		}
		return false;
	}

	public double getMinY(World world, BlockPos pos, int volume)
	{
		if (density > 0)
		{
			return 0.0D;
		}
		else if (density < 0)
		{
			return 1.0D - volume / 16.0D;
		}
		else
		{
			if (GFManipulationAPI.getGasType(pos.down(), world) == this)
			{
				return 0.0D;
			}
			boolean b = GFManipulationAPI.getGasType(pos.up(), world) == this;
			double d = (0.5D - volume / 8.0D) * (b ? 2.0D : 1.0D);
			return Math.max(d, 0.0D);
		}
	}

	public double getMaxY(World world, BlockPos pos, int volume)
	{
		if (density > 0)
		{
			return volume / 16.0D;
		}
		else if (density < 0)
		{
			return 1.0D;
		}
		else
		{
			if (GFManipulationAPI.getGasType(pos.up(), world) == this)
			{
				return 1.0D;
			}
			boolean b = GFManipulationAPI.getGasType(pos.down(), world) == this;
			double d = 1.0D - (0.5D - volume / 8.0D) * (b ? 2.0D : 1.0D);
			return Math.min(d, 1.0D);
		}
	}
}
