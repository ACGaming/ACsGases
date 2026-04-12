package net.trentv.gasesframework.client.sound;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.trentv.gasesframework.common.sound.SoundEvents;

@SideOnly(Side.CLIENT)
public class GasSoundLoop extends PositionedSound implements ITickableSound
{
	private boolean donePlaying = false;

	public GasSoundLoop(BlockPos pos, int capacity)
	{
		super(SoundEvents.BLOCK_GAS_LOOP.getSoundEvent(), SoundCategory.BLOCKS);
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.1F;
		this.pitch = 2.0F - (capacity / 16.0F);
		this.xPosF = pos.getX() + 0.5F;
		this.yPosF = pos.getY() + 0.5F;
		this.zPosF = pos.getZ() + 0.5F;
		this.attenuationType = AttenuationType.LINEAR;
	}

	public void setPitch(int capacity)
	{
		this.pitch = 2.0F - (capacity / 16.0F);
	}

	public void stopPlaying()
	{
		donePlaying = true;
	}

	@Override
	public boolean isDonePlaying()
	{
		return donePlaying;
	}

	@Override
	public void update()
	{

	}
}
