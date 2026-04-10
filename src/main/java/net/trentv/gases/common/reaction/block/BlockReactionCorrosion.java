package net.trentv.gases.common.reaction.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.trentv.gases.common.configuration.GasesMainConfigurations;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.block.IBlockReaction;
import net.trentv.gasesframework.common.block.BlockGas;

public class BlockReactionCorrosion implements IBlockReaction
{
	@Override
	public void react(Block blockReactive, IBlockAccess access, GasType gasType, BlockPos gasPos, BlockPos scanPos)
	{
		if (blockReactive instanceof BlockAir || blockReactive instanceof BlockGas) return;
		if (access instanceof World world && world.rand.nextInt(10) == 0)
		{
			IBlockState state = world.getBlockState(scanPos);
			if (state.getBlockHardness(world, scanPos) <= GasesMainConfigurations.GASES.CORROSIVE_GAS.corrosivePower)
			{
				world.destroyBlock(scanPos, world.rand.nextInt(10) == 0);
			}
		}
	}
}
