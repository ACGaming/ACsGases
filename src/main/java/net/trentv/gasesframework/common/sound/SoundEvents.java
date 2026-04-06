package net.trentv.gasesframework.common.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import net.trentv.gasesframework.GasesFramework;

public enum SoundEvents
{
	BLOCK_GAS_LOOP("block.gas_loop"),
	EFFECT_DUCT_TAPE("effect.duct_tape");

	private final SoundEvent soundEvent;

	SoundEvents(String path)
	{
		ResourceLocation resourceLocation = new ResourceLocation(GasesFramework.MODID, path);
		this.soundEvent = new SoundEvent(resourceLocation);
		this.soundEvent.setRegistryName(resourceLocation);
	}

	public SoundEvent getSoundEvent()
	{
		return this.soundEvent;
	}
}
