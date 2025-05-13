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

package io.github.axolotlclient.modules.hud.gui0.hud;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.DoubleOption;
import io.github.axolotlclient.bridge.PlatformDispatch;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.gui0.entry.BoxHudEntry;
import java.util.List;
import lombok.Getter;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

public class PlayerHud extends BoxHudEntry {

	public static final AxoIdentifier ID = AxoIdentifier.of("kronhud", "playerhud");
	@Getter
	private static boolean currentlyRendering;
	private final DoubleOption rotation = new DoubleOption("rotation", 0d, 0d, 360d);
	private final BooleanOption dynamicRotation = new BooleanOption("dynamicrotation", true);
	private final BooleanOption autoHide = new BooleanOption("autoHide", false);
	private float lastYawOffset = 0;
	private float yawOffset = 0;
	private float lastYOffset = 0;
	private float lastYaw = 0;
	private float yOffset = 0;
	private long hide;

	public PlayerHud() {
		super(62, 94, true);
	}

	@Override
	public boolean tickable() {
		return true;
	}

	@Override
	public void tick() {
		final var player = client.br$getPlayer();

		if(player == null){
			return;
		}

		yawOffset += player.br$getYaw() - lastYaw;
		lastYaw = player.br$getYaw();

		lastYawOffset = yawOffset;
		yawOffset *= .93f;
		lastYOffset = yOffset;

		/*if (client.player != null && client.player.isVisuallySwimming()) {
			float rawPitch = client.player.isInWater() ? -90.0F - client.player.getXRot() : -90.0F;
			float pitch = Mth.lerp(client.player.getSwimAmount(1), 0.0F, rawPitch);
			float height = client.player.getBbHeight();
			// sin = opposite / hypotenuse
			float offset = (float) (Math.sin(Math.toRadians(pitch)) * height);
			yOffset = Math.abs(offset) + 35;
		} else if (client.player != null && client.player.isFallFlying()) {
			// Elytra!

			float j = (float) client.player.getFallFlyingTicks() + 1;
			float k = Mth.clamp(j * j / 100.0F, 0.0F, 1.0F);

			float pitch = k * (-90.0F - client.player.getXRot()) + 90;
			float height = client.player.getBbHeight();
			// sin = opposite / hypotenuse
			float offset = (float) (Math.sin(Math.toRadians(pitch)) * height) * 50;
			yOffset = 35 - offset;
			if (pitch < 0) {
				yOffset -= (float) (((1 / (1 + Math.exp(-pitch / 4))) - .5) * 20);
			}
		} else {
			yOffset *= .8f;
		}*/
	}

	@Override
	public AxoIdentifier getId() {
		return ID;
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(dynamicRotation);
		options.add(rotation);
		options.add(autoHide);
		return options;
	}

	@Override
	public void renderComponent(AxoRenderContext  graphics, float delta) {
		PlatformDispatch.playerHud$renderPlayer(graphics, delta);
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext graphics, float delta) {
		PlatformDispatch.playerHud$renderPlayer(graphics, 0);
	}
}
