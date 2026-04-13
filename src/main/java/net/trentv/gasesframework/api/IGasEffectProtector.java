package net.trentv.gasesframework.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import net.trentv.gasesframework.api.reaction.entity.IEntityReaction;

public interface IGasEffectProtector
{
	boolean apply(EntityLivingBase entity, IEntityReaction reaction, GasType type, BlockPos pos, ItemStack itemstack);
}
