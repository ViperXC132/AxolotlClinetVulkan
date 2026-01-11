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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.NotNull;

public class ScreenshotToast implements Toast, ToastExtension {
	private static final int TOAST_WIDTH = 100;
	private static final int DISPLAY_TIME_MILLIS = 4000;
	private final ImageInstance image;
	private final int height;
	private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;

	@SuppressWarnings("resource")
	public ScreenshotToast(ImageInstance instance) {
		this.image = instance;
		this.height = ((int) (instance.image().getHeight() * (TOAST_WIDTH / (float) instance.image().getWidth())));
	}

	@Override
	public int width() {
		return 2 + TOAST_WIDTH;
	}

	@Override
	public int height() {
		return 2 + height;
	}

	@Override
	public Toast.@NotNull Visibility getWantedVisibility() {
		return this.wantedVisibility;
	}

	@Override
	public void update(@NotNull ToastManager toastManager, long visibilityTime) {
		var time = DISPLAY_TIME_MILLIS * toastManager.getNotificationDisplayTimeMultiplier();
		this.wantedVisibility = visibilityTime < time ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
	}

	@Override
	public void render(GuiGraphics guiGraphics, @NotNull Font font, long visibilityTime) {
		guiGraphics.fill(0, 0, width(), height+2, -1);
		float prog = MathUtil.clamp(visibilityTime/80f, 0f, 1f);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, image.id(), 1, 1, 0, 0, TOAST_WIDTH, height, TOAST_WIDTH, height, ClientColors.ARGB.white(prog));
	}

	@Override
	public OptionalLong axolotlclient$animationDuration() {
		return OptionalLong.of(300);
	}
}
