package net.trentv.gasesframework.common.gastype;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;
import net.trentv.gases.common.configuration.GasesMainConfigurations;
import net.trentv.gasesframework.api.Combustibility;
import net.trentv.gasesframework.api.GFManipulationAPI;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.common.GasesFrameworkObjects;

public class GasTypeFire extends GasType
{
	public GasTypeFire(String name, int color, int opacity, int density, Combustibility combustability)
	{
		super(name, color, opacity, density, combustability);
		setLightLevel(0.5f);
	}

	@Override
	public void postTick(World world, IBlockState state, BlockPos pos)
	{
		if (!world.isRemote)
		{
			if (Blocks.FIRE.canPlaceBlockAt(world, pos))
			{
				world.setBlockState(pos, Blocks.FIRE.getDefaultState());
			}
			else if (GasesMainConfigurations.GASES.SMOKE.smokeAmount > 0 && world.rand.nextInt(4) == 0)
			{
				GFManipulationAPI.addGasLevel(pos, world, GasesFrameworkObjects.SMOKE, GasesMainConfigurations.GASES.SMOKE.smokeAmount);
			}
		}
	}

	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		if (rand.nextInt(12) == 0)
		{
			world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
		}

		int volume = GFManipulationAPI.getGasLevel(pos, world);
		double minY = this.getMinY(world, pos, volume);
		double maxY = this.getMaxY(world, pos, volume);

		if (rand.nextFloat() < maxY - minY)
		{
			double xd = pos.getX() + rand.nextDouble();
			double yd = pos.getY() + minY + rand.nextDouble() * (maxY - minY);
			double zd = pos.getZ() + rand.nextDouble();
			world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, xd, yd, zd, 0.0D, 0.0D, 0.0D);
		}
	}
}
