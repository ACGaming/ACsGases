package net.trentv.gases.common.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import net.trentv.gases.Gases;
import net.trentv.gases.common.configuration.GasesMainConfigurations;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.IGasEffectProtector;
import net.trentv.gasesframework.api.reaction.entity.IEntityReaction;

public class ItemRespirator extends ItemArmor implements IGasEffectProtector
{
	private final List<Class<? extends IEntityReaction>> blockedReactions;
	private final Item repairMaterial;

	public ItemRespirator(List<Class<? extends IEntityReaction>> list, ArmorMaterial material, String name, Item repairMaterial)
	{
		super(material, 1, EntityEquipmentSlot.HEAD);
		setCreativeTab(Gases.CREATIVE_TAB);
		this.blockedReactions = list;
		this.repairMaterial = repairMaterial;
		setRegistryName(Gases.MODID, name);
		setTranslationKey(name);
	}

	@Override
	public boolean apply(EntityLivingBase entity, IEntityReaction reaction, GasType type, BlockPos pos, ItemStack itemstack)
	{
		int headY = (int) (entity.posY + entity.getEyeHeight());
		if (pos.getY() == headY && blockedReactions.contains(reaction.getClass()))
		{
			if (!entity.world.isRemote && itemstack.isItemStackDamageable() && entity.world.getWorldTime() % GasesMainConfigurations.GASES.respiratorDamageRate == 0)
			{
				itemstack.damageItem(GasesMainConfigurations.GASES.respiratorDamageAmount, entity);
			}
			return true;
		}
		return false;
	}

	/**
	 * Return whether this item is repairable in an anvil.
	 */
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
	{
		return (repair.getItem() == repairMaterial);
	}
}
