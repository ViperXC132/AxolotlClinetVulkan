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
		RenderLayer renderLayer = model.getLayer(skinTexture);
		var tessellator = Tessellator.getInstance();
		var buf = VertexConsumerProvider.immediate(tessellator.getBuffer());
		model.render(graphics, buf.getBuffer(renderLayer), 15728880, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
		if (cape != null) {
			graphics.translate(0.0F, 0.0F, 0.125F);
			graphics.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(6.0F));
			graphics.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
			model.renderCape(graphics, buf.getBuffer(RenderLayer.getEntitySolid(cape)), 15728880, OverlayTexture.DEFAULT_UV);
		}
		graphics.pop();
		tessellator.draw();
		graphics.pop();
		DiffuseLighting.enableGuiDepthLighting();
	}
}
