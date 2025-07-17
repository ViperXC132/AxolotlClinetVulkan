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

package io.github.axolotlclient.modules.hud.gui0.hud;

import java.util.List;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.bridge.Platform;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.gui0.entry.BoxHudEntry;
import io.github.axolotlclient.util.MathUtil;

public class MouseMovementHud extends BoxHudEntry {

	public static final AxoIdentifier ID = AxoIdentifier.of("kronhud", "mousemovementhud");

	private final GraphicsOption mouseMovementIndicatorInner = new GraphicsOption("mouseMovementIndicator", new int[][]{
		new int[]{0, 0, 0, 0, 0, 0, 0},
		new int[]{0, 0, 0, 0, 0, 0, 0},
		new int[]{0, 0, 0, 0, 0, 0, 0},
		new int[]{0, 0, 0, -1, 0, 0, 0},
		new int[]{0, 0, 0, 0, 0, 0, 0},
		new int[]{0, 0, 0, 0, 0, 0, 0},
		new int[]{0, 0, 0, 0, 0, 0, 0}
	});
	private final GraphicsOption mouseMovementIndicatorOuter = new GraphicsOption("mouseMovementIndicatorOuter", new int[][]{
		new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		new int[]{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
		new int[]{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
		new int[]{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
		new int[]{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
		new int[]{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
		new int[]{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
		new int[]{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
		new int[]{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
		new int[]{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
		new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
	});

	private float mouseX = 0;
	private float mouseY = 0;
	private float lastMouseX = 0;
	private float lastMouseY = 0;

	private boolean hasPreviousPitchYaw = false;
	private float prevPitch = 0;
	private float prevYaw = 0;

	public MouseMovementHud() {
		super(53, 35, true);
	}

	// Implementation credit goes to TheKodeToad
	// This project has the author's approval to use this
	// https://github.com/Sol-Client/Client/blob/main/game/src/main/java/io/github/solclient/client/mod/impl/hud/keystrokes/KeystrokesMod.java
	// Port to Bridge: removed event and poll pitch/yaw in renderComponent
	@Override
	public void renderComponent(AxoRenderContext context, float delta) {
		final var player = client.br$getPlayer();

		if(player != null) {
			if (hasPreviousPitchYaw) {
				mouseX = (player.br$getYaw() - prevYaw);
				mouseY = (player.br$getPitch() - prevPitch);
				prevPitch = player.br$getPitch();
				prevYaw = player.br$getYaw();

				float halfWidth = getWidth() / 2f;
				mouseX = MathUtil.clamp(mouseX, -halfWidth + 4, halfWidth - 4);
				mouseY = MathUtil.clamp(mouseY, -13, 13);
			} else {
				hasPreviousPitchYaw = true;
				prevPitch = player.br$getPitch();
				prevYaw = player.br$getYaw();
			}
		}

		context.br$glColor4(1, 1, 1, 1);
		context.br$glEnableBlend();
		int spaceY = getRawY();
		int spaceX = getRawX();

		float calculatedMouseX = (lastMouseX + ((mouseX - lastMouseX) * delta)) - 5;
		float calculatedMouseY = (lastMouseY + ((mouseY - lastMouseY) * delta)) - 5;

		context.br$drawTexture(spaceX + (width / 2) - 7 / 2 - 1, spaceY + 17 - (7 / 2), 7, 7, Platform.createTexture(mouseMovementIndicatorInner));
		// Woah KodeToad, good use of translate
		context.br$translateMatrix(calculatedMouseX, calculatedMouseY, 0);
		context.br$drawTexture(spaceX + (width / 2) - 1, spaceY + 17, 11, 11, Platform.createTexture(mouseMovementIndicatorOuter));
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext context, float delta) {
		renderComponent(context, delta);
	}

	@Override
	public void tick() {
		lastMouseX = mouseX;
		lastMouseY = mouseY;
		mouseX *= .75f;
		mouseY *= .75f;
	}

	@Override
	public boolean tickable() {
		return true;
	}

	@Override
	public AxoIdentifier getId() {
		return ID;
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(mouseMovementIndicatorInner);
		options.add(mouseMovementIndicatorOuter);
		return options;
	}
}
