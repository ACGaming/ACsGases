package net.trentv.gasesframework.api.gasworldgentype;

import net.minecraft.world.World;

import net.trentv.gasesframework.api.GasType;

public abstract class GasWorldGenType
{
    /**
     * A name for this gas world gen type. Must be unique.
     */
    public final String name;
    /**
     * The gas type to be placed by this world generator.
     */
    public final GasType gasType;
    /**
     * The average amount of pockets/clouds per 16x16x16 chunk of blocks.
     */
    public final float generationFrequency;
    /**
     * The average number of gas blocks of the gas pockets/clouds this will
     * generate.
     */
    public final float averageVolume;
    /**
     * The evenness of the gas pocket/cloud. 0.0f gives very uneven gas
     * pockets/clouds. 1.0f gives completely round gas pockets/clouds.
     */
    public final float evenness;
    /**
     * The minimal y coordinate of a gas pocket/cloud.
     */
    public final int minY;
    /**
     * The maximal y coordinate of a gas pocket/cloud.
     */
    public final int maxY;

    /**
     * Creates a new gas world gen type. Gas world gen types are necessary for
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
    public GasWorldGenType(String name, GasType gasType, float generationFrequency, float averageVolume, float evenness, int minY, int maxY)
    {
        this.name = name;
        this.gasType = gasType;
        this.generationFrequency = generationFrequency;
        this.averageVolume = averageVolume;
        this.evenness = evenness < 0.0f ? 0.0f : (Math.min(evenness, 1.0f));
        this.minY = minY;
        this.maxY = maxY;
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
    public abstract int getPlacementVolume(World world, int x, int y, int z, float placementScore);
}
