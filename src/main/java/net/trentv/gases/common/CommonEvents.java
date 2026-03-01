package net.trentv.gases.common;

import net.minecraft.block.BlockStone;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.trentv.gases.common.configuration.GasesConfigLists;
import net.trentv.gases.common.configuration.GasesMainConfigurations;
import net.trentv.gasesframework.api.GFManipulationAPI;
import net.trentv.gasesframework.api.GasType;

public class CommonEvents
{
	// Water + Lava -> Obsidian + Steam
	@SubscribeEvent
	public void onBlockEvent(BlockEvent event)
	{
		if (event.getState().getBlock() == Blocks.OBSIDIAN)
		{
			if (event instanceof BlockEvent.NeighborNotifyEvent)
			{
				if (event.getWorld().getBlockState(event.getPos().up()).getMaterial().isLiquid())
				{
					GFManipulationAPI.addGasLevel(event.getPos().up(2), event.getWorld(), GasesObjects.STEAM, GasesMainConfigurations.GASES.STEAM.amountOnReaction);
				}
			}
		}
	}

	@SubscribeEvent
	public void onHarvestDropsEvent(BlockEvent.HarvestDropsEvent event)
	{
		// Mining Coal Ore -> Coal Dust
		if (GasesConfigLists.COAL_DUST_EMISSION_BLOCKS.contains(event.getState().getBlock()))
		{
			GFManipulationAPI.addGasLevel(event.getPos(), event.getWorld(), GasesObjects.COAL_DUST, GasesMainConfigurations.GASES.COAL_DUST.amountOnMine);
		}
		// Mining Stone -> Stone Dust
		else if (event.getState().getBlock() instanceof BlockStone)
		{
			GFManipulationAPI.addGasLevel(event.getPos(), event.getWorld(), GasesObjects.STONE_DUST, GasesMainConfigurations.GASES.DUST.amountOnMine);
		}
	}

	// Bedrock -> Modified Bedrock
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPopulateChunkEventPre(PopulateChunkEvent.Pre event)
	{
		if (event.getWorld().provider.getDimensionType() != DimensionType.OVERWORLD) return;
		Chunk chunk = event.getWorld().getChunk(event.getChunkX(), event.getChunkZ());
		for (ExtendedBlockStorage storage : chunk.getBlockStorageArray())
		{
			if (storage != null)
			{
				for (int x = 0; x < 16; x++)
				{
					for (int y = 0; y < 16; y++)
					{
						for (int z = 0; z < 16; z++)
						{
							if (storage.get(x, y, z).equals(Blocks.BEDROCK.getDefaultState()))
							{
								storage.set(x, y, z, GasesObjects.MODIFIED_BEDROCK.getDefaultState());
							}
						}
					}
				}
			}
		}
		chunk.setModified(true);
	}


	// Helium -> Pitch Sounds
	@SubscribeEvent
	public void onPlaySoundAtEntityEvent(PlaySoundAtEntityEvent event)
	{
		if (event.getEntity() instanceof EntityLivingBase)
		{
			EntityLivingBase entity = (EntityLivingBase) event.getEntity();
			World world = event.getEntity().getEntityWorld();

			int x = MathHelper.floor(entity.posX);
			int y = MathHelper.floor(entity.posY + entity.getEyeHeight());
			int z = MathHelper.floor(entity.posZ);

			GasType type = GFManipulationAPI.getGasType(new BlockPos(x, y, z), world);
			if (type == GasesObjects.HELIUM)
			{
				event.setPitch(event.getPitch() * 5.0f);
			}
		}
	}
}
