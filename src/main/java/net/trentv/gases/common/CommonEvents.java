package net.trentv.gases.common;

import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.trentv.gasesframework.api.GFManipulationAPI;

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
                    GFManipulationAPI.addGasLevel(event.getPos().up(2), event.getWorld(), GasesObjects.STEAM, 8);
                }
            }
        }
    }

    // Mining Coal Ore -> Coal Dust
    @SubscribeEvent
    public void onHarvestDropsEvent(BlockEvent.HarvestDropsEvent event)
    {
        if (event.getState().getBlock() == Blocks.COAL_ORE)
        {
            GFManipulationAPI.addGasLevel(event.getPos(), event.getWorld(), GasesObjects.COAL_DUST, 10);
        }
    }

    // Bedrock -> Modified Bedrock
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPopulateChunkEventPre(PopulateChunkEvent.Pre event)
    {
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
}
