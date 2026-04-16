package net.trentv.gasesframework.client;

import org.lwjgl.opengl.GL11;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.trentv.gasesframework.api.GasType;
import net.trentv.gasesframework.api.capability.IGasEffects;
import net.trentv.gasesframework.common.block.BlockGas;
import net.trentv.gasesframework.common.capability.GasEffectsProvider;

public class ClientEvents
{
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFogDensity(FogDensity event)
	{
		Entity a = event.getEntity();
		if (a.hasCapability(GasEffectsProvider.GAS_EFFECTS, null))
		{
			IGasEffects q = a.getCapability(GasEffectsProvider.GAS_EFFECTS, null);
			float f = q.getBlindness() / 250.0f;

			if (f >= 0.1f)
			{
				event.setDensity(f * f);
				GlStateManager.setFog(GlStateManager.FogMode.EXP);
				event.setCanceled(true);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFogColor(FogColors event)
	{
		Entity a = event.getEntity();
		if (a.hasCapability(GasEffectsProvider.GAS_EFFECTS, null))
		{
			IGasEffects q = a.getCapability(GasEffectsProvider.GAS_EFFECTS, null);

			float f = 1 - (q.getBlindness() / 250);
			event.setRed(event.getRed() * f);
			event.setGreen(event.getGreen() * f);
			event.setBlue(event.getBlue() * f);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderOverlay(RenderGameOverlayEvent.Pre event)
	{
		if (event.getType() != RenderGameOverlayEvent.ElementType.HELMET) return;

		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		if (player == null) return;

		BlockPos eyePos = new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		IBlockState eyeState = player.world.getBlockState(eyePos);
		if (!(eyeState.getBlock() instanceof BlockGas blockGas)) return;

		TextureAtlasSprite sprite = mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(eyeState);
		int capacity = eyeState.getValue(BlockGas.CAPACITY);
		float alpha = (capacity / 16.0f) * 0.6f;
		renderGasOverlay(event.getResolution(), blockGas.gasType, sprite, alpha);
	}

	@SideOnly(Side.CLIENT)
	private void renderGasOverlay(ScaledResolution resolution, GasType gasType, TextureAtlasSprite sprite, float alpha)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		int color = gasType.color;
		float red = ((color >> 16) & 0xFF) / 255.0f;
		float green = ((color >> 8) & 0xFF) / 255.0f;
		float blue = (color & 0xFF) / 255.0f;

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(red, green, blue, alpha);
		GlStateManager.disableAlpha();
		GlStateManager.depthMask(false);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		int width = resolution.getScaledWidth();
		int height = resolution.getScaledHeight();

		float u0 = sprite.getMinU();
		float u1 = sprite.getMaxU();
		float v0 = sprite.getMinV();
		float v1 = sprite.getMaxV();

		buffer.pos(0, height, -90).tex(u0, v1).endVertex();
		buffer.pos(width, height, -90).tex(u1, v1).endVertex();
		buffer.pos(width, 0, -90).tex(u1, v0).endVertex();
		buffer.pos(0, 0, -90).tex(u0, v0).endVertex();

		tessellator.draw();

		GlStateManager.depthMask(true);
		GlStateManager.enableAlpha();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.disableBlend();
	}
}
