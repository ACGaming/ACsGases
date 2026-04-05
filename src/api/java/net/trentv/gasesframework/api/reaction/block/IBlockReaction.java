package net.trentv.gasesframework.api.reaction.block;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.trentv.gasesframework.api.GasType;

public interface IBlockReaction
{
	void react(Block blockReactive, IBlockAccess access, GasType gas, BlockPos pos);
}
