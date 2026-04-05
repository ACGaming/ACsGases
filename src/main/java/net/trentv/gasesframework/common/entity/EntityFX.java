package net.trentv.gasesframework.common.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityFX extends Entity
{
	protected int lifetime;
	protected int maxLifetime;

	public EntityFX(World world)
	{
		super(world);
		setSize(0.2F, 0.2F);
		this.isImmuneToFire = true;
		this.noClip = true;
	}

	public EntityFX(World world, double x, double y, double z)
	{
		this(world);
		setPosition(x, y, z);
	}

	@Override
	protected void entityInit()
	{
	}

	@Override
	public void onUpdate()
	{
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;

		if (++lifetime >= maxLifetime && !world.isRemote)
		{
			setDead();
			return;
		}

		if (this.isInLava())
		{
			setDead();
			return;
		}

		super.onUpdate();

		this.move(MoverType.SELF, motionX, motionY, motionZ);
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block)
	{
	}

	@Override
	protected boolean canTriggerWalking()
	{
		return false;
	}

	@Override
	public boolean handleWaterMovement()
	{
		if (super.handleWaterMovement())
		{
			this.setDead();
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender()
	{
		return 15728880;
	}

	@Override
	public float getBrightness()
	{
		return 1.0F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance)
	{
		return true;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
		lifetime = compound.getInteger("lifetime");
		maxLifetime = compound.getInteger("maxLifetime");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
		compound.setInteger("lifetime", lifetime);
		compound.setInteger("maxLifetime", maxLifetime);
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source)
	{
		return true;
	}
}
