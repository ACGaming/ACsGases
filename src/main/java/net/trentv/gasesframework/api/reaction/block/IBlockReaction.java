package net.trentv.gasesframework.api.reaction.block;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.trentv.gasesframework.api.GasType;

public interface IBlockReaction
{
	void react(Block blockReactive, World world, GasType gasType, BlockPos gasPos, BlockPos scanPos);
}
