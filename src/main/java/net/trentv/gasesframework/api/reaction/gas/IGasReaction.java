package net.trentv.gasesframework.api.reaction.gas;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.trentv.gasesframework.api.GasType;

public interface IGasReaction
{
	void react(GasType gasA, World world, GasType gasB, BlockPos gasAPos, BlockPos gasBPos);
}
