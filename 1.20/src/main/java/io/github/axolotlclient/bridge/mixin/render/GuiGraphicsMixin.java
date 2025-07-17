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
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.render.AxoFont;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements AxoRenderContext {
	@Shadow
	@Final
	private MatrixStack matrices;

	@Shadow
	public abstract void enableScissor(int x1, int y1, int x2, int y2);

	@Shadow
	public abstract void disableScissor();

	@Shadow
	public abstract int drawText(TextRenderer renderer, Text text, int x, int y, int color, boolean shadowed);

	@Shadow
	public abstract void fill(int x1, int y1, int x2, int y2, int color);

	@Shadow
	public abstract void drawItem(ItemStack stack, int x, int y);

	@Shadow
	public abstract void drawItemInSlot(TextRenderer textRenderer, ItemStack stack, int x, int y,
										@Nullable String countOverride);

	@Override
	public void br$popMatrix() {
		matrices.pop();
	}

	@Override
	public void br$pushMatrix() {
		matrices.push();
	}

	@Override
	public void br$scaleMatrix(float sx, float sy, float sz) {
		matrices.scale(sx, sy, sz);
	}

	@Override
	public void br$translateMatrix(float x, float y, float z) {
		matrices.translate(x, y, z);
	}

	// scissor
	@Override
	public void br$pushScissor(int x, int y, int w, int h) {
		enableScissor(x, y, w, h);
	}

	@Override
	public void br$popScissor() {
		disableScissor();
	}

	// GL state management
	@Override
	public void br$glEnableBlend() {
		RenderSystem.enableBlend();
	}

	@Override
	public void br$glEnableAlpha() {
	}

	@Override
	public void br$glDisableBlend() {
		RenderSystem.disableBlend();
	}

	@Override
	public void br$glDisableAlpha() {
	}

	@Override
	public void br$glColor4(float r, float g, float b, float a) {
	}

	@Override
	public int br$drawString(String value, int x, int y, int color, boolean shadow) {
		return drawText(MinecraftClient.getInstance().textRenderer, Text.of(value), x, y, color, shadow);
	}

	@Override
	public int br$drawString(AxoText value, int x, int y, int color, boolean shadow) {
		return drawText(MinecraftClient.getInstance().textRenderer, (Text) value, x, y, color, shadow);
	}

	@Override
	public void br$fillRect(int x, int y, int width, int height, int color) {
		fill(x, y, width, height, color);
	}

	@Override
	public void br$outlineRect(int x, int y, int width, int height, int color) {
		DrawUtil.outlineRect((GuiGraphics) (Object) this, x, y, width, height, color);
	}

	@Override
	public void br$drawTexture(int x, int y, int width, int height, AxoSprite sprite) {
		((AxoSpriteImpl) sprite).draw(MinecraftClient.getInstance(), (GuiGraphics) (Object) this, x, y, width, height);
	}

	// item model rendering

	public void br$renderGuiItemModel(AxoItemStack stack, int x, int y) {
		drawItem((ItemStack) stack, x, y);
	}

	public void br$renderGuiItemOverlay(AxoItemStack stack, int x, int y, String countLabel, int textColor,
										boolean shadow) {
		drawItemInSlot(MinecraftClient.getInstance().textRenderer, (ItemStack) stack, x, y, countLabel);
	}

	@ApiStatus.NonExtendable
	public void br$renderGuiItemOverlay(AxoItemStack stack, int x, int y, String countLabel) {
		br$renderGuiItemOverlay(stack, x, y, countLabel, 0xffffffff, true);
	}

	// misc methods
	public AxoFont br$getFont() {
		return MinecraftClient.getInstance().textRenderer;
	}
}
