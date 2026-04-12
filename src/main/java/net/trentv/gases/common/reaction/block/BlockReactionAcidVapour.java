package net.trentv.gases.common.reaction.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.trentv.gases.common.GasesObjects;
import net.trentv.gasesframework.api.GFManipulationAPI;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.block.IBlockReaction;

public class BlockReactionAcidVapour implements IBlockReaction
{
	@Override
	public void react(Block blockReactive, World world, GasType gasType, BlockPos gasPos, BlockPos scanPos)
	{
		if (world.getBlockState(scanPos).getMaterial() == Material.WATER)
		{
			int level = GFManipulationAPI.getGasLevel(gasPos, world);
			GFManipulationAPI.setGasLevel(gasPos, world, GasesObjects.ACID_VAPOUR, level);
		}
	}
}
