package net.trentv.gasesframework.common;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.github.bsideup.jabel.Desugar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import net.trentv.gases.common.configuration.GasesConfigLists;
import net.trentv.gasesframework.GasesFramework;
import net.trentv.gasesframework.api.GasesFrameworkAPI;
import net.trentv.gasesframework.api.IGasEffectProtector;
import net.trentv.gasesframework.api.capability.IGasEffects;
import net.trentv.gasesframework.common.capability.GasEffectsProvider;

public class CommonEvents
{
	private static final Set<Long> OCCUPIED_CHUNKS = new HashSet<>();
	private static final Queue<PendingExplosion> EXPLOSION_QUEUE = new ConcurrentLinkedDeque<>();

	public static void scheduleExplosion(WorldServer world, BlockPos pos, float power, int delayTicks)
	{
		EXPLOSION_QUEUE.add(new PendingExplosion(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, power, world.getTotalWorldTime() + delayTicks));
	}

	@Nullable
	public static IGasEffectProtector getProtector(ItemStack stack)
	{
		if (stack.isEmpty()) return null;
		Item item = stack.getItem();
		if (item instanceof IGasEffectProtector prot) return prot;
		return GasesConfigLists.RESPIRATORS.get(item);
	}

	public static boolean isPlayerNearby(BlockPos pos)
	{
		int cx = pos.getX() >> 4;
		int cz = pos.getZ() >> 4;
		return OCCUPIED_CHUNKS.contains(ChunkPos.asLong(cx, cz));
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		Entity b = event.getEntity();
		if (b.hasCapability(GasEffectsProvider.GAS_EFFECTS, null))
		{
			IGasEffects q = b.getCapability(GasEffectsProvider.GAS_EFFECTS, null);

			if (q.getSuffocation() == 200)
			{
				b.attackEntityFrom(GasesFrameworkAPI.damageSourceAsphyxiation, 2);
			}

			if (q.getSuffocation() > 0) q.setSuffocation(q.getSuffocation() - 1);
			if (q.getBlindness() > 0) q.setBlindness(q.getBlindness() - 1);

			if (q.getSlowness() > 0)
			{
				q.setSlowness(q.getSlowness() - 1);
				float multiply = 1 - (q.getSlowness() / 100);
				if (multiply < 0.05f) multiply = 0.05f;
				b.motionX *= multiply;
				b.motionZ *= multiply;
			}
		}
	}

	@SubscribeEvent
	public void attachCapabilityEventEntity(AttachCapabilitiesEvent<Entity> e)
	{
		if (e.getObject() instanceof EntityLivingBase)
		{
			e.addCapability(new ResourceLocation(GasesFramework.MODID, "gas_effects"), new GasEffectsProvider());
		}
	}

	@SubscribeEvent
	public void onWorldTickOccupiedChunks(TickEvent.WorldTickEvent event)
	{
		if (event.phase != TickEvent.Phase.START) return;
		if (event.world.isRemote) return;
		if (event.world.getWorldTime() % 5 != 0) return;

		OCCUPIED_CHUNKS.clear();
		for (EntityPlayer player : event.world.playerEntities)
		{
			int cx = (int) player.posX >> 4;
			int cz = (int) player.posZ >> 4;
			for (int dx = -1; dx <= 1; dx++)
			{
				for (int dz = -1; dz <= 1; dz++)
				{
					OCCUPIED_CHUNKS.add(ChunkPos.asLong(cx + dx, cz + dz));
				}
			}
		}
	}

	@SubscribeEvent
	public void onWorldTickExplosionQueue(TickEvent.WorldTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END) return;
		if (!(event.world instanceof WorldServer server)) return;
		if (event.world.getWorldTime() % 5 != 0) return;

		long time = server.getTotalWorldTime();
		Iterator<PendingExplosion> it = EXPLOSION_QUEUE.iterator();
		while (it.hasNext())
		{
			PendingExplosion exp = it.next();
			if (exp.world == server && time >= exp.time)
			{
				server.newExplosion(null, exp.x, exp.y, exp.z, exp.power, true, true);
				it.remove();
			}
		}
	}

	@Desugar
	private record PendingExplosion(WorldServer world, double x, double y, double z, float power, long time) {}
}
