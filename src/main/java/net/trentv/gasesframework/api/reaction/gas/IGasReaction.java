package net.trentv.gasesframework.api.reaction.gas;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.trentv.gasesframework.api.GasType;

public interface IGasReaction
{
	void react(GasType gasA, IBlockAccess access, GasType gasB, BlockPos gasAPos, BlockPos gasBPos);
}
