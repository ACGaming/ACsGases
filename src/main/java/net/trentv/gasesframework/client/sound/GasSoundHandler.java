package net.trentv.gasesframework.client.sound;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.trentv.gasesframework.common.block.BlockGas;

@SideOnly(Side.CLIENT)
public class GasSoundHandler
{
	private static final int SOUND_RANGE = 16;
	private final Map<BlockPos, Integer> gasBlocksInRange = new HashMap<>();
	private final Map<BlockPos, GasSoundLoop> activeSounds = new HashMap<>();
	private int ticks = 0;

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END) return;
		if (++ticks % 20 != 0) return;
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		World world = mc.world;
		if (player == null || world == null)
		{
			activeSounds.clear();
			return;
		}
		gasBlocksInRange.clear();
		BlockPos playerPos = player.getPosition();
		for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(playerPos.add(-SOUND_RANGE, -SOUND_RANGE, -SOUND_RANGE), playerPos.add(SOUND_RANGE, SOUND_RANGE, SOUND_RANGE)))
		{
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof BlockGas)
			{
				int capacity = state.getValue(BlockGas.CAPACITY);
				gasBlocksInRange.put(pos.toImmutable(), capacity);
			}
		}
		for (Map.Entry<BlockPos, Integer> entry : gasBlocksInRange.entrySet())
		{
			BlockPos pos = entry.getKey();
			int capacity = entry.getValue();
			GasSoundLoop existing = activeSounds.get(pos);
			if (existing == null || existing.isDonePlaying())
			{
				GasSoundLoop sound = new GasSoundLoop(pos, capacity);
				activeSounds.put(pos, sound);
				mc.getSoundHandler().playSound(sound);
			}
			else
			{
				existing.setPitch(capacity);
			}
		}
		Iterator<Map.Entry<BlockPos, GasSoundLoop>> it = activeSounds.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<BlockPos, GasSoundLoop> entry = it.next();
			if (!gasBlocksInRange.containsKey(entry.getKey()))
			{
				entry.getValue().stopPlaying();
				it.remove();
			}
		}
	}
}
