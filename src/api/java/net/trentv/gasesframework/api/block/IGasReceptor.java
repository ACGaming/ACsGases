package net.trentv.gasesframework.api.block;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import net.trentv.gasesframework.api.GasType;

/**
 * An interface for interactivity with gas pipe systems. A gas receptor is able
 * to receive gas.
 */
public interface IGasReceptor extends IGasInterface
{
	/**
	 * Receive a gas through one of this side of the block. Returns true if the
	 * gas type is accepted and consumed.
	 *
	 * @param world   The world object
	 * @param x       X coordinate
	 * @param y       Y coordinate
	 * @param z       Z coordinate
	 * @param side    The local side the received gas is inserted into
	 * @param gasType The type of gas being received
	 * @return True if the block could receive the gas
	 */
	boolean receiveGas(World world, int x, int y, int z, EnumFacing side, GasType gasType);

	/**
	 * Determine if a gas can be received through this side of the block.
	 *
	 * @param world   The world object
	 * @param x       X coordinate
	 * @param y       Y coordinate
	 * @param z       Z coordinate
	 * @param side    The local side the received gas is inserted into
	 * @param gasType The type of gas being received
	 * @return True if the block can receive the gas
	 */
	boolean canReceiveGas(World world, int x, int y, int z, EnumFacing side, GasType gasType);
}
