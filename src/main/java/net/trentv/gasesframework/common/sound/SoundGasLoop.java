package net.trentv.gasesframework.common.sound;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.trentv.gasesframework.client.ClientEvents;
import net.trentv.gasesframework.common.block.BlockGas;

@SideOnly(Side.CLIENT)
public class SoundGasLoop extends MovingSound
{
	private final World world;
	private final BlockPos pos;
	private final long posKey;
	private boolean blockGone = false;
	private int fadeTicks = 0;

	public SoundGasLoop(World world, BlockPos pos, IBlockState state)
	{
		super(SoundEvents.BLOCK_GAS_LOOP.getSoundEvent(), SoundCategory.BLOCKS);
		this.world = world;
		this.pos = pos;
		this.posKey = pos.toLong();
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.2F;
		this.pitch = 2.0F - (state.getValue(BlockGas.CAPACITY) / 16.0F);
		this.xPosF = pos.getX() + 0.5F;
		this.yPosF = pos.getY() + 0.5F;
		this.zPosF = pos.getZ() + 0.5F;
		this.attenuationType = AttenuationType.LINEAR;
	}

	@Override
	public void update()
	{
		if (!blockGone)
		{
			if (!(world.getBlockState(pos).getBlock() instanceof BlockGas))
			{
				blockGone = true;
			}
		}
		else
		{
			fadeTicks++;
			this.volume = Math.max(0.0F, 0.2F - (fadeTicks * 0.01F));
		}
	}

	@Override
	public boolean isDonePlaying()
	{
		if (this.volume <= 0.0F)
		{
			ClientEvents.GAS_SOUNDS.remove(this.posKey);
			return true;
		}
		return false;
	}
}
