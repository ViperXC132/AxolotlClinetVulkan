package io.github.axolotlclient.bridge.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.render.AxoTextRenderer;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resource.Identifier;
import org.jetbrains.annotations.Nullable;

public class AxoRenderContextImpl implements AxoRenderContext {
	@Nullable
	private static AxoRenderContextImpl INSTANCE;

	public static AxoRenderContext getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AxoRenderContextImpl();
		}

		return INSTANCE;
	}

	private final Minecraft client = Minecraft.getInstance();

	@Override
	public void popMatrix() {
		GlStateManager.popMatrix();
	}

	@Override
	public void pushMatrix() {
		GlStateManager.pushMatrix();
	}

	@Override
	public void scaleMatrix(float sx, float sy, float sz) {
		GlStateManager.scalef(sx, sy, sz);
	}

	@Override
	public void translateMatrix(float x, float y, float z) {
		GlStateManager.translatef(x, y, z);
	}

	@Override
	public void glEnableTexture() {
		GlStateManager.enableTexture();
	}

	@Override
	public void glEnableBlend() {
		GlStateManager.enableBlend();
	}

	@Override
	public void glColor4(float r, float g, float b, float a) {
		GlStateManager.color4f(r, g, b, a);
	}

	@Override
	public void fillRect(int x, int y, int width, int height, int color) {
		DrawUtil.fillRect(x, y, width, height, color);
	}

	@Override
	public void outlineRect(int x, int y, int width, int height, int color) {
		DrawUtil.outlineRect(x, y, width, height, color);
	}

	@Override
	public void drawTexture(int sx, int sy, int tx, int ty, int sw, int sh, int tw, int th, AxoIdentifier texture) {
		client.getTextureManager().bind((Identifier) texture);
		DrawUtil.drawTexture(sx, sy, tx, ty, sh, sw, th, tw);
	}

	@Override
	public AxoTextRenderer getTextRenderer() {
		return client.textRenderer;
	}

	@Override
	public void renderGuiItemModel(AxoItemStack stack, int x, int y) {
		Minecraft.getInstance().getItemRenderer().renderGuiItemModel(Bridge.unwrapStack(stack), x, y);
	}

	@Override
	public void renderGuiItemOverlay(AxoItemStack stack, int x, int y, String countLabel, int textColor, boolean shadow) {
		ItemUtil.renderGuiItemOverlay(client.textRenderer, Bridge.unwrapStack(stack), x, y, countLabel, textColor, shadow);
	}
}
