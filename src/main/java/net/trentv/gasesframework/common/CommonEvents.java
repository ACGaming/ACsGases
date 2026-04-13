package net.trentv.gasesframework.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.github.bsideup.jabel.Desugar;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import net.trentv.gases.common.GasesObjects;
import net.trentv.gases.common.configuration.GasesConfigLists;
import net.trentv.gases.common.configuration.GasesMainConfigurations;
import net.trentv.gasesframework.GasesFramework;
import net.trentv.gasesframework.api.GasesFrameworkAPI;
import net.trentv.gasesframework.api.capability.IGasEffects;
import net.trentv.gasesframework.api.reaction.entity.IEntityReaction;
import net.trentv.gasesframework.common.capability.GasEffectsProvider;

public class CommonEvents
{
	private static final Queue<PendingExplosion> EXPLOSION_QUEUE = new ConcurrentLinkedDeque<>();

	public static boolean applyGasEffectProtection(EntityLivingBase entity, IEntityReaction reaction, BlockPos pos, ItemStack itemstack)
	{
		int headY = (int) (entity.posY + entity.getEyeHeight());
		if (pos.getY() == headY && isValidCustomRespirator(reaction, itemstack))
		{
			if (!entity.world.isRemote && itemstack.isItemStackDamageable() && entity.world.getWorldTime() % GasesMainConfigurations.GASES.respiratorDamageRate == 0)
			{
				itemstack.damageItem(1, entity);
			}
			return true;
		}
		return false;
	}

	public static void scheduleExplosion(WorldServer world, BlockPos pos, float power, int delayTicks)
	{
		EXPLOSION_QUEUE.add(new PendingExplosion(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, power, world.getTotalWorldTime() + delayTicks));
	}

	private static boolean isValidCustomRespirator(IEntityReaction reaction, ItemStack itemstack)
	{
		if (GasesConfigLists.RESPIRATORS_PRIMITIVE.contains(itemstack.getItem()) && GasesObjects.BLOCKED_REACTIONS_PRIMITIVE.contains(reaction.getClass())) return true;
		return GasesConfigLists.RESPIRATORS_ADVANCED.contains(itemstack.getItem()) && GasesObjects.BLOCKED_REACTIONS_ADVANCED.contains(reaction.getClass());
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
	public void onWorldTick(TickEvent.WorldTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END) return;
		if (!(event.world instanceof WorldServer server)) return;

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
