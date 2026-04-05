package net.trentv.gases;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import net.trentv.gases.common.block.BlockHeated;

public class GasesRegistry
{
	public static ArrayList<Block> blocks = new ArrayList<Block>();
	public static ArrayList<Item> items = new ArrayList<Item>();
	private static HashMap<ResourceLocation, BlockHeated> registeredLocations = new HashMap<ResourceLocation, BlockHeated>();
	private static Map<Block, Block> rustedBlocks = new IdentityHashMap<Block, Block>();
	private static Map<Block, Block> unrustedBlocks = new IdentityHashMap<Block, Block>();
	private static Map<Item, Item> rustedItems = new IdentityHashMap<Item, Item>();
	private static Map<Item, Item> unrustedItems = new IdentityHashMap<Item, Item>();
	private static Set<String> rustableMaterials = new HashSet<String>();

	// Utility methods
	public static void registerBlock(Block... toRegister)
	{
		for (Block in : toRegister)
			blocks.add(in);
	}

	public static void registerBlockAndItem(Block... toRegister)
	{
		for (Block in : toRegister)
		{
			blocks.add(in);
			ItemBlock a = new ItemBlock(in);
			a.setRegistryName(in.getRegistryName());
			items.add(a);
		}
	}

	public static void registerItem(Item... toRegister)
	{
		for (Item in : toRegister)
			items.add(in);
	}

	public static void registerHeatedModel(BlockHeated... toRegister)
	{
		for (BlockHeated in : toRegister)
		{
			registeredLocations.put(in.getRegistryName(), in);
		}
	}

	public static void registerRustedBlock(Block block, Block rustedBlock)
	{
		rustedBlocks.put(block, rustedBlock);
		unrustedBlocks.put(rustedBlock, block);
		rustedItems.put(Item.getItemFromBlock(block), Item.getItemFromBlock(rustedBlock));
	}

	public static Block getRustedBlock(Block block)
	{
		return rustedBlocks.get(block);
	}

	public static Block getUnrustedBlock(Block rustedBlock)
	{
		return unrustedBlocks.get(rustedBlock);
	}

	public static void registerRustedItem(Item item, Item rustedItem)
	{
		rustedItems.put(item, rustedItem);
		unrustedItems.put(rustedItem, item);
	}

	public static Item getRustedItem(Item item)
	{
		Item rustedItem = rustedItems.get(item);
		if (rustedItem == null)
		{
			Block block = Block.getBlockFromItem(item);
			if (block != Blocks.AIR)
			{
				Block rustedBlock = getRustedBlock(block);
				if (rustedBlock != null)
				{
					return Item.getItemFromBlock(rustedBlock);
				}
			}
		}
		return rustedItem;
	}

	public static Item getUnrustedItem(Item rustedItem)
	{
		Item unrustedItem = unrustedItems.get(rustedItem);
		if (unrustedItem == null)
		{
			Block rustedBlock = Block.getBlockFromItem(rustedItem);
			if (rustedBlock != Blocks.AIR)
			{
				Block unrustedBlock = getUnrustedBlock(rustedBlock);
				if (unrustedBlock != null)
				{
					return Item.getItemFromBlock(unrustedBlock);
				}
			}
		}
		return unrustedItem;
	}

	public static void registerRustableMaterial(String material)
	{
		rustableMaterials.add(material);
	}

	public static boolean isRustableMaterial(String material)
	{
		return rustableMaterials.contains(material);
	}

	// Events
	@SubscribeEvent
	public void registerRenderers(ModelRegistryEvent event)
	{
		for (Item obj : items)
		{
			ModelLoader.setCustomModelResourceLocation(obj, 0, new ModelResourceLocation(obj.getRegistryName(), "inventory"));
		}
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event)
	{
		for (Block block : blocks)
			event.getRegistry().register(block);
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		for (Item item : items)
			event.getRegistry().register(item);
	}

	public static HashMap<ResourceLocation, BlockHeated> getRegisteredHeatedLocations()
	{
		return registeredLocations;
	}
}
