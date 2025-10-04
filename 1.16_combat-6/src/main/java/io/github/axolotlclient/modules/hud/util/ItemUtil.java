/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.modules.hud.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.util.ClientColors;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * <p>License: GPL-3.0</p>
 */

@SuppressWarnings("deprecation")
@UtilityClass
public class ItemUtil {
	public void renderGuiItemModel(float scale, ItemStack stack, float x, float y) {
		DiffuseLighting.disable();
		MinecraftClient client = MinecraftClient.getInstance();
		BakedModel model = client.getItemRenderer().getHeldItemModel(stack, null, null);
		RenderSystem.pushMatrix();
		RenderSystem.scalef(scale, scale, 1);
		client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
		//noinspection DataFlowIssue
		client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
		RenderSystem.enableRescaleNormal();
		RenderSystem.enableAlphaTest();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.translatef(x, y, 100.0F + client.getItemRenderer().zOffset);
		RenderSystem.translatef(8.0F, 8.0F, 0.0F);
		RenderSystem.scalef(1.0F, -1.0F, 1.0F);
		RenderSystem.scalef(16.0F, 16.0F, 16.0F);
		MatrixStack matrixStack = new MatrixStack();
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		boolean bl = !model.isSideLit();
		if (bl) {
			DiffuseLighting.disableGuiDepthLighting();
		}

		MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack, immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
		immediate.draw();
		RenderSystem.enableDepthTest();
		if (bl) {
			DiffuseLighting.enableGuiDepthLighting();
		}

		RenderSystem.disableAlphaTest();
		RenderSystem.disableRescaleNormal();
		RenderSystem.popMatrix();
		DiffuseLighting.disable();
	}

	public static void renderGuiItemOverlay(MatrixStack matrices, TextRenderer renderer, ItemStack stack, int x, int y,
											String countLabel, int textColor, boolean shadow) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (stack.isEmpty()) {
			return;
		}

		if (stack.getCount() != 1 || countLabel != null) {
			String string = countLabel == null ? String.valueOf(stack.getCount()) : countLabel;
			matrices.translate(0.0, 0.0, client.getItemRenderer().zOffset + 200.0F);
			DrawUtil.drawString(matrices, string, (x + 19 - 2 - renderer.getWidth(string)), (y + 6 + 3), textColor,
				shadow);
		}

		if (stack.isDamaged()) {
			RenderSystem.disableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.disableBlend();
			float f = (float) stack.getDamage();
			float g = (float) stack.getMaxDamage();
			float h = Math.max(0.0F, (g - f) / g);
			int i = Math.round(13.0F - f * 13.0F / g);
			int j = MathHelper.hsvToRgb(h / 3.0F, 1.0F, 1.0F);
			DrawUtil.fillRect(matrices, x + 2, y + 13, 13, 2, ClientColors.BLACK.toInt());
			DrawUtil.fillRect(matrices, x + 2, y + 13, i, 1,
				(((255 << 8) + (j >> 16 & 255) << 8) + (j >> 8 & 255) << 8) + (j & 255));
			RenderSystem.enableBlend();
			RenderSystem.enableTexture();
			RenderSystem.enableDepthTest();
		}

		ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
		float f = clientPlayerEntity == null ? 0.0F
			: clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(),
			MinecraftClient.getInstance().getTickDelta());
		if (f > 0.0F) {
			RenderSystem.disableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			DrawUtil.fillRect(matrices, x, y + MathHelper.floor(16.0F * (1.0F - f)), 16, MathHelper.ceil(16.0F * f),
				ClientColors.WHITE.withAlpha(127).toInt());
			RenderSystem.enableTexture();
			RenderSystem.enableDepthTest();
		}
	}
}
