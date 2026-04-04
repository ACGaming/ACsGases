package net.trentv.gases.common.worldgen;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;
import net.trentv.gases.common.GasesObjects;
import net.trentv.gases.common.configuration.GasesMainConfigurations;

public class WorldGeneratorFogEmitter implements IWorldGenerator
{
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
	{
		if (world.provider.getDimensionType() != DimensionType.OVERWORLD) return;
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		for (int i = 0; i < GasesMainConfigurations.WORLD_GENERATION.OVERWORLD.CRACKED_BEDROCK.generationChecks; i++)
		{
			int x = (chunkX << 4) + 8 + random.nextInt(8);
			int y = random.nextInt(4);
			int z = (chunkZ << 4) + 8 + random.nextInt(8);
			pos.setPos(x, y, z);
			if (world.getBlockState(pos) == Blocks.BEDROCK)
			{
				world.setBlockState(pos, GasesObjects.WHISPERING_FOG_EMITTER.getDefaultState());
			}
		}
	}
}
