package net.trentv.gasesframework.api.block;

import net.minecraft.world.World;

import java.util.Random;
import net.trentv.gasesframework.api.GasType;

/**
 * An interface for blocks that can carry and transport gas types, most commonly
 * implemented by pipes.
 */
public interface IGasTransporter extends IGasInterface
{
	/**
	 * Get the type currently carried by this gas transporter.
	 *
	 * @param world The world object
	 * @param x     X coordinate
	 * @param y     Y coordinate
	 * @param z     Z coordinate
	 * @return The gas type carried by this gas transporter
	 */
	GasType getCarriedType(World world, int x, int y, int z);

	/**
	 * Set the type carried by this gas transporter. May change the block at the
	 * coordinate as long as it is still an IGasTransporter
	 *
	 * @param world The world object
	 * @param x     X coordinate
	 * @param y     Y coordinate
	 * @param z     Z coordinate
	 * @param type  The type to carry
	 * @return The block in the position after the type has been set
	 */
	IGasTransporter setCarriedType(World world, int x, int y, int z, GasType type);

	/**
	 * Handle a certain amount of pressure.
	 *
	 * @param world    The world object
	 * @param random   The random object
	 * @param x        X coordinate
	 * @param y        Y coordinate
	 * @param z        Z coordinate
	 * @param pressure The pressure to handle
	 */
	void handlePressure(World world, Random random, int x, int y, int z, int pressure);
}
