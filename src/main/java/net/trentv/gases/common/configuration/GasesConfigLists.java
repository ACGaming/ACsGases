package net.trentv.gases.common.configuration;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class GasesConfigLists
{
	public static final List<Block> COAL_DUST_EMISSION_BLOCKS = new ArrayList<>();
	public static final List<Block> DUST_EMISSION_BLOCKS = new ArrayList<>();
	public static final List<Block> IGNITION_SOURCES = new ArrayList<>();

	public static void init()
	{
		addBlocksToList(GasesMainConfigurations.GASES.COAL_DUST.blocks, COAL_DUST_EMISSION_BLOCKS);
		addBlocksToList(GasesMainConfigurations.GASES.DUST.blocks, DUST_EMISSION_BLOCKS);
		addBlocksToList(GasesMainConfigurations.GASES.ignitionSources, IGNITION_SOURCES);
	}

	private static void addBlocksToList(String[] blocks, List<Block> list)
	{
		for (String s : blocks)
		{
			Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
			if (b != null)
			{
				list.add(b);
			}
		}
	}
}
