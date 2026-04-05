package net.trentv.gasesframework.api.block;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

/**
 * An abstract base interface which many machinery related interfaces extend.
 * Implementing this interface is unnecessary.
 */
public interface IGasInterface
{
	/**
	 * Can a pipe connect to this side of the block?
	 *
	 * @param blockAccess The block access
	 * @param x           X coordinate
	 * @param y           Y coordinate
	 * @param z           Z coordinate
	 * @param side        The local side of this block the pipe can connect to.
	 * @return True if a pipe can connect to this side of the block
	 */
	boolean connectToPipe(IBlockAccess blockAccess, int x, int y, int z, EnumFacing side);
}
