package net.trentv.gases.common.reaction.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockTorch;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.block.IBlockReaction;

public class BlockReactionExtinguish implements IBlockReaction
{
	@Override
	public void react(Block blockReactive, IBlockAccess access, GasType gasType, BlockPos gasPos, BlockPos scanPos)
	{
		if (access instanceof World world)
		{
			if (blockReactive instanceof BlockTorch)
			{
				world.destroyBlock(scanPos, true);
			}
			else if (blockReactive instanceof BlockFire)
			{
				world.playEvent(null, 1009, scanPos, 0);
				world.setBlockToAir(scanPos);
			}
		}
	}
}
