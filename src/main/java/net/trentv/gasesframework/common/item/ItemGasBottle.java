package net.trentv.gasesframework.common.item;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import net.trentv.gasesframework.GasesFramework;
import net.trentv.gasesframework.api.*;

// Gas in a bottle, gas in a bottle
// Don't need gas when it comes in a bottle
public class ItemGasBottle extends Item
{
	private static final String NBT_GAS = "Gas";

	public static ItemStack setGasType(ItemStack stack, GasType gasType)
	{
		if (gasType == null || gasType == GasesFrameworkAPI.AIR) return stack;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) nbt = new NBTTagCompound();
		nbt.setString(NBT_GAS, gasType.getRegistryName().toString());
		stack.setTagCompound(nbt);
		return stack;
	}

	public static GasType getGasType(ItemStack stack)
	{
		if (!stack.hasTagCompound()) return null;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null || !nbt.hasKey(NBT_GAS, 8)) return null;
		String name = nbt.getString(NBT_GAS);
		return GFRegistrationAPI.getGasType(new ResourceLocation(name));
	}

	public ItemGasBottle()
	{
		super();
		setRegistryName(GasesFramework.MODID, "gas_bottle");
		setCreativeTab(GasesFramework.CREATIVE_TAB);
		setTranslationKey(GasesFramework.MODID + ".gas_bottle");
		setMaxStackSize(16);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote)
		{
			GasType gasType = getGasType(stack);
			if (gasType == null) return new ActionResult<>(EnumActionResult.PASS, stack);
			RayTraceResult rayTraceResult = rayTrace(world, player, false);
			if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) return new ActionResult<>(EnumActionResult.PASS, stack);
			BlockPos pos = rayTraceResult.getBlockPos();
			EnumFacing side = rayTraceResult.sideHit;
			BlockPos placePos = pos.offset(side);
			if (!player.canPlayerEdit(placePos, side, stack)) return new ActionResult<>(EnumActionResult.FAIL, stack);
			if (!GFManipulationAPI.canPlaceGas(placePos, world, gasType)) return new ActionResult<>(EnumActionResult.FAIL, stack);
			int levelNew = GFManipulationAPI.getGasLevel(placePos, world) + 8;
			if (levelNew <= 16)
			{
				GFManipulationAPI.setGasLevel(placePos, world, gasType, levelNew);
				if (!player.capabilities.isCreativeMode)
				{
					ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
					if (stack.getCount() > 1)
					{
						stack.shrink(1);
						if (!player.inventory.addItemStackToInventory(emptyBottle))
						{
							player.dropItem(emptyBottle, false);
						}
					}
					else
					{
						player.setHeldItem(hand, emptyBottle);
					}
				}
				world.playSound(null, placePos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 0.6F, 1.0F);
				world.playSound(null, placePos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 0.4F, 0.2F);
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			}
		}
		return new ActionResult<>(EnumActionResult.PASS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		super.addInformation(stack, world, tooltip, flag);
		GasType gasType = getGasType(stack);
		if (gasType == null) return;
		tooltip.add(I18n.format(gasType.block.getTranslationKey() + ".name"));
		if (gasType.combustability != Combustibility.NONE)
		{
			tooltip.add(I18n.format("tooltip.gasesframework.burnrate") + ": " + gasType.combustability.burnRate + "/5");
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if (!isInCreativeTab(tab)) return;
		for (GasType type : GFRegistrationAPI.getGasTypes())
		{
			if (type != null)
			{
				ItemStack filled = new ItemStack(this);
				setGasType(filled, type);
				items.add(filled);
			}
		}
	}
}
