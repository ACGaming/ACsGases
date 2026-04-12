package net.trentv.gases.common.reaction.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.entity.IEntityReaction;

public class EntityReactionIgnition implements IEntityReaction
{
	@Override
	public void react(Entity e, World world, GasType gas, BlockPos pos)
	{
		if (e.isBurning())
		{
			gas.ignite(world, pos);
		}
	}
}
