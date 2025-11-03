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

package io.github.axolotlclient.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.sdl.SDLMouse;

public sealed abstract class WindowAccess permits WindowAccess.GLFWAccess, WindowAccess.SDLAccess, WindowAccess.NoOpAccess {

	private static final boolean GLFW_AVAILABLE = checkGlfwAvailable();
	private static final boolean SDL_AVAILABLE = checkSDLAvailable();

	public static boolean isGlfwAvailable() {
		return WindowAccess.GLFW_AVAILABLE;
	}

	public static boolean isSdlAvailable() {
		return WindowAccess.SDL_AVAILABLE;
	}

	public enum Cursor {
		RESIZE_ALL,
		ARROW,
		RESIZE_NWSE,
		RESIZE_NESW
	}

	public abstract long createCursor(Cursor cursor);

	public abstract void setCursor(long cursor);

	public abstract void destroyStandardCursor(long... cursors);

	public abstract boolean rawMouseMotionAvailable();

	public abstract void setRawMouseMotion(boolean enabled);

	private static final WindowAccess INSTANCE = create();

	public static WindowAccess getInstance() {
		return INSTANCE;
	}

	private static boolean checkGlfwAvailable() {
		try {
			Class.forName("org.lwjgl.glfw.GLFW");
			return true;
		} catch (Throwable ignored) {
		}
		return false;
	}

	private static boolean checkSDLAvailable() {
		try {
			Class.forName("org.lwjgl.sdl.SDL");
			return true;
		} catch (Throwable ignored) {
		}
		return false;
	}

	private static WindowAccess create() {
		if (GLFW_AVAILABLE) {
			return new GLFWAccess();
		} else if (SDL_AVAILABLE) {
			return new SDLAccess();
		}
		return new NoOpAccess();
	}

	final static class NoOpAccess extends WindowAccess {

		@Override
		public long createCursor(Cursor cursor) {
			return 0;
		}

		@Override
		public void setCursor(long cursor) {

		}

		@Override
		public void destroyStandardCursor(long... cursors) {

		}

		@Override
		public boolean rawMouseMotionAvailable() {
			return false;
		}

		@Override
		public void setRawMouseMotion(boolean enabled) {

		}
	}

	final static class SDLAccess extends WindowAccess {

		@Override
		public long createCursor(Cursor cursor) {
			return SDLMouse.SDL_CreateSystemCursor(switch (cursor) {
				case RESIZE_ALL -> SDLMouse.SDL_SYSTEM_CURSOR_MOVE;
				case ARROW -> SDLMouse.SDL_SYSTEM_CURSOR_DEFAULT;
				case RESIZE_NWSE -> SDLMouse.SDL_SYSTEM_CURSOR_NWSE_RESIZE;
				case RESIZE_NESW -> SDLMouse.SDL_SYSTEM_CURSOR_NESW_RESIZE;
			});
		}

		@Override
		public void setCursor(long cursor) {
			SDLMouse.SDL_SetCursor(cursor);
		}

		@Override
		public void destroyStandardCursor(long... cursors) {
			for (long c : cursors) {
				SDLMouse.SDL_DestroyCursor(c);
			}
		}

		@Override
		public boolean rawMouseMotionAvailable() {
			return false;
		}

		@Override
		public void setRawMouseMotion(boolean enabled) {

		}
	}

	final static class GLFWAccess extends WindowAccess {

		@Override
		public long createCursor(Cursor cursor) {
			return GLFW.glfwCreateStandardCursor(switch (cursor) {
				case RESIZE_ALL -> GLFW.GLFW_RESIZE_ALL_CURSOR;
				case ARROW -> GLFW.GLFW_ARROW_CURSOR;
				case RESIZE_NWSE -> GLFW.GLFW_RESIZE_NWSE_CURSOR;
				case RESIZE_NESW -> GLFW.GLFW_RESIZE_NESW_CURSOR;
			});
		}

		@Override
		public void setCursor(long cursor) {
			GLFW.glfwSetCursor(WindowHandleAccess.getWindowHandle(), cursor);
		}

		@Override
		public void destroyStandardCursor(long... cursors) {
			// we do not need to destroy glfw standard cursors ourselves
		}

		@Override
		public boolean rawMouseMotionAvailable() {
			return WindowHandleAccess.isHandleAvailable();
		}

		@Override
		public void setRawMouseMotion(boolean enabled) {
			GLFW.glfwSetInputMode(WindowHandleAccess.getWindowHandle(), GLFW.GLFW_RAW_MOUSE_MOTION, enabled ? 1 : 0);
		}
	}

	public static class WindowHandleAccess {

		private static MethodHandle getHandle;

		static {
			try {
				getHandle = MethodHandles.lookup().findStatic(Class.forName("org.lwjgl.opengl.Display"), "getHandle", MethodType.methodType(long.class));
			} catch (Throwable ignored) {
			}
		}

		private static long windowHandle = -1;

		public static long getWindowHandle() {
			if (windowHandle == -1) {
				try {
					windowHandle = (long) getHandle.invoke();
				} catch (Throwable ignored) {
				}
			}
			return windowHandle;
		}

		// Since the reflection used for this only works with legacy-lwjgl3 it's possible for us not being able to access the window handle.
		// This however should not lead to a crash despite compiling against glfw.
		public static boolean isHandleAvailable() {
			return getWindowHandle() != -1;
		}
	}
}
