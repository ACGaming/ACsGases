package net.trentv.gasesframework.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;

import net.trentv.gasesframework.api.GFRegistrationAPI;
import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.common.CommonProxy;
import net.trentv.gasesframework.common.GasesFrameworkObjects;
import net.trentv.gasesframework.common.item.ItemGasBottle;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderers()
	{
	}

	@Override
	public void registerColorHandlers()
	{
		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
		BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
		GasType[] types = GFRegistrationAPI.getGasTypes();
		for (GasType type : types)
		{
			ItemBlock item = type.itemBlock;
			Block block = type.block;
			// Register the gastype for both the item and the block so they can be tinted at runtime
			if (type.tintindex)
			{
				blockColors.registerBlockColorHandler(type.getGasColor(), block);
				itemColors.registerItemColorHandler(type.getGasColor(), item);
			}
		}
		itemColors.registerItemColorHandler((stack, tintIndex) -> {
			if (tintIndex == 0)
			{
				GasType gas = ItemGasBottle.getGasType(stack);
				return gas != null ? gas.color : 0xFFFFFF;
			}
			return 0xFFFFFF;
		}, GasesFrameworkObjects.GAS_BOTTLE);
	}

	@Override
	public void registerEventHandlers()
	{
		super.registerEventHandlers();
		MinecraftForge.EVENT_BUS.register(new ClientEvents());
		//MinecraftForge.EVENT_BUS.register(new GasSoundHandler());
	}
}
