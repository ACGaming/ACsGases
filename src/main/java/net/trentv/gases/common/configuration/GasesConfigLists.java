package net.trentv.gases.common.configuration;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.trentv.gases.Gases;
import net.trentv.gases.GasesRegistry;
import net.trentv.gases.common.GasesObjects;
import net.trentv.gases.common.block.BlockHeated;
import net.trentv.gasesframework.api.GFRegistrationAPI;
import net.trentv.gasesframework.api.IGasEffectProtector;
import net.trentv.gasesframework.api.reaction.entity.IEntityReaction;

public class GasesConfigLists
{
	public static final List<Block> COAL_DUST_EMISSION_BLOCKS = new ArrayList<>();
	public static final List<Block> DUST_EMISSION_BLOCKS = new ArrayList<>();
	public static final List<Block> SMOKE_EMISSION_BLOCKS = new ArrayList<>();
	public static final Map<Item, IGasEffectProtector> RESPIRATORS = new IdentityHashMap<>();

	public static void preInit()
	{
		registerHeatedRecipes();
	}

	public static void postInit()
	{
		COAL_DUST_EMISSION_BLOCKS.addAll(addBlocksToList(GasesMainConfigurations.GASES.COAL_DUST.blocks, "coal dust"));
		DUST_EMISSION_BLOCKS.addAll(addBlocksToList(GasesMainConfigurations.GASES.DUST.blocks, "dust"));
		SMOKE_EMISSION_BLOCKS.addAll(addBlocksToList(GasesMainConfigurations.GASES.SMOKE.blocks, "smoke"));
		registerCustomRespirators();
		registerIgnitionSources();
		registerRustableMaterials();
	}

	private static List<Block> addBlocksToList(String[] blocks, String listName)
	{
		List<Block> list = new ArrayList<>();
		for (String s : blocks)
		{
			// Assume regular resource location
			if (s.contains(":"))
			{
				Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
				if (b != null)
				{
					list.add(b);
					Gases.logger.info("Added block {} to {} list", b.getRegistryName(), listName);
				}
			}
			// Assume ore dictionary name
			else
			{
				for (ItemStack is : OreDictionary.getOres(s))
				{
					Block b = Block.getBlockFromItem(is.getItem());
					if (b != null)
					{
						list.add(b);
						Gases.logger.info("Added block {} to {} list", b.getRegistryName(), listName);
					}
				}
			}
		}
		return list;
	}

	private static void registerCustomRespirators()
	{
		for (String s : GasesMainConfigurations.GASES.customRespirators)
		{
			String[] parts = s.split(";");
			if (parts.length != 2) return;

			String item = parts[0];
			String edition = parts[1];

			Item respirator = ForgeRegistries.ITEMS.getValue(new ResourceLocation(item));
			if (respirator == null) return;

			if (edition.equals("primitive"))
			{
				RESPIRATORS.put(respirator, createRespirator(GasesObjects.BLOCKED_REACTIONS_PRIMITIVE));
			}
			else if (edition.equals("advanced"))
			{
				RESPIRATORS.put(respirator, createRespirator(GasesObjects.BLOCKED_REACTIONS_ADVANCED));
			}
		}
	}

	private static IGasEffectProtector createRespirator(List<Class<? extends IEntityReaction>> blockedReactions)
	{
		return (entity, reaction, gasType, pos, stack) -> {
			int headY = (int) (entity.posY + entity.getEyeHeight());
			if (pos.getY() != headY || !blockedReactions.contains(reaction.getClass())) return false;
			if (!entity.world.isRemote && stack.isItemStackDamageable() && entity.world.getWorldTime() % GasesMainConfigurations.GASES.respiratorDamageRate == 0)
			{
				stack.damageItem(GasesMainConfigurations.GASES.respiratorDamageAmount, entity);
			}
			return true;
		};
	}

	private static void registerHeatedRecipes()
	{
		for (String s : GasesMainConfigurations.GASES.IOCALFAEUS_GAS.heatedRecipes)
		{
			String[] parts = s.split(";");
			if (parts.length != 4) return;

			String original = parts[0];
			String refined = parts[1];
			String ruined = parts[2];
			String id = parts[3];

			Block blockOriginal = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(original));
			Block blockRefined = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(refined));
			Block blockRuined = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ruined));

			if (blockOriginal == null || blockRefined == null || blockRuined == null || id.isEmpty()) return;

			GasesRegistry.registerHeatedRecipe(new BlockHeated(blockOriginal.getDefaultState(), blockRefined.getDefaultState(), blockRuined.getDefaultState(), id));
		}
	}

	private static void registerIgnitionSources()
	{
		List<Block> l = addBlocksToList(GasesMainConfigurations.GASES.ignitionSources, "ignition sources");
		for (Block b : l)
		{
			GFRegistrationAPI.registerIgnitionSource(b.getDefaultState());
		}
	}

	private static void registerRustableMaterials()
	{
		for (String s : GasesMainConfigurations.GASES.CHLORINE_GAS.rustableMaterials)
		{
			GasesRegistry.registerRustableMaterial(s);
		}
	}
}
