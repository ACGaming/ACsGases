package net.trentv.gasesframework;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import net.trentv.gasesframework.api.lanterntype.LanternType;
import net.trentv.gasesframework.common.block.BlockLantern;

public class GasesFrameworkRegistry
{
	public static final List<Block> BLOCKS = new ArrayList<>();
	public static final List<Item> ITEMS = new ArrayList<>();
	public static final Set<LanternType> REGISTERED_LANTERN_TYPES = Collections.newSetFromMap(new IdentityHashMap<>());
	public static final Map<String, LanternType> LANTERN_TYPES_BY_NAME = new HashMap<>();
	public static final Map<Item, LanternType> LANTERN_TYPES_BY_ITEM = new HashMap<>();
	public static final Map<LanternType, BlockLantern> LANTERN_TYPE_LANTERN_BLOCKS = new IdentityHashMap<>();

	// Utility methods

	public static void registerBlock(Block... toRegister)
	{
		for (Block in : toRegister) BLOCKS.add(in);
	}

	public static void registerBlockAndItem(Block... toRegister)
	{
		for (Block in : toRegister)
		{
			BLOCKS.add(in);
			ItemBlock a = new ItemBlock(in);
			a.setRegistryName(in.getRegistryName());
			ITEMS.add(a);
		}
	}

	public static void registerItem(Item... toRegister)
	{
		ITEMS.addAll(Arrays.asList(toRegister));
	}

	public static void registerLantern(BlockLantern block)
	{
		registerBlockAndItem(block);
		REGISTERED_LANTERN_TYPES.add(block.type);
		LANTERN_TYPES_BY_NAME.put(block.type.name, block.type);
		LANTERN_TYPES_BY_ITEM.put(block.type.itemOut, block.type);
		LANTERN_TYPE_LANTERN_BLOCKS.put(block.type, block);
	}

	// Events

	@SubscribeEvent
	public void registerRenderers(ModelRegistryEvent event)
	{
		for (Item obj : ITEMS)
		{
			ModelLoader.setCustomModelResourceLocation(obj, 0, new ModelResourceLocation(obj.getRegistryName(), "inventory"));
		}
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event)
	{
		for (Block block : BLOCKS) event.getRegistry().register(block);
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		for (Item item : ITEMS) event.getRegistry().register(item);
	}
}
