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

import io.github.axolotlclient.bridge.impl.AxoSpriteImpl;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.util.ClientColors;
import io.github.axolotlclient.util.MathUtil;
import io.github.axolotlclient.util.notifications.toasts.Toast;
import io.github.axolotlclient.util.notifications.toasts.ToastManager;
import net.minecraft.client.render.TextRenderer;
import org.jetbrains.annotations.NotNull;

public class ScreenshotToast implements Toast {
	private static final int TOAST_WIDTH = 100;
	private static final int DISPLAY_TIME_MILLIS = 4000;
	private final ImageInstance image;
	private final int height;
	private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;

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
	public void render(AxoRenderContext graphics, TextRenderer font, long visibilityTime) {
		graphics.br$fillRect(0, 0, width(), height + 2, -1);
		float prog = MathUtil.clamp(visibilityTime / 80f, 1f, 0f);
		var sprite = new AxoSpriteImpl.Simple(image.id(), 0, 0, TOAST_WIDTH, height);
		graphics.br$drawTexture(1, 1, TOAST_WIDTH, height, sprite);
		graphics.br$fillRect(1, 1, TOAST_WIDTH, height, ClientColors.ARGB.white(prog));
	}

	@Override
	public long axolotlclient$animationDuration() {
		return 300;
	}
}
