package net.trentv.gases.common.reaction.gas;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.gas.IGasReaction;

public class GasReactionIgnition implements IGasReaction
{
	public final GasType match;

	public GasReactionIgnition(GasType match)
	{
		this.match = match;
	}

	@Override
	public void react(GasType gasA, IBlockAccess access, GasType gasB, BlockPos gasAPos, BlockPos gasBPos)
	{
		if (access instanceof World world && gasB == match)
		{
			gasA.ignite(world, gasAPos);
		}
	}
}
