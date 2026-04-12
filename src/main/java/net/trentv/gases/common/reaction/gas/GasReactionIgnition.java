package net.trentv.gases.common.reaction.gas;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.github.bsideup.jabel.Desugar;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.gas.IGasReaction;

@Desugar
public record GasReactionIgnition(GasType match) implements IGasReaction
{
	@Override
	public void react(GasType gasA, World world, GasType gasB, BlockPos gasAPos, BlockPos gasBPos)
	{
		if (gasB == match)
		{
			gasA.ignite(world, gasAPos);
		}
	}
}
