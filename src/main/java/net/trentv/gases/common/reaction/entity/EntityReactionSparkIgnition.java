package net.trentv.gases.common.reaction.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.entity.IEntityReaction;
import net.trentv.gasesframework.common.entity.EntityFlashSparkFX;

public class EntityReactionSparkIgnition implements IEntityReaction
{
	@Override
	public void react(Entity e, IBlockAccess access, GasType gas, BlockPos pos)
	{
		if (access instanceof World world && e instanceof EntityFlashSparkFX)
		{
			gas.ignite(world, access.getBlockState(pos), pos);
		}
	}
}
