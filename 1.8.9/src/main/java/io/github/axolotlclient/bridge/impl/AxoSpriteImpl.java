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
import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.resource.Identifier;

public interface AxoSpriteImpl extends AxoSprite {
	void draw(Minecraft client, int sX, int sY, int sW, int sH);

	record Simple(Identifier id, int x, int y, int width, int height) implements AxoSpriteImpl {
		@Override
		public void draw(Minecraft client, int sX, int sY, int sW, int sH) {
			GlStateManager.color3f(1, 1, 1);
			client.getTextureManager().bind(id);
			GuiElement.drawTexture(sX, sY, x, y, sW, sH, width, height);
		}
	}

	record Config(GraphicsOption option) implements AxoSpriteImpl {
		@Override
		public void draw(Minecraft client, int sX, int sY, int sW, int sH) {
			GlStateManager.color3f(1, 1, 1);
			Util.bindTexture(option);
			GuiElement.drawTexture(sX, sY, 0, 0, sW, sH, option.get().getWidth(), option.get().getHeight());
		}
	}
}
