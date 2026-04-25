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

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * <p>License: GPL-3.0</p>
 */

public class DrawUtil {

	public static void fillRect(GuiGraphicsExtractor graphics, Rectangle rectangle, Color color) {
		fillRect(graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, color.toInt());
	}

	public static void fillRect(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
		graphics.fill(x, y, x + width, y + height, color);
	}

	public static void outlineRect(GuiGraphicsExtractor graphics, Rectangle rectangle, Color color) {
		outlineRect(graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, color.toInt());
	}

	public static void outlineRect(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
		graphics.fill(x, y, x + width, y + 1, color);
		graphics.fill(x, y + height - 1, x + width, y + height, color);
		graphics.fill(x, y + 1, x + 1, y + height - 1, color);
		graphics.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
	}

	public static void drawCenteredString(GuiGraphicsExtractor graphics, Font renderer, String text, int x, int y, Color color, boolean shadow) {
		drawCenteredString(graphics, renderer, text, x, y, color.toInt(), shadow);
	}

	public static void drawCenteredString(GuiGraphicsExtractor graphics, Font renderer, String text, int x, int y, int color, boolean shadow) {
		if (shadow) {
			graphics.centeredText(renderer, text, x, y, color);
		} else graphics.text(renderer, text, (x - renderer.width(text) / 2), y, color);
	}

	public static int drawString(GuiGraphicsExtractor graphics, String text, int x, int y, int color, boolean shadow) {
		graphics.text(Minecraft.getInstance().font, text, x, y, color, shadow);
		return x + Minecraft.getInstance().font.width(text);
	}

	public static int drawString(GuiGraphicsExtractor graphics, String text, int x, int y, Color color, boolean shadow) {
		return drawString(graphics, text, x, y, color.toInt(), shadow);
	}
}
