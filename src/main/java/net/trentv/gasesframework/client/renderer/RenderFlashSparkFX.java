package net.trentv.gasesframework.client.renderer;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.trentv.gasesframework.common.entity.EntityFlashSparkFX;

@SideOnly(Side.CLIENT)
public class RenderFlashSparkFX extends Render<EntityFlashSparkFX>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("gasesframework", "textures/effects/flash_spark.png");

	public RenderFlashSparkFX(RenderManager renderManager)
	{
		super(renderManager);
	}

	@Override
	public void doRender(EntityFlashSparkFX entity, double x, double y, double z, float entityYaw, float partialTick)
	{
		double xPos = entity.posX - renderManager.viewerPosX;
		double yPos = entity.posY - renderManager.viewerPosY;
		double zPos = entity.posZ - renderManager.viewerPosZ;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();

		buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(xPos, yPos, zPos).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();

		for (int i = 0; i < 5; i++)
		{
			xPos -= entity.prevMotionX[i];
			yPos -= entity.prevMotionY[i];
			zPos -= entity.prevMotionZ[i];
			float alpha = (5 - i) / 5.0F;
			buffer.pos(xPos, yPos, zPos).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
		}

		tessellator.draw();

		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	@Override
	@Nullable
	protected ResourceLocation getEntityTexture(EntityFlashSparkFX entity)
	{
		return TEXTURE;
	}
}
