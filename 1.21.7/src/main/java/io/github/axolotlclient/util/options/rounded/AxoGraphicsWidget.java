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

package io.github.axolotlclient.util.options.rounded;

import java.util.Base64;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Graphics;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.screen.GraphicsEditorScreen;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.GraphicsWidget;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.rounded.widgets.RoundedButtonWidget;
import io.github.axolotlclient.util.notifications.Notifications;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AxoGraphicsWidget extends GraphicsWidget {
	private final GraphicsOption option;

	public AxoGraphicsWidget(int x, int y, int width, int height, GraphicsOption option) {
		super(x, y, width, height, option);
		this.option = option;
	}

	@Override
	public void onPress() {
		Minecraft.getInstance().setScreen(new AxoGraphicsEditorScreen(Minecraft.getInstance().screen, this.option));
	}

	public static class AxoGraphicsEditorScreen extends GraphicsEditorScreen {
		private final Graphics graphics;

		public AxoGraphicsEditorScreen(Screen parent, GraphicsOption option) {
			super(parent, option);
			this.graphics = option.get();
		}

		@Override
		public void init() {
			super.init();

			int buttonX = gridX + maxGridWidth + 10;
			int buttonY = gridY + 60;
			var back = (RoundedButtonWidget) children().getLast();
			removeWidget(back);
			addRenderableWidget(new RoundedButtonWidget(buttonX, buttonY + 25, Component.translatable("graphics.copy_text"),
				btn -> minecraft.keyboardHandler.setClipboard(Base64.getEncoder().encodeToString(graphics.getPixelData())))).setWidth(100);
			addRenderableWidget(new RoundedButtonWidget(buttonX, buttonY + 50, Component.translatable("graphics.paste_text"),
				btn -> {
					try {
						graphics.setPixelData(Base64.getDecoder().decode(minecraft.keyboardHandler.getClipboard()));
					} catch (IllegalArgumentException e) {
						Notifications.getInstance().addStatus("graphics.paste_text.failed", "graphics.paste_text.failed.desc");
					}
				})).setWidth(100);
			addRenderableWidget(back);
		}
	}
}
