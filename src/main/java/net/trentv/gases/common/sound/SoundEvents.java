package net.trentv.gases.common.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import net.trentv.gases.Gases;

public enum SoundEvents
{
	EFFECT_GAS_WHISPER("effect.gas_whisper");

	private final SoundEvent soundEvent;

	SoundEvents(String path)
	{
		ResourceLocation resourceLocation = new ResourceLocation(Gases.MODID, path);
		this.soundEvent = new SoundEvent(resourceLocation);
		this.soundEvent.setRegistryName(resourceLocation);
	}

	public SoundEvent getSoundEvent()
	{
		return this.soundEvent;
	}
}
