package net.trentv.gasesframework;

import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import net.trentv.gasesframework.api.capability.IGasEffects;
import net.trentv.gasesframework.common.CommonProxy;
import net.trentv.gasesframework.common.GasesFrameworkObjects;
import net.trentv.gasesframework.common.capability.BaseGasEffects;
import net.trentv.gasesframework.common.capability.GasEffectsStorage;
import net.trentv.gasesframework.common.worldgen.WorldGeneratorGasesFramework;
import net.trentv.gasesframework.impl.ImplManipulationAPI;
import net.trentv.gasesframework.impl.ImplRegistrationAPI;

@Mod(modid = GasesFramework.MODID, version = GasesFramework.VERSION, acceptedMinecraftVersions = "1.12.2")
public class GasesFramework
{
	@Instance(GasesFramework.MODID)
	public static GasesFramework INSTANCE;

	public static final String MODID = "gasesframework";
	public static final String VERSION = "2.0.0";
	public static final GasesFrameworkCreativeTab CREATIVE_TAB = new GasesFrameworkCreativeTab("gasesframework");
	public static final WorldGeneratorGasesFramework worldGenerator = new WorldGeneratorGasesFramework();

	@SidedProxy(clientSide = "net.trentv.gasesframework.client.ClientProxy", serverSide = "net.trentv.gasesframework.server.ServerProxy")
	public static CommonProxy proxy;

	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();

		new ImplRegistrationAPI();
		new ImplManipulationAPI();

		// Register all items, blocks, and gases
		GasesFrameworkObjects.init();
		CapabilityManager.INSTANCE.register(IGasEffects.class, new GasEffectsStorage<>(), new BaseGasEffects());
		proxy.registerEventHandlers();
		proxy.registerRenderers();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.registerColorHandlers();
		// Register world generator with massive weight to ensure gas pockets are not popped by other world generators
		GameRegistry.registerWorldGenerator(worldGenerator, 10000);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		logger.info("'Gases Framework' initialized");
	}

	private static class GasesFrameworkCreativeTab extends CreativeTabs
	{
		public GasesFrameworkCreativeTab(String label)
		{
			super(label);
		}

		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(GasesFrameworkObjects.SMOKE.itemBlock);
		}
	}
}
