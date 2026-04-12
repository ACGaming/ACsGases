package net.trentv.gases.common.reaction.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.block.IBlockReaction;

public class BlockReactionWaterIgnition implements IBlockReaction
{
	@Override
	public void react(Block blockReactive, World world, GasType gasType, BlockPos gasPos, BlockPos scanPos)
	{
		if (world.getBlockState(scanPos).getMaterial() == Material.WATER)
		{
			gasType.ignite(world, gasPos);
		}
	}
}
