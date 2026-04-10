package net.trentv.gases.common.reaction.block;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.block.IBlockReaction;

public class BlockReactionWaterIgnition implements IBlockReaction
{
	@Override
	public void react(Block blockReactive, IBlockAccess access, GasType gasType, BlockPos gasPos, BlockPos scanPos)
	{
		if (access instanceof World world && (blockReactive == Blocks.WATER || blockReactive == Blocks.FLOWING_WATER))
		{
			gasType.ignite(world, gasPos);
		}
	}
}
