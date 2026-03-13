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

package io.github.axolotlclient.util;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.function.Supplier;

import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Graphics;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.mixin.DynamicTextureAccessor;
import io.github.axolotlclient.mixin.MinecraftClientAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Window;
import net.minecraft.client.render.texture.DynamicTexture;
import net.minecraft.resource.Identifier;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

public class Util {
	@ApiStatus.Internal
	public static Window window;

	/**
	 * Gets the amount of ticks in between start and end, on a 24000 tick system.
	 *
	 * @param start The start of the time you wish to measure
	 * @param end   The end of the time you wish to measure
	 * @return The amount of ticks in between start and end
	 */
	public static int getTicksBetween(int start, int end) {
		if (end < start)
			end += 24000;
		return end - start;
	}

	public static int toGlCoordsX(int x) {
		if (window == null) {
			window = new Window(Minecraft.getInstance());
		}
		return x * window.getScale();
	}

	public static int toGlCoordsY(int y) {
		if (window == null) {
			window = new Window(Minecraft.getInstance());
		}
		int scale = window.getScale();
		return Minecraft.getInstance().height - y * scale - scale;
	}

	public static int toMCCoordsX(int x) {
		if (window == null) {
			window = new Window(Minecraft.getInstance());
		}
		return x * window.getWidth() / Minecraft.getInstance().width;
	}

	public static int toMCCoordsY(int y) {
		if (window == null) {
			window = new Window(Minecraft.getInstance());
		}
		return window.getHeight() - y * window.getHeight() / Minecraft.getInstance().height - 1;
	}

	public static Window getWindow() {
		if (window == null) {
			try {
				window = new Window(Minecraft.getInstance());
			} catch (Exception e) {
				return null;
			}
		}
		return window;
	}

	public static void addMessageToChatHud(Text msg) {
		Minecraft.getInstance().gui.getChat().addMessage(msg);
	}

	public static String splitAtCapitalLetters(String string) {
		if (string == null || string.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (char c : string.toCharArray()) {
			if (Character.isUpperCase(c) && c != string.charAt(0)) {
				builder.append(" ");
			}
			builder.append(c);
		}
		return builder.toString();
	}

	public static <T> T make(Supplier<T> factory) {
		return factory.get();
	}

	public static boolean currentServerAddressContains(String address) {
		if (Minecraft.getInstance().isInSingleplayer()
			|| Minecraft.getInstance().isIntegratedServerRunning()) {
			return false;
		}
		if (Minecraft.getInstance().getCurrentServerEntry() != null) {
			return Minecraft.getInstance().getCurrentServerEntry().address.contains(address);
		}
		return ((MinecraftClientAccessor) Minecraft.getInstance()).getServerAddress() != null
			&& ((MinecraftClientAccessor) Minecraft.getInstance()).getServerAddress().contains(address);
	}

	public static float lerp(float start, float end, float percent) {
		return start + ((end - start) * percent);
	}

	public static Identifier getTexture(GraphicsOption option) {
		return getTexture(option.get(), "graphics_" + option.getName());
	}

	public static Identifier getTexture(Graphics graphics, String name) {
		Identifier id = new Identifier("axolotlclient", name.toLowerCase(Locale.ROOT));
		try {
			DynamicTexture texture;
			var previous = Minecraft.getInstance().getTextureManager().get(id);
			if (previous == null || (previous instanceof DynamicTextureAccessor tex && (tex.getHeight() != graphics.getHeight() || tex.getWidth() != graphics.getWidth()))) {
				texture = new DynamicTexture(ImageIO.read(new ByteArrayInputStream(graphics.getPixelData())));
				Minecraft.getInstance().getTextureManager().register(id, texture);
			} else {
				texture = (DynamicTexture) Minecraft.getInstance().getTextureManager().get(id);
				int[] pix = texture.getPixels();
				for (int x = 0; x < graphics.getWidth(); x++) {
					for (int y = 0; y < graphics.getHeight(); y++) {
						int rows = (y) * graphics.getWidth() + x;
						pix[rows] = graphics.getPixelColor(x, y);
					}
				}
			}

			texture.upload();
		} catch (IOException e) {
			AxolotlClientCommon.getInstance().getLogger().error("Failed to bind texture for " + id + ": ", e);
		}
		return id;
	}

	public static void bindTexture(GraphicsOption option) {
		Identifier id = getTexture(option);
		Minecraft.getInstance().getTextureManager().bind(id);
	}

	public static String getFormatCode(Color color) {
		return String.format("§#%06X", color.getRed() << 16 | color.getGreen() << 8 | color.getBlue());
	}
}
