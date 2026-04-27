package net.trentv.gases.common.gastype;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;
import net.trentv.gases.common.sound.SoundEvents;
import net.trentv.gasesframework.api.Combustibility;
import net.trentv.gasesframework.api.GasType;

public class GasTypeWhisperingFog extends GasType
{
	public GasTypeWhisperingFog(String name, int color, int opacity, int density, Combustibility combustability)
	{
		super(name, color, opacity, density, combustability);
	}

	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		if (rand.nextInt(60) == 0)
		{
			world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.EFFECT_GAS_WHISPER.getSoundEvent(), SoundCategory.BLOCKS, 1.0f, 1.0f, false);
			world.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat(), 0.0D, 0.0D, 0.0D);
		}
	}
}
