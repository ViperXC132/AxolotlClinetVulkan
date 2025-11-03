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

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class LoadingScreen extends Screen {
	private final Text description;

	public LoadingScreen(Text title, Text description) {
		super(title);
		this.description = description;
	}

	@Override
	public void render(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		renderBackground(graphics);
		drawCenteredText(graphics, textRenderer, getTitle(), width / 2, 33 / 2 - textRenderer.fontHeight / 2, -1);
		int centerX = width / 2;
		int centerY = height / 2;
		var text = description;
		textRenderer.draw(graphics, text, centerX - textRenderer.getWidth(text) / 2f, centerY - 9, -1);
		String string = switch ((int) (Util.getMeasuringTimeMs() / 300L % 4L)) {
			case 1, 3 -> "o O o";
			case 2 -> "o o O";
			default -> "O o o";
		};
		textRenderer.draw(graphics, string, centerX - textRenderer.getWidth(string) / 2f, centerY + 9, 0xFF808080);
	}
}
