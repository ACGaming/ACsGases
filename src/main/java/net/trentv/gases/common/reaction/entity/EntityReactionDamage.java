package net.trentv.gases.common.reaction.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.entity.IEntityReaction;

public class EntityReactionDamage implements IEntityReaction
{
	private final DamageSource source;
	private final float damage;

	public EntityReactionDamage(DamageSource source, float damage)
	{
		this.source = source;
		this.damage = damage;
	}

	@Override
	public void react(Entity e, IBlockAccess access, GasType gas, BlockPos pos)
	{
		e.attackEntityFrom(source, damage);
	}
}
