package net.trentv.gases.common.gasworldgentype;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;
import net.trentv.gases.common.GasesObjects;
import net.trentv.gases.common.configuration.GasesMainConfigurations;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.gasworldgentype.GasWorldGenPocket;

public class GasWorldGenDiabalinePocket extends GasWorldGenPocket
{
    private static final Random random = new Random();

    public GasWorldGenDiabalinePocket(String name, GasType gasType, float generationFrequency, float averageVolume, float evenness, int minY, int maxY, Object... replaceBlocks)
    {
        super(name, gasType, generationFrequency, averageVolume, evenness, minY, maxY, replaceBlocks);
    }

    /**
     * Get the volume of gas placed at this location, if any. Must be a number between 0 and 16.
     *
     * @param placementScore - The greater this value is, the more central this block of gas is.
     */
    @Override
    public int getPlacementVolume(World world, int x, int y, int z, float placementScore)
    {
        int result = super.getPlacementVolume(world, x, y, z, placementScore);
        BlockPos pos = new BlockPos(x, y, z);
        if (result == 0 && replaceBlocks.contains(world.getBlockState(pos).getBlock()))
        {
            if (random.nextFloat() < GasesMainConfigurations.WORLD_GENERATION.DIABALINE_ORE.commonness)
            {
                world.setBlockState(pos, GasesObjects.DIABALINE_ORE.getDefaultState());
            }
        }

        return result;
    }
}
