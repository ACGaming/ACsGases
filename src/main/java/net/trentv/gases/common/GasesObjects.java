package net.trentv.gases.common;

import java.util.Arrays;
import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import net.trentv.gases.Gases;
import net.trentv.gases.GasesRegistry;
import net.trentv.gases.common.block.BlockDiabalineOre;
import net.trentv.gases.common.block.BlockHeated;
import net.trentv.gases.common.block.BlockModifiedBedrock;
import net.trentv.gases.common.gastype.GasTypeBlackDamp;
import net.trentv.gases.common.gastype.GasTypeLightSensitive;
import net.trentv.gases.common.gastype.GasTypeVoid;
import net.trentv.gases.common.item.ItemDiabalineRefined;
import net.trentv.gases.common.item.ItemRespirator;
import net.trentv.gases.common.reaction.EntityReactionDamage;
import net.trentv.gases.common.reaction.EntityReactionFinine;
import net.trentv.gasesframework.GasesFrameworkRegistry;
import net.trentv.gasesframework.api.Combustibility;
import net.trentv.gasesframework.api.GFRegistrationAPI;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.lanterntype.LanternType;
import net.trentv.gasesframework.api.reaction.entity.EntityReactionBlindness;
import net.trentv.gasesframework.api.reaction.entity.EntityReactionSlowness;
import net.trentv.gasesframework.api.reaction.entity.EntityReactionSuffocation;
import net.trentv.gasesframework.common.block.BlockLantern;

public class GasesObjects
{
	public static final DamageSource DAMAGE_SOURCE_STEAM = new DamageSource("gas_steam").setDamageBypassesArmor();
	public static final DamageSource DAMAGE_SOURCE_VOID = new DamageSource("gas_void").setDamageBypassesArmor();

	public static final GasType STEAM = new GasType("steam", 0xFFFFFF, 12, 1, Combustibility.NONE).setCohesion(2).setDissipation(4, 2);
	public static final GasType NATURAL_GAS = new GasType("natural", 0x6F7F6F, 8, 1, Combustibility.FLAMMABLE);
	public static final GasType RED_GAS = new GasType("red", 0x7F4F4F, 2, -1, Combustibility.EXPLOSIVE);
	public static final GasType VOID_GAS = new GasTypeVoid("void", 0x1F1F1F, 0, -1, Combustibility.NONE);
	public static final GasType ELECTRIC = new GasType("electric", 0x1F7F7F, 0, 0, Combustibility.NONE).setCohesion(16);
	public static final GasType CORROSIVE = new GasType("corrosive", 0x1F1FDF, 0, 0, Combustibility.NONE).setCohesion(16);
	public static final GasType NITROUS = new GasType("nitrous", 0x6F3F2F, 4, -1, Combustibility.NONE);
	public static final GasType ACID_VAPOUR = new GasType("acid_vapour", 0x4F7FBF, 0, 1, Combustibility.NONE).setDissipation(1, 1);
	public static final GasType COAL_DUST = new GasType("coal_dust", 0x2F2F2F, 2, 0, Combustibility.EXPLOSIVE).setCohesion(6).setDissipation(2, 4);
	public static final GasType BLACK_DAMP = new GasTypeBlackDamp("black_damp", 0x000000, 16, 0, Combustibility.NONE);
	public static final GasType CHLORINE = new GasType("chlorine", 0xC2F29C, 1, 8, Combustibility.NONE);
	public static final GasType STONE_DUST = new GasType("stone_dust", 0x7F7F7F, 0, -1, Combustibility.NONE).setDissipation(8, 4);
	public static final GasType IOCALFAEUS = new GasTypeLightSensitive("iocalfaeus", 0x5C2B77, 6, -1, Combustibility.NONE);
	public static final GasType HELIUM = new GasType("helium", 0x00FFFF, 14, 0, Combustibility.NONE).setCohesion(16);
	public static final GasType FININE = new GasType("finine", 0xFFFEE8, 0, 0, Combustibility.NONE).setCohesion(16).setTexture(new ResourceLocation(Gases.MODID, "block/finine"), false);
	public static final GasType WHISPERING_FOG = new GasType("whispering_fog", 0x000000, 15, -1, Combustibility.HIGHLY_EXPLOSIVE);

	private static final GasType[] IMPLEMENTED_GASES = new GasType[] { STEAM, NATURAL_GAS, RED_GAS, VOID_GAS, ELECTRIC, CORROSIVE, NITROUS, ACID_VAPOUR, COAL_DUST, BLACK_DAMP, CHLORINE, STONE_DUST, IOCALFAEUS, HELIUM, FININE, WHISPERING_FOG };

	private static final HashMap<Block, BlockHeated> HEATED_RECIPE_LIST = new HashMap<>();

	public static final BlockModifiedBedrock MODIFIED_BEDROCK = new BlockModifiedBedrock(VOID_GAS, 4, 5, new ResourceLocation(Gases.MODID, "bedrock"));
	public static final BlockModifiedBedrock WHISPERING_FOG_EMITTER = (BlockModifiedBedrock) new BlockModifiedBedrock(WHISPERING_FOG, 1, 16, new ResourceLocation(Gases.MODID, "whispering_fog_emitter")).setCreativeTab(Gases.CREATIVE_TAB);
	public static final BlockDiabalineOre DIABALINE_ORE = (BlockDiabalineOre) new BlockDiabalineOre(false, new ResourceLocation(Gases.MODID, "diabaline_ore")).setCreativeTab(Gases.CREATIVE_TAB);
	public static final BlockDiabalineOre DIABALINE_ORE_GLOWING = (BlockDiabalineOre) new BlockDiabalineOre(true, new ResourceLocation(Gases.MODID, "diabaline_ore_glowing")).setCreativeTab(Gases.CREATIVE_TAB);

	public static final ItemDiabalineRefined DIABALINE_REFINED = new ItemDiabalineRefined();
	public static final ItemRespirator PRIMITIVE_RESPIRATOR = new ItemRespirator(Arrays.asList(EntityReactionSlowness.class, EntityReactionSuffocation.class), EnumHelper.addArmorMaterial("primitive_respirator", Gases.MODID + ":primitive_respirator", 20, new int[] { 2, 0, 0, 0 }, 12, null, 5), "primitive_respirator", Items.COAL);
	public static final ItemRespirator ADVANCED_RESPIRATOR = new ItemRespirator(Arrays.asList(EntityReactionSlowness.class, EntityReactionSuffocation.class, EntityReactionBlindness.class), EnumHelper.addArmorMaterial("advanced_respirator", Gases.MODID + ":advanced_respirator", 50, new int[] { 2, 0, 0, 0 }, 12, null, 5), "advanced_respirator", Items.IRON_INGOT);

	public static final LanternType LANTERN_TYPE_TORCH = new LanternType("torch", 15.0f / 16.0f, "gases:lantern_torch", Item.getItemFromBlock(Blocks.TORCH), null, 0).setCreativeTab(Gases.CREATIVE_TAB);
	public static final LanternType LANTERN_TYPE_GLOWSTONE = new LanternType("glowstone", 1.0f, "gases:lantern_glowstone", Items.GLOWSTONE_DUST, null, 0).setCreativeTab(Gases.CREATIVE_TAB);
	public static final BlockLantern LANTERN_TORCH = new BlockLantern(LANTERN_TYPE_TORCH);
	public static final BlockLantern LANTERN_GLOWSTONE = new BlockLantern(LANTERN_TYPE_GLOWSTONE);

	public static void init()
	{
		registerEntityReactions();
		for (GasType type : IMPLEMENTED_GASES)
		{
			type.setCreativeTab(Gases.CREATIVE_TAB);
			GFRegistrationAPI.registerGasType(type, new ResourceLocation(Gases.MODID, "gas_" + type.name));
		}

		registerHeatedRecipe(new BlockHeated(Blocks.IRON_ORE.getDefaultState(), Blocks.IRON_BLOCK.getDefaultState(), Blocks.STONE.getDefaultState(), "iron"));
		registerHeatedRecipe(new BlockHeated(Blocks.DIAMOND_ORE.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState(), Blocks.STONE.getDefaultState(), "diamond"));
		registerHeatedRecipe(new BlockHeated(Blocks.GOLD_ORE.getDefaultState(), Blocks.GOLD_BLOCK.getDefaultState(), Blocks.STONE.getDefaultState(), "gold"));
		registerHeatedRecipe(new BlockHeated(Blocks.REDSTONE_ORE.getDefaultState(), Blocks.REDSTONE_BLOCK.getDefaultState(), Blocks.STONE.getDefaultState(), "redstone"));
		registerHeatedRecipe(new BlockHeated(Blocks.LAPIS_ORE.getDefaultState(), Blocks.LAPIS_BLOCK.getDefaultState(), Blocks.STONE.getDefaultState(), "lapis"));
		registerHeatedRecipe(new BlockHeated(Blocks.STONE.getDefaultState(), Blocks.STONE.getDefaultState(), Blocks.STONE.getDefaultState(), "stone"));

		GasesRegistry.registerItem(DIABALINE_REFINED, PRIMITIVE_RESPIRATOR, ADVANCED_RESPIRATOR);
		GasesRegistry.registerBlockAndItem(MODIFIED_BEDROCK, WHISPERING_FOG_EMITTER);
		GasesRegistry.registerBlockAndItem(DIABALINE_ORE, DIABALINE_ORE_GLOWING);

		GasesFrameworkRegistry.registerLantern(LANTERN_TORCH);
		GasesFrameworkRegistry.registerLantern(LANTERN_GLOWSTONE);
	}

	public static void registerEntityReactions()
	{
		STEAM.registerEntityReaction(new EntityReactionDamage(DAMAGE_SOURCE_STEAM, 4));
		NATURAL_GAS.registerEntityReaction(new EntityReactionBlindness(2), new EntityReactionSuffocation(2, 3), new EntityReactionSlowness(8));
		RED_GAS.registerEntityReaction(new EntityReactionBlindness(1), new EntityReactionSuffocation(2, 3), new EntityReactionSlowness(8));
		VOID_GAS.registerEntityReaction(new EntityReactionDamage(DAMAGE_SOURCE_VOID, 8), new EntityReactionBlindness(20), new EntityReactionSuffocation(40, 3));
		ELECTRIC.registerEntityReaction(new EntityReactionBlindness(4), new EntityReactionSuffocation(2, 3));
		CORROSIVE.registerEntityReaction(new EntityReactionBlindness(4), new EntityReactionSuffocation(2, 3));
		NITROUS.registerEntityReaction(new EntityReactionBlindness(1), new EntityReactionSuffocation(2, 3), new EntityReactionSlowness(16));
		ACID_VAPOUR.registerEntityReaction(new EntityReactionBlindness(20), new EntityReactionSuffocation(1, 3));
		COAL_DUST.registerEntityReaction(new EntityReactionSuffocation(6, 3), new EntityReactionSlowness(16));
		BLACK_DAMP.registerEntityReaction(new EntityReactionSuffocation(6, 3), new EntityReactionSlowness(24));
		CHLORINE.registerEntityReaction(new EntityReactionBlindness(4), new EntityReactionSuffocation(6, 3));
		STONE_DUST.registerEntityReaction(new EntityReactionSuffocation(8, 3), new EntityReactionSlowness(16));
		HELIUM.registerEntityReaction(new EntityReactionSuffocation(14, 3));
		FININE.registerEntityReaction(new EntityReactionFinine());
		WHISPERING_FOG.registerEntityReaction(new EntityReactionSuffocation(6, 3), new EntityReactionSlowness(24));
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
		GasesRegistry.registerBlockAndItem(block);
		GasesRegistry.registerHeatedModel(block);
		HEATED_RECIPE_LIST.put(block.original.getBlock(), block);
	}
}
