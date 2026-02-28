package net.trentv.gasesframework.api.gasworldgentype;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.trentv.gasesframework.api.GasType;

public class GasWorldGenCloud extends GasWorldGenType
{
    /**
     * Creates a new gas world gen cloud. Gas world gen types are necessary for
     * adding gases to the terrain. If the Gases Framework retrogen is enabled,
     * this type will be generated in chunks where it has not previously been
     * generated.
     *
     * @param name                A name for this gas world gen type. Must be unique
     * @param gasType             The gas type to be placed by this world generator
     * @param generationFrequency The average amount of pockets/clouds per 16x16x16 chunk of
     *                            blocks
     * @param averageVolume       The average number of gas blocks of the gas pockets/clouds
     *                            this will generate
     * @param evenness            The evenness of the gas pocket/cloud. 0.0f gives very uneven
     *                            gas pockets/clouds. 1.0f gives completely round gas
     *                            pockets/clouds
     * @param minY                The minimal y coordinate of a gas pocket/cloud
     * @param maxY                The maximal y coordinate of a gas pocket/cloud
     */
    public GasWorldGenCloud(String name, GasType gasType, float generationFrequency, float averageVolume, float evenness, int minY, int maxY)
    {
        super(name, gasType, generationFrequency, averageVolume, evenness, minY, maxY);
    }

    /**
     * Get the volume of gas placed at this location, if any. Must be a number
     * between 0 and 16
     *
     * @param world          The world object
     * @param x              X coordinate
     * @param y              Y coordinate
     * @param z              Z coordinate
     * @param placementScore The greater this value is, the more central this block of gas
     *                       is.
     * @return The volume of gas to place
     */
    @Override
    public int getPlacementVolume(World world, int x, int y, int z, float placementScore)
    {
        return world.isAirBlock(new BlockPos(x, y, z)) ? 16 : 0;
    }
}
