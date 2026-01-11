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

package io.github.axolotlclient.modules.screenshotUtils;

import java.util.OptionalLong;

import io.github.axolotlclient.util.ClientColors;
import io.github.axolotlclient.util.MathUtil;
import io.github.axolotlclient.util.duck.ToastExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import org.jetbrains.annotations.NotNull;

public class ScreenshotToast implements Toast, ToastExtension {
	private static final int TOAST_WIDTH = 100;
	private static final int DISPLAY_TIME_MILLIS = 4000;
	private final ImageInstance image;
	private final int height;

	@SuppressWarnings("resource")
	public ScreenshotToast(ImageInstance instance) {
		this.image = instance;
		this.height = ((int) (instance.image().getHeight() * (TOAST_WIDTH / (float) instance.image().getWidth())));
	}

	@Override
	public int getWidth() {
		return 2 + TOAST_WIDTH;
	}

	@Override
	public int getHeight() {
		return 2 + height;
	}

	@Override
	public Visibility draw(GuiGraphics guiGraphics, @NotNull ToastManager toastManager, long visibilityTime) {
		guiGraphics.fill(0, 0, getWidth(), height + 2, -1);
		float prog = MathUtil.clamp(visibilityTime / 80f, 1f, 0f);
		guiGraphics.drawTexture(image.id(), 1, 1, 0, 0, TOAST_WIDTH, height, TOAST_WIDTH, height);
		guiGraphics.br$fillRect(1, 1, TOAST_WIDTH, height, ClientColors.ARGB.white(prog));
		var time = DISPLAY_TIME_MILLIS * toastManager.method_48221();
		return visibilityTime < time ? Visibility.SHOW : Visibility.HIDE;
	}

	@Override
	public OptionalLong axolotlclient$animationDuration() {
		return OptionalLong.of(300);
	}
}
