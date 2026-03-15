/*
 * Copyright © 2026 moehreag <moehreag@gmail.com> & Contributors
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

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public final class CursorTypes {
	public static final long DEFAULT = 0L;
	public static final long ARROW = createStandardCursor(GLFW.GLFW_ARROW_CURSOR, DEFAULT);
	public static final long IBEAM = createStandardCursor(GLFW.GLFW_IBEAM_CURSOR, DEFAULT);
	public static final long CROSSHAIR = createStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR, DEFAULT);
	public static final long POINTING_HAND = createStandardCursor(GLFW.GLFW_POINTING_HAND_CURSOR, DEFAULT);
	public static final long RESIZE_NS = createStandardCursor(GLFW.GLFW_RESIZE_NS_CURSOR, DEFAULT);
	public static final long RESIZE_EW = createStandardCursor(GLFW.GLFW_RESIZE_EW_CURSOR, DEFAULT);
	public static final long RESIZE_ALL = createStandardCursor(GLFW.GLFW_RESIZE_ALL_CURSOR, DEFAULT);
	public static final long NOT_ALLOWED = createStandardCursor(GLFW.GLFW_NOT_ALLOWED_CURSOR, DEFAULT);
	public static final long RESIZE_NWSE = createStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR, POINTING_HAND),
		RESIZE_NESW = createStandardCursor(GLFW.GLFW_RESIZE_NESW_CURSOR, POINTING_HAND);

	private static long currentCursor = ARROW;

	public static void request(long cursor) {
		if (cursor != currentCursor) {
			currentCursor = cursor;
			GLFW.glfwSetCursor(MinecraftClient.getInstance().getWindow().getHandle(), cursor);
		}
	}

	private static long createStandardCursor(int id, long fallback) {
		long l = GLFW.glfwCreateStandardCursor(id);
		return l == 0L ? fallback : l;
	}
}
