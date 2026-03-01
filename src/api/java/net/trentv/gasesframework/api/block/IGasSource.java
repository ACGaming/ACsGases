package net.trentv.gasesframework.api.block;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import net.trentv.gasesframework.api.GasType;

/**
 * An interface for interactivity with gas pipe systems. A gas source can have
 * gas extracted from one or more of its sides.
 */
public interface IGasSource extends IGasInterface
{
	/**
	 * Get (but not extract) the gas type that can be extracted from this side.
	 *
	 * @param world The world object
	 * @param x     X coordinate
	 * @param y     Y coordinate
	 * @param z     Z coordinate
	 * @param side  The local side gas can be extracted from.
	 * @return The gas type that can be extracted from this side, or null
	 */
	GasType getGasTypeFromSide(World world, int x, int y, int z, EnumFacing side);

	/**
	 * Extract the gas type from this side.
	 *
	 * @param world The world object
	 * @param x     X coordinate
	 * @param y     Y coordinate
	 * @param z     Z coordinate
	 * @param side  The local side gas is extracted from.
	 * @return The gas type that was extracted from this side, or null
	 */
	GasType extractGasTypeFromSide(World world, int x, int y, int z, EnumFacing side);
}
