package net.trentv.gasesframework.common.entity;

import net.minecraft.world.World;

public class EntityFlashSparkFX extends EntityFX
{
	public final double[] prevMotionX = new double[5];
	public final double[] prevMotionY = new double[5];
	public final double[] prevMotionZ = new double[5];

	public EntityFlashSparkFX(World world)
	{
		super(world);
	}

	public EntityFlashSparkFX(World world, double x, double y, double z, double xv, double yv, double zv)
	{
		super(world, x, y, z);
		this.motionX = xv;
		this.motionY = yv;
		this.motionZ = zv;
		this.maxLifetime = 20 + world.rand.nextInt(10);
	}

	@Override
	public void onUpdate()
	{
		if (motionY == 0.0D)
		{
			motionY = -prevMotionY[0] * 0.75D;
			motionX *= 0.9D;
			motionZ *= 0.9D;
		}
		else
		{
			motionY -= 0.1D;
		}

		push(motionX, prevMotionX);
		push(motionY, prevMotionY);
		push(motionZ, prevMotionZ);

		super.onUpdate();
	}

	private void push(double motion, double[] array)
	{
		for (int i = 4; i > 0; i--)
		{
			array[i] = array[i - 1];
		}
		array[0] = motion;
	}
}
