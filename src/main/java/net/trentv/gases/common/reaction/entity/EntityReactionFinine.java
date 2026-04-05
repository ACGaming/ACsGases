package net.trentv.gases.common.reaction.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.trentv.gases.common.configuration.GasesMainConfigurations;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.entity.IEntityReaction;

public class EntityReactionFinine implements IEntityReaction
{
	@Override
	public void react(Entity e, IBlockAccess access, GasType gas, BlockPos pos)
	{
		BlockPos originalPosition = e.getPosition();
		BlockPos newPosition;
		int iterations = 0;
		do
		{
			iterations++;
			newPosition = new BlockPos(originalPosition);

			int newX = e.world.rand.nextInt(16) - 8;
			int newY = e.world.rand.nextInt(16) - 8;
			int newZ = e.world.rand.nextInt(16) - 8;
			newPosition = newPosition.add(newX, newY, newZ);

		} while (iterations < GasesMainConfigurations.GASES.FININE.maxTeleportSearches & (access.isAirBlock(pos) & access.isAirBlock(pos.up())));

		e.setPositionAndRotation(newPosition.getX(), newPosition.getY(), newPosition.getZ(), e.world.rand.nextInt(360), e.world.rand.nextInt(180) - 90);
	}
}
