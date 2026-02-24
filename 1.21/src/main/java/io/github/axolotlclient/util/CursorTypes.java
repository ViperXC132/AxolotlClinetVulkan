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
