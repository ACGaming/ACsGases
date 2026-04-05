package net.trentv.gases.common.reaction.block;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.trentv.gases.common.configuration.GasesMainConfigurations;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.block.IBlockReaction;

public class BlockReactionCorrosion implements IBlockReaction
{
	@Override
	public void react(Block blockReactive, IBlockAccess access, GasType gas, BlockPos pos)
	{
		if (access instanceof World world && world.rand.nextInt(10) == 0)
		{
			if (blockReactive != Blocks.AIR && world.getBlockState(pos).getBlockHardness(world, pos) <= GasesMainConfigurations.GASES.CORROSIVE_GAS.corrosivePower)
			{
				if (world.rand.nextInt(10) == 0)
				{
					world.destroyBlock(pos, true);
				}
				else
				{
					world.setBlockToAir(pos);
				}
			}
		}

	}
}
