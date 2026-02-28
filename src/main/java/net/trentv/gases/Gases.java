package net.trentv.gases;

import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.trentv.gases.common.CommonProxy;
import net.trentv.gases.common.GasesObjects;
import net.trentv.gases.common.configuration.GasesMainConfigurations;
import net.trentv.gases.common.gasworldgentype.GasWorldGenDiabalinePocket;
import net.trentv.gasesframework.GasesFramework;
import net.trentv.gasesframework.api.gasworldgentype.GasWorldGenCloud;
import net.trentv.gasesframework.api.gasworldgentype.GasWorldGenPocket;

@Mod(modid = Gases.MODID, version = Gases.VERSION, acceptedMinecraftVersions = "1.12.2", dependencies = "required-after:gasesframework")
public class Gases
{
	public static final String MODID = "gases";
	public static final String VERSION = "2.0.0";

	public static final GasesCreativeTab CREATIVE_TAB = new GasesCreativeTab("gases");

	public static Logger logger;

	@SidedProxy(clientSide = "net.trentv.gases.client.ClientProxy", serverSide = "net.trentv.gases.server.ServerProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();

		GasesObjects.init();

		proxy.registerEventHandlers();
		proxy.registerRenderers();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		// Overworld
		GasesFramework.worldGenerator.registerGasWorldGenType(new GasWorldGenPocket("overworld_naturalGas", GasesObjects.NATURAL_GAS, GasesMainConfigurations.WORLD_GENERATION.OVERWORLD.GASES.naturalGas, 48.0f, 0.5f, 16, 48, "stone"), "overworld");
		GasesFramework.worldGenerator.registerGasWorldGenType(new GasWorldGenPocket("overworld_redGas", GasesObjects.RED_GAS, GasesMainConfigurations.WORLD_GENERATION.OVERWORLD.GASES.redGas, 48.0f, 0.5f, 4, 20, "stone"), "overworld");
		GasesFramework.worldGenerator.registerGasWorldGenType(new GasWorldGenPocket("overworld_nitrous", GasesObjects.NITROUS, GasesMainConfigurations.WORLD_GENERATION.OVERWORLD.GASES.nitrousGas, 32.0f, 0.5f, 4, 20, "stone"), "overworld");
		GasesFramework.worldGenerator.registerGasWorldGenType(new GasWorldGenPocket("overworld_chlorine", GasesObjects.CHLORINE, GasesMainConfigurations.WORLD_GENERATION.OVERWORLD.GASES.chlorineGas, 32.0f, 0.5f, 32, 48, "stone"), "overworld");
		GasesFramework.worldGenerator.registerGasWorldGenType(new GasWorldGenDiabalinePocket("overworld_iocalfaeus", GasesObjects.IOCALFAEUS, GasesMainConfigurations.WORLD_GENERATION.OVERWORLD.GASES.iocalfaeusGas, 24.0f, 0.5f, 4, 60, "stone"), "overworld");
		GasesFramework.worldGenerator.registerGasWorldGenType(new GasWorldGenCloud("overworld_blackDamp", GasesObjects.BLACK_DAMP, GasesMainConfigurations.WORLD_GENERATION.OVERWORLD.GASES.blackDamp, 80.0f, 0.5f, 4, 48), "overworld");
		GasesFramework.worldGenerator.registerGasWorldGenType(new GasWorldGenPocket("overworld_helium", GasesObjects.HELIUM, GasesMainConfigurations.WORLD_GENERATION.OVERWORLD.GASES.helium, 24.0f, 0.5f, 64, 96, "stone"), "overworld");

		// Nether
		GasesFramework.worldGenerator.registerGasWorldGenType(new GasWorldGenCloud("nether_electricGas", GasesObjects.ELECTRIC, GasesMainConfigurations.WORLD_GENERATION.NETHER.GASES.electricGas, 40.0f, 1.0f, 40, 128), "the_nether");
		GasesFramework.worldGenerator.registerGasWorldGenType(new GasWorldGenCloud("nether_corrosiveGas", GasesObjects.CORROSIVE, GasesMainConfigurations.WORLD_GENERATION.NETHER.GASES.corrosiveGas, 40.0f, 1.0f, 40, 128), "the_nether");

		// End
		GasesFramework.worldGenerator.registerGasWorldGenType(new GasWorldGenCloud("end_finine", GasesObjects.FININE, GasesMainConfigurations.WORLD_GENERATION.END.GASES.finineGas, 40.0f, 1.0f, 40, 128), "the_end");
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		logger.info("'AC's Gases' initialized");
	}

	private static class GasesCreativeTab extends CreativeTabs
	{
		public GasesCreativeTab(String label)
		{
			super(label);
		}

		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(GasesObjects.DIABALINE_REFINED);
		}
	}
}
