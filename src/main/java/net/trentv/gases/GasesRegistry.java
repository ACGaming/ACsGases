package net.trentv.gases;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import net.trentv.gases.common.GasesObjects;
import net.trentv.gases.common.block.BlockHeated;
import net.trentv.gases.common.reaction.block.*;
import net.trentv.gases.common.reaction.entity.EntityReactionDamage;
import net.trentv.gases.common.reaction.entity.EntityReactionFinine;
import net.trentv.gases.common.reaction.entity.EntityReactionRustItems;
import net.trentv.gases.common.reaction.entity.EntityReactionSparkIgnition;
import net.trentv.gasesframework.api.Combustibility;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.reaction.entity.EntityReactionBlindness;
import net.trentv.gasesframework.api.reaction.entity.EntityReactionSlowness;
import net.trentv.gasesframework.api.reaction.entity.EntityReactionSuffocation;

public class GasesRegistry
{
	public static final Set<Block> BLOCKS = new ObjectOpenHashSet<>();
	public static final Set<Item> ITEMS = new ObjectOpenHashSet<>();

	private static final Map<ResourceLocation, BlockHeated> HEATED_LOCATIONS = new Object2ObjectOpenHashMap<>();
	private static final Map<Block, BlockHeated> HEATED_RECIPE_LIST = new Object2ObjectOpenHashMap<>();
	private static final Map<Block, Block> RUSTED_BLOCKS = new Object2ObjectOpenHashMap<>();
	private static final Map<Block, Block> UNRUSTED_BLOCKS = new Object2ObjectOpenHashMap<>();
	private static final Map<Item, Item> RUSTED_ITEMS = new Object2ObjectOpenHashMap<>();
	private static final Map<Item, Item> UNRUSTED_ITEMS = new Object2ObjectOpenHashMap<>();
	private static final Set<String> RUSTABLE_MATERIALS = new ObjectOpenHashSet<>();

	// Utility methods
	public static void registerBlock(Block... toRegister)
	{
		BLOCKS.addAll(Arrays.asList(toRegister));
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

	public static void registerHeatedModel(BlockHeated... toRegister)
	{
		for (BlockHeated in : toRegister)
		{
			HEATED_LOCATIONS.put(in.getRegistryName(), in);
		}
	}

	public static void registerRustedBlock(Block block, Block rustedBlock)
	{
		RUSTED_BLOCKS.put(block, rustedBlock);
		UNRUSTED_BLOCKS.put(rustedBlock, block);
		RUSTED_ITEMS.put(Item.getItemFromBlock(block), Item.getItemFromBlock(rustedBlock));
	}

	public static Block getRustedBlock(Block block)
	{
		return RUSTED_BLOCKS.get(block);
	}

	public static Block getUnrustedBlock(Block rustedBlock)
	{
		return UNRUSTED_BLOCKS.get(rustedBlock);
	}

	public static void registerRustedItem(Item item, Item rustedItem)
	{
		RUSTED_ITEMS.put(item, rustedItem);
		UNRUSTED_ITEMS.put(rustedItem, item);
	}

	public static Item getRustedItem(Item item)
	{
		Item rustedItem = RUSTED_ITEMS.get(item);
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
		Item unrustedItem = UNRUSTED_ITEMS.get(rustedItem);
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
		RUSTABLE_MATERIALS.add(material);
	}

	public static boolean isRustableMaterial(String material)
	{
		return RUSTABLE_MATERIALS.contains(material);
	}

	public static void registerBlockReactions()
	{
		GasesObjects.RED_GAS.registerBlockReaction(new BlockReactionWaterIgnition());
		GasesObjects.CORROSIVE.registerBlockReaction(new BlockReactionCorrosion());
		GasesObjects.NITROUS.registerBlockReaction(new BlockReactionAcidVapour());
		GasesObjects.BLACK_DAMP.registerBlockReaction(new BlockReactionExtinguish());
		GasesObjects.CHLORINE.registerBlockReaction(new BlockReactionRustBlocks());
	}

	public static void registerEntityReactions()
	{
		GasesObjects.STEAM.registerEntityReaction(new EntityReactionDamage(GasesObjects.DAMAGE_SOURCE_STEAM, 4));
		GasesObjects.NATURAL_GAS.registerEntityReaction(new EntityReactionBlindness(2), new EntityReactionSuffocation(2, 3), new EntityReactionSlowness(8));
		GasesObjects.RED_GAS.registerEntityReaction(new EntityReactionBlindness(1), new EntityReactionSuffocation(2, 3), new EntityReactionSlowness(8));
		GasesObjects.VOID_GAS.registerEntityReaction(new EntityReactionDamage(GasesObjects.DAMAGE_SOURCE_VOID, 8), new EntityReactionBlindness(20), new EntityReactionSuffocation(40, 3));
		GasesObjects.ELECTRIC.registerEntityReaction(new EntityReactionBlindness(4), new EntityReactionSuffocation(2, 3));
		GasesObjects.CORROSIVE.registerEntityReaction(new EntityReactionDamage(DamageSource.GENERIC, 2), new EntityReactionBlindness(4), new EntityReactionSuffocation(2, 3));
		GasesObjects.NITROUS.registerEntityReaction(new EntityReactionBlindness(1), new EntityReactionSuffocation(2, 3), new EntityReactionSlowness(16));
		GasesObjects.ACID_VAPOUR.registerEntityReaction(new EntityReactionDamage(DamageSource.GENERIC, 4), new EntityReactionBlindness(20), new EntityReactionSuffocation(1, 3));
		GasesObjects.COAL_DUST.registerEntityReaction(new EntityReactionSuffocation(6, 3), new EntityReactionSlowness(16));
		GasesObjects.BLACK_DAMP.registerEntityReaction(new EntityReactionSuffocation(6, 3), new EntityReactionSlowness(24));
		GasesObjects.CHLORINE.registerEntityReaction(new EntityReactionRustItems(), new EntityReactionBlindness(4), new EntityReactionSuffocation(6, 3));
		GasesObjects.STONE_DUST.registerEntityReaction(new EntityReactionSuffocation(8, 3), new EntityReactionSlowness(16));
		GasesObjects.HELIUM.registerEntityReaction(new EntityReactionSuffocation(14, 3));
		GasesObjects.FININE.registerEntityReaction(new EntityReactionFinine());
		GasesObjects.WHISPERING_FOG.registerEntityReaction(new EntityReactionSuffocation(6, 3), new EntityReactionSlowness(24));

		for (GasType gasType : GasesObjects.IMPLEMENTED_GASES)
		{
			if (gasType.combustability.burnRate >= Combustibility.FLAMMABLE.burnRate)
			{
				gasType.registerEntityReaction(new EntityReactionSparkIgnition());
			}
		}
	}

	public static void registerGasReactions()
	{

	}

	@Nullable
	public static BlockHeated getHeated(Block block)
	{
		return HEATED_RECIPE_LIST.get(block);
	}

	public static BlockHeated[] getAllHeated()
	{
		return HEATED_RECIPE_LIST.values().toArray(new BlockHeated[HEATED_RECIPE_LIST.size()]);
	}

	public static void registerHeatedRecipe(BlockHeated block)
	{
		registerBlockAndItem(block);
		registerHeatedModel(block);
		HEATED_RECIPE_LIST.put(block.original.getBlock(), block);
	}

	public static Map<ResourceLocation, BlockHeated> getRegisteredHeatedLocations()
	{
		return HEATED_LOCATIONS;
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
		for (Block block : BLOCKS)
			event.getRegistry().register(block);
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		for (Item item : ITEMS)
			event.getRegistry().register(item);
	}
}
