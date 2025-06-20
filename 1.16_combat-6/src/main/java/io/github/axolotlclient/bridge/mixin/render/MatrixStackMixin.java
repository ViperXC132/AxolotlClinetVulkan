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

package io.github.axolotlclient.bridge.mixin.render;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.bridge.impl.AxoSpriteImpl;
import io.github.axolotlclient.bridge.impl.Bridge;
import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.render.AxoFont;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MatrixStack.class)
public abstract class MatrixStackMixin implements AxoRenderContext {

	@Shadow
	public abstract void pop();

	@Shadow
	public abstract void push();

	@Shadow
	public abstract void scale(float x, float y, float z);

	@Shadow
	public abstract void translate(double x, double y, double z);

	// Matrix management
	@Override
	public void br$popMatrix() {
		pop();
	}

	@Override
	public void br$pushMatrix() {
		push();
	}

	@Override
	public void br$scaleMatrix(float sx, float sy, float sz) {
		scale(sx, sy, sz);
	}

	@Override
	public void br$translateMatrix(float x, float y, float z) {
		translate(x, y, z);
	}

	// scissor
	public void br$pushScissor(int x, int y, int w, int h) {
		io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil.pushScissor(x, y, w, h);
	}

	public void br$popScissor() {
		io.github.axolotlclient.AxolotlClientConfig.impl.util.DrawUtil.popScissor();
	}

	// GL state management
	public void br$glEnableBlend() {
		RenderSystem.enableBlend();
	}

	public void br$glEnableAlpha() {
		RenderSystem.enableAlphaTest();
	}

	public void br$glDisableBlend() {
		RenderSystem.disableBlend();
	}

	public void br$glDisableAlpha() {
		RenderSystem.disableAlphaTest();
	}

	public void br$glColor4(float r, float g, float b, float a) {
		RenderSystem.color4f(r, g, b, a);
	}

	// string rendering
	public int br$drawString(String value, int x, int y, int color, boolean shadow) {
		return DrawUtil.drawString(((MatrixStack) (Object) this), value, x, y, color, shadow);
	}

	@Override
	public int br$drawString(AxoText value, int x, int y, int color, boolean shadow) {
		return DrawUtil.drawText(((MatrixStack) (Object) this), (Text) value, x, y, color, shadow);
	}

	public void br$fillRect(int x, int y, int width, int height, int color) {
		DrawUtil.fillRect(((MatrixStack) (Object) this), x, y, width, height, color);
	}

	public void br$outlineRect(int x, int y, int width, int height, int color) {
		DrawUtil.outlineRect(((MatrixStack) (Object) this), x, y, width, height, color);
	}

	// texture drawing
	public void br$drawTexture(int x, int y, int width, int height, AxoSprite sprite) {
		((AxoSpriteImpl) sprite).draw(MinecraftClient.getInstance(), ((MatrixStack) (Object) this), x, y, width,
			height);
	}

	// item model rendering

	@Override
	public void br$renderGuiItemModel(AxoItemStack stack, int x, int y) {
		// TODO: apply matrixstack transforms here?
		MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon((ItemStack) stack, x, y);
	}

	@Override
	public void br$renderGuiItemOverlay(AxoItemStack stack, int x, int y, String countLabel, int textColor, boolean shadow) {
		ItemUtil.renderGuiItemOverlay(
			((MatrixStack) (Object) (this)),
			MinecraftClient.getInstance().textRenderer,
			(ItemStack) stack, x, y, countLabel, textColor,
			shadow
		);
	}

	// misc methods
	public AxoFont br$getFont() {
		return MinecraftClient.getInstance().textRenderer;
	}
}
