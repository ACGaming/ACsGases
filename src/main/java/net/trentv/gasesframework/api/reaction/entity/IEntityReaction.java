package net.trentv.gasesframework.api.reaction.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.trentv.gasesframework.api.GasType;

public interface IEntityReaction
{
	void react(Entity e, World world, GasType gas, BlockPos pos);
}
