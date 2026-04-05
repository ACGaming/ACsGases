package net.trentv.gases.common.reaction.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Random;
import net.trentv.gases.GasesRegistry;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.entity.IEntityReaction;

public class EntityReactionRustItems implements IEntityReaction
{
	@Override
	public void react(Entity e, IBlockAccess access, GasType gas, BlockPos pos)
	{
		if (!e.world.isRemote)
		{
			if (e instanceof EntityPlayer playerEntity)
			{
				InventoryPlayer inventory = playerEntity.inventory;
				for (int i = 0; i < 4; i++)
				{
					ItemStack itemstack = inventory.armorItemInSlot(i);
					inventory.armorInventory.set(i, tryRustItem(itemstack, e.world.rand));
				}
			}
			else if (e instanceof EntityItem itemEntity)
			{
				ItemStack itemStack = itemEntity.getItem();
				ItemStack newItemStack = tryRustItem(itemStack, e.world.rand);

				if (newItemStack != null)
				{
					itemEntity.setItem(newItemStack);
				}
				else
				{
					itemEntity.setDead();
				}
			}
		}
	}

	public ItemStack tryRustItem(ItemStack itemstack, Random random)
	{
		if (itemstack == null)
		{
			return null;
		}

		Item item = itemstack.getItem();
		Item replacementItem = GasesRegistry.getRustedItem(item);
		if (replacementItem != null)
		{
			return new ItemStack(replacementItem, itemstack.getCount(), itemstack.getItemDamage());
		}
		else
		{
			if (canRustDamageItem(item))
			{
				if (itemstack.attemptDamageItem(1, random, null))
				{
					return null;
				}
			}
		}

		if (itemstack.getCount() > 0)
		{
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	public boolean canRustDamageItem(Item item)
	{
		String material = null;
		if (item instanceof ItemArmor)
		{
			material = ((ItemArmor) item).getArmorMaterial().toString();
		}
		else if (item instanceof ItemTool)
		{
			material = ((ItemTool) item).getToolMaterialName();
		}
		else if (item instanceof ItemSword)
		{
			material = ((ItemSword) item).getToolMaterialName();
		}

		if (material != null)
		{
			return GasesRegistry.isRustableMaterial(material);
		}
		else
		{
			return false;
		}
	}
}
