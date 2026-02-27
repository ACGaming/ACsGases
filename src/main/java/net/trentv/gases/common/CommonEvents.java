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
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.HarvestDropsEvent event)
    {
        if (event.getState().getBlock() == Blocks.COAL_ORE)
        {
            GFManipulationAPI.addGasLevel(event.getPos(), event.getWorld(), GasesObjects.COAL_DUST, 10);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPopulateChunkPre(PopulateChunkEvent.Pre event)
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
