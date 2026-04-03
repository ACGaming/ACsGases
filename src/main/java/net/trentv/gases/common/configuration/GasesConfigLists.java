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

	public static void init()
	{
		for (String s : GasesMainConfigurations.GASES.COAL_DUST.blocks)
		{
			Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
			if (b != null)
			{
				COAL_DUST_EMISSION_BLOCKS.add(b);
			}
		}
		for (String s : GasesMainConfigurations.GASES.DUST.blocks)
		{
			Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
			if (b != null)
			{
				DUST_EMISSION_BLOCKS.add(b);
			}
		}
	}
}
