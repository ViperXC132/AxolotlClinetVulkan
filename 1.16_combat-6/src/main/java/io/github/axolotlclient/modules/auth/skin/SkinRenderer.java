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

package io.github.axolotlclient.modules.auth.skin;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class SkinRenderer {
	private static PlayerEntityModel<?> classicModel, slimModel;

	private SkinRenderer() {
	}

	public static void render(MatrixStack graphics, boolean classicVariant,
							  Identifier skinTexture,
							  @Nullable Identifier cape,
							  float rotationX,
							  float rotationY,
							  float pivotY,
							  int x0,
							  int y0,
							  int x1,
							  int y1,
							  float scale) {
		if (classicModel == null && classicVariant) {
			classicModel = new PlayerEntityModel<>(0, false);
			classicModel.child = false;
		}
		if (slimModel == null && !classicVariant) {
			slimModel = new PlayerEntityModel<>(0, true);
			slimModel.child = false;
		}

		int width = x1 - x0;
		DiffuseLighting.disable();
		graphics.push();
		graphics.translate(x0 + width / 2.0F, (float) (y1), 100.0F);
		graphics.scale(scale, scale, scale);
		graphics.translate(0.0F, -0.0625F, 0.0F);
		graphics.translate(0, pivotY, 0);
		graphics.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(rotationX));
		graphics.translate(0, -pivotY, 0);
		graphics.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotationY));
		graphics.push();
		graphics.scale(1.0F, 1.0F, -1.0F);
		graphics.translate(0.0F, -1.5F, 0.0F);
		var model = classicVariant ? classicModel : slimModel;
		var tessellator = Tessellator.getInstance();
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.enableTexture();
		MinecraftClient.getInstance().getTextureManager().bindTexture(skinTexture);
		var consumer = VertexConsumerProvider.immediate(tessellator.getBuffer()).getBuffer(model.getLayer(skinTexture));
		Consumer<ModelPart> renderModelPart = m -> m.render(graphics, consumer, 15728880, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
		renderModelPart.accept(model.head);
		renderModelPart.accept(model.torso);
		renderModelPart.accept(model.rightArm);
		renderModelPart.accept(model.leftArm);
		renderModelPart.accept(model.rightLeg);
		renderModelPart.accept(model.leftLeg);
		renderModelPart.accept(model.helmet);
		renderModelPart.accept(model.leftPantLeg);
		renderModelPart.accept(model.rightPantLeg);
		renderModelPart.accept(model.leftSleeve);
		graphics.translate(0, 0, -0.62f);
		renderModelPart.accept(model.rightSleeve);
		graphics.translate(0, 0, 0.62f);
		renderModelPart.accept(model.jacket);
		tessellator.draw();
		if (cape != null) {
			graphics.push();
			MinecraftClient.getInstance().getTextureManager().bindTexture(cape);
			graphics.translate(0.0F, 0.0F, 0.125F);
			graphics.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(6.0F));
			graphics.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
			model.renderCape(graphics, VertexConsumerProvider.immediate(tessellator.getBuffer()).getBuffer(RenderLayer.getEntitySolid(cape)), 15728880, OverlayTexture.DEFAULT_UV);
			tessellator.draw();
			graphics.pop();
		}
		graphics.pop();

		graphics.pop();
		RenderSystem.disableBlend();
		RenderSystem.disableDepthTest();
		DiffuseLighting.enableGuiDepthLighting();
	}
}
