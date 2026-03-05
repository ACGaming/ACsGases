package net.trentv.gasesframework.common;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.trentv.gasesframework.GasesFramework;
import net.trentv.gasesframework.GasesFrameworkRegistry;
import net.trentv.gasesframework.api.Combustibility;
import net.trentv.gasesframework.api.GFRegistrationAPI;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.lanterntype.LanternType;
import net.trentv.gasesframework.api.reaction.entity.EntityReactionBlindness;
import net.trentv.gasesframework.api.reaction.entity.EntityReactionHrrm;
import net.trentv.gasesframework.api.reaction.entity.EntityReactionSlowness;
import net.trentv.gasesframework.api.reaction.entity.EntityReactionSuffocation;
import net.trentv.gasesframework.common.block.BlockGas;
import net.trentv.gasesframework.common.block.BlockLantern;

public class GasesFrameworkObjects
{
	public static final GasType SMOKE = new GasType("smoke", 0xAAAAAA, 2, 1, Combustibility.NONE).setCreativeTab(GasesFramework.CREATIVE_TAB).setCohesion(10).registerEntityReaction(new EntityReactionSuffocation(3, 3)).registerEntityReaction(new EntityReactionBlindness(4)).registerEntityReaction(new EntityReactionSlowness(2));
	public static final GasType FIRE = new GasType("fire", 0x7F4F4F7F, 2, 0, Combustibility.NONE).setCreativeTab(GasesFramework.CREATIVE_TAB).setCohesion(8).setDissipation(8, 8).setTexture(new ResourceLocation(GasesFramework.MODID, "block/gas_fire"), false);

	public static final LanternType LANTERN_TYPE_EMPTY = new LanternType("empty", 0.0f, "gasesframework:lantern_empty", null, null, 0).setCreativeTab(GasesFramework.CREATIVE_TAB);
	public static final LanternType LANTERN_TYPE_GAS_EMPTY = new LanternType("gas_empty", 0.0f, "gasesframework:lantern_gas_empty", Items.GLASS_BOTTLE, LANTERN_TYPE_EMPTY, 0).setCreativeTab(GasesFramework.CREATIVE_TAB);
	public static final LanternType LANTERN_TYPE_GAS_1 = new LanternType("gas_1", 1.0f, "gasesframework:lantern_gas_1", Items.GLASS_BOTTLE, LANTERN_TYPE_GAS_EMPTY, 1);
	public static final LanternType LANTERN_TYPE_GAS_2 = new LanternType("gas_2", 1.0f, "gasesframework:lantern_gas_2", Items.GLASS_BOTTLE, LANTERN_TYPE_GAS_EMPTY, 2);
	public static final LanternType LANTERN_TYPE_GAS_3 = new LanternType("gas_3", 1.0f, "gasesframework:lantern_gas_3", Items.GLASS_BOTTLE, LANTERN_TYPE_GAS_EMPTY, 3);
	public static final LanternType LANTERN_TYPE_GAS_4 = new LanternType("gas_4", 1.0f, "gasesframework:lantern_gas_4", Items.GLASS_BOTTLE, LANTERN_TYPE_GAS_EMPTY, 4);
	public static final LanternType LANTERN_TYPE_GAS_5 = new LanternType("gas_5", 1.0f, "gasesframework:lantern_gas_5", Items.GLASS_BOTTLE, LANTERN_TYPE_GAS_EMPTY, 5);
	public static final LanternType[] LANTERN_TYPES_GAS = new LanternType[] {LANTERN_TYPE_GAS_EMPTY, LANTERN_TYPE_GAS_1, LANTERN_TYPE_GAS_2, LANTERN_TYPE_GAS_3, LANTERN_TYPE_GAS_4, LANTERN_TYPE_GAS_5};

	public static final BlockLantern LANTERN_EMPTY = new BlockLantern(LANTERN_TYPE_EMPTY);
	public static final BlockLantern LANTERN_GAS_EMPTY = new BlockLantern(LANTERN_TYPE_GAS_EMPTY);
	public static final BlockLantern LANTERN_GAS_1 = new BlockLantern(LANTERN_TYPE_GAS_1);
	public static final BlockLantern LANTERN_GAS_2 = new BlockLantern(LANTERN_TYPE_GAS_2);
	public static final BlockLantern LANTERN_GAS_3 = new BlockLantern(LANTERN_TYPE_GAS_3);
	public static final BlockLantern LANTERN_GAS_4 = new BlockLantern(LANTERN_TYPE_GAS_4);
	public static final BlockLantern LANTERN_GAS_5 = new BlockLantern(LANTERN_TYPE_GAS_5);

	public static void init()
	{
		GFRegistrationAPI.registerGasType(SMOKE, new ResourceLocation(GasesFramework.MODID, "gas_" + SMOKE.name));
		GFRegistrationAPI.registerGasType(FIRE, new ResourceLocation(GasesFramework.MODID, "gas_" + FIRE.name));

		GasType[] allTypes = GFRegistrationAPI.getGasTypes();
		for (GasType type : allTypes)
		{
			type.registerEntityReaction(new EntityReactionHrrm());
		}

		GFRegistrationAPI.registerIgnitionSource(Blocks.FIRE.getDefaultState());
		GFRegistrationAPI.registerIgnitionSource(Blocks.LAVA.getDefaultState());
		GFRegistrationAPI.registerIgnitionSource(Blocks.FLOWING_LAVA.getDefaultState());
		GFRegistrationAPI.registerIgnitionSource(Blocks.TORCH.getDefaultState());
		GFRegistrationAPI.registerIgnitionSource(Blocks.LIT_FURNACE.getDefaultState());

		for (int i = 1; i <= 16; i++)
		{
			// The actual ignited gas
			GFRegistrationAPI.registerIgnitionSource(FIRE.block.getDefaultState().withProperty(BlockGas.CAPACITY, i));
		}

		GasesFrameworkRegistry.registerLantern(LANTERN_EMPTY);
		GasesFrameworkRegistry.registerLantern(LANTERN_GAS_EMPTY);
		GasesFrameworkRegistry.registerLantern(LANTERN_GAS_1);
		GasesFrameworkRegistry.registerLantern(LANTERN_GAS_2);
		GasesFrameworkRegistry.registerLantern(LANTERN_GAS_3);
		GasesFrameworkRegistry.registerLantern(LANTERN_GAS_4);
		GasesFrameworkRegistry.registerLantern(LANTERN_GAS_5);
	}

	public static BlockLantern getLanternBlock(LanternType type)
	{
		return GasesFrameworkRegistry.LANTERN_TYPE_LANTERN_BLOCKS.get(type);
	}

	public static LanternType getLanternTypeByInput(Item item)
	{
		return GasesFrameworkRegistry.LANTERN_TYPES_BY_ITEM.get(item);
	}
}
