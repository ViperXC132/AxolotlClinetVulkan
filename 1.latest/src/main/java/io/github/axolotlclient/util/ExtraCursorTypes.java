package io.github.axolotlclient.util;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import org.lwjgl.glfw.GLFW;

public final class ExtraCursorTypes {
	public static final CursorType RESIZE_NWSE = CursorType.createStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR, "resize_nwse", CursorTypes.POINTING_HAND),
		RESIZE_NESW = CursorType.createStandardCursor(GLFW.GLFW_RESIZE_NESW_CURSOR, "resize_nesw", CursorTypes.POINTING_HAND);
}
