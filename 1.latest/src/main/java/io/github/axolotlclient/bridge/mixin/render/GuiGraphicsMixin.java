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

import io.github.axolotlclient.bridge.impl.AxoSpriteImpl;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.render.AxoFont;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.util.HorizontalGradientRectangleRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements AxoRenderContext {
	@Shadow
	@Final
	Minecraft minecraft;

	@Shadow
	@Final
	private Matrix3x2fStack pose;

	@Shadow
	public abstract void enableScissor(int x1, int y1, int x2, int y2);

	@Shadow
	public abstract void disableScissor();

	@Shadow
	public abstract void fill(int x1, int y1, int x2, int y2, int color);

	@Shadow
	public abstract void renderItem(ItemStack par1, int par2, int par3);

	@Shadow
	public abstract void renderItemDecorations(Font par1, ItemStack par2, int par3, int par4, String par5);

	@Shadow
	public abstract void drawString(Font par1, String par2, int par3, int par4, int par5, boolean par6);

	@Shadow
	public abstract void drawString(Font par1, Component par2, int par3, int par4, int par5, boolean par6);

	@Shadow
	public abstract void fillGradient(int startX, int startY, int endX, int endY, int startColor, int endColor);

	@Unique
	private @NotNull GuiGraphics self() {
		return (GuiGraphics) (Object) this;
	}

	@Override
	public void br$popMatrix() {
		pose.popMatrix();
	}

	@Override
	public void br$pushMatrix() {
		pose.pushMatrix();
	}

	@Override
	public void br$scaleMatrix(float sx, float sy) {
		pose.scale(sx, sy);
	}

	@Override
	public void br$translateMatrix(float x, float y) {
		pose.translate(x, y);
	}

	@Override
	public void br$rotateMatrix(float ang) {
		pose.rotate(ang);
	}

	@Override
	public void br$rotateMatrixAround(float ang, float x, float y) {
		pose.rotateAbout(ang, x, y);
	}

	// scissor
	@Override
	public void br$pushScissor(int x, int y, int w, int h) {
		enableScissor(x, y, x + w, y + h);
	}

	@Override
	public void br$popScissor() {
		disableScissor();
	}

	// GL state management
	@Override
	public void br$glEnableBlend() {
		// no-op
	}

	@Override
	public void br$glEnableAlpha() {
	}

	@Override
	public void br$glDisableBlend() {
		// no-op
	}

	@Override
	public void br$glDisableAlpha() {
	}

	@Override
	public void br$glColor4(float r, float g, float b, float a) {
	}

	@Override
	public int br$drawString(String value, int x, int y, int color, boolean shadow) {
		drawString(minecraft.font, value, x, y, color, shadow);
		return x + minecraft.font.width(value);
	}

	@Override
	public int br$drawString(AxoText value, int x, int y, int color, boolean shadow) {
		drawString(minecraft.font, (Component) value, x, y, color, shadow);
		return x + minecraft.font.width((FormattedText) value);
	}

	@Override
	public void br$fillRect(int x, int y, int width, int height, int color) {
		fill(x, y, x + width, y + height, color);
	}

	@Override
	public void br$fillRectGradientVert(int x, int y, int width, int height, int color1, int color2) {
		fillGradient(x, y, x + width, y + height, color1, color2);
	}

	@Override
	public void br$fillRectGradientHoriz(int x, int y, int width, int height, int color1, int color2) {
		HorizontalGradientRectangleRenderState.create(self(), x, y, x + width, y + height, color1, color2).submit();
	}

	@Override
	public void br$fillRectRoundGradient(int x, int y, int width, int height, int colorTopLeft, int colorBottomLeft, int colorBottomRight, int colorTopRight, float roundingPx) {
		self().axolotlclient_rendering$roundedRectGradient(x, y, x + width, y + height, colorTopLeft, colorBottomLeft, colorBottomRight, colorTopRight, roundingPx);
	}

	@Override
	public void br$fillSegment(int x0, int y0, int x1, int y1, int colorX0Y0, int colorX0Y1, int colorX1Y1, int colorX1Y0, float radius) {
		self().axolotlclient_rendering$segment(x0, y0, x1, y1, colorX0Y0, colorX0Y1, colorX1Y1, colorX1Y0, radius);
	}

	@Override
	public void br$outlineRect(int x, int y, int width, int height, int color) {
		DrawUtil.outlineRect(self(), x, y, width, height, color);
	}

	@Override
	public void br$fillRectRound(int x, int y, int width, int height, int color, float rounding) {
		self().axolotlclient_rendering$roundedRect(x, y, x + width, y + height, color, rounding);
	}

	@Override
	public void br$outlineRectRound(int x, int y, int width, int height, int color, float rounding) {
		self().axolotlclient_rendering$outlineRoundedRect(x, y, x + width, y + height, color, rounding, 0.5f);
	}

	@Override
	public void br$drawTexture(int x, int y, int width, int height, AxoSprite sprite) {
		((AxoSpriteImpl) sprite).draw(minecraft, self(), x, y, width, height);
	}

	// item model rendering

	public void br$renderGuiItemModel(AxoItemStack stack, int x, int y) {
		renderItem((ItemStack) stack, x, y);
	}

	public void br$renderGuiItemOverlay(AxoItemStack stack, int x, int y, String countLabel, int textColor,
										boolean shadow) {
		renderItemDecorations(minecraft.font, (ItemStack) stack, x, y, countLabel);
	}

	@ApiStatus.NonExtendable
	public void br$renderGuiItemOverlay(AxoItemStack stack, int x, int y, String countLabel) {
		br$renderGuiItemOverlay(stack, x, y, countLabel, 0xffffffff, true);
	}

	// misc methods
	public AxoFont br$getFont() {
		return minecraft.font;
	}
}
