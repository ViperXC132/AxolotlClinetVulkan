/*
 * Copyright © 2025 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.bridge.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.render.AxoFont;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.text.Text;
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
	public void br$popMatrix() {
		GlStateManager.popMatrix();
	}

	@Override
	public void br$pushMatrix() {
		GlStateManager.pushMatrix();
	}

	@Override
	public void br$scaleMatrix(float sx, float sy, float sz) {
		GlStateManager.scalef(sx, sy, sz);
	}

	@Override
	public void br$translateMatrix(float x, float y, float z) {
		GlStateManager.translatef(x, y, z);
	}

	@Override
	public void br$pushScissor(int x, int y, int w, int h) {
		io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil.pushScissor(x, y, w, h);
	}

	@Override
	public void br$popScissor() {
		io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil.popScissor();
	}

	@Override
	public void br$glEnableBlend() {
		GlStateManager.enableBlend();
	}

	@Override
	public void br$glEnableAlpha() {
		GlStateManager.enableAlphaTest();
	}

	@Override
	public void br$glDisableBlend() {
		GlStateManager.disableBlend();
	}

	@Override
	public void br$glDisableAlpha() {
		GlStateManager.disableAlphaTest();
	}

	@Override
	public void br$glColor4(float r, float g, float b, float a) {
		GlStateManager.color4f(r, g, b, a);
	}

	@Override
	public void br$fillRect(int x, int y, int width, int height, int color) {
		DrawUtil.fillRect(x, y, width, height, color);
	}

	@Override
	public void br$outlineRect(int x, int y, int width, int height, int color) {
		DrawUtil.outlineRect(x, y, width, height, color);
	}

	@Override
	public void br$drawTexture(int x, int y, int width, int height, AxoSprite sprite) {
		((AxoSpriteImpl) sprite).draw(client, x, y, width, height);
	}

	@Override
	public int br$drawString(String value, int x, int y, int color, boolean shadow) {
		return client.textRenderer.draw(value, x, y, color, shadow);
	}

	@Override
	public int br$drawString(AxoText value, int x, int y, int color, boolean shadow) {
		return br$drawString(((Text) value).getFormattedString(), x, y, color, shadow);
	}

	@Override
	public void br$drawCenteredString(String value, int x, int y, int color, boolean shadow) {
		AxoRenderContext.super.br$drawCenteredString(value, x, y, color, shadow);
	}

	@Override
	public AxoFont br$getFont() {
		return client.textRenderer;
	}

	@Override
	public void br$renderGuiItemModel(AxoItemStack stack, int x, int y) {
		final var vanilla = Bridge.unwrapStack(stack);

		if(vanilla != null) {
			ItemUtil.renderGuiItemModel(vanilla, x, y);
		}
	}

	@Override
	public void br$renderGuiItemOverlay(AxoItemStack stack, int x, int y, String countLabel, int textColor, boolean shadow) {
		ItemUtil.renderGuiItemOverlay(
			client.textRenderer,
			Bridge.unwrapStack(stack), x, y, countLabel, textColor,
			shadow
		);
	}
}
