package net.trentv.gasesframework.api.reaction.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.capability.IGasEffects;

public class EntityReactionBlindness implements IEntityReaction
{
	@CapabilityInject(IGasEffects.class)
	public static Capability<IGasEffects> GAS_EFFECTS;

	public float blindnessRate;

	public EntityReactionBlindness(float blindnessRate)
	{
		this.blindnessRate = blindnessRate;
	}

	@Override
	public void react(Entity e, World world, GasType gas, BlockPos pos)
	{
		if (e.hasCapability(GAS_EFFECTS, null))
		{
			IGasEffects q = e.getCapability(GAS_EFFECTS, null);
			if (new BlockPos(e.getPositionEyes(0)).toLong() == pos.toLong())
			{
				if (!world.isAirBlock(new BlockPos(e.getPositionEyes(0))))
				{
					if (q.getBlindness() < 250 - blindnessRate)
					{
						q.setBlindness(q.getBlindness() + blindnessRate);
					}
				}
			}
		}
	}
}
