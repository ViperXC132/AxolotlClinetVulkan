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

package io.github.axolotlclient.modules.hud.gui.hud;

import java.util.List;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.DoubleOption;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.gui.entry.BoxHudEntry;

public abstract class PlayerHudCommon extends BoxHudEntry {
	public static final AxoIdentifier ID = AxoIdentifier.of("kronhud", "playerhud");
	protected final DoubleOption rotation = new DoubleOption("rotation", 0d, 0d, 360d);
	protected final BooleanOption dynamicRotation = new BooleanOption("dynamicrotation", true);
	protected final BooleanOption autoHide = new BooleanOption("autoHide", false);
	protected float lastYawOffset = 0;
	protected float yawOffset = 0;
	protected float lastYOffset = 0;
	protected float yOffset = 0;
	protected long hide;

	public PlayerHudCommon() {
		super(62, 94, true);
	}

	@Override
	public boolean tickable() {
		return true;
	}

	@Override
	public void tick() {
		lastYawOffset = yawOffset;
		yawOffset *= .93f;
		lastYOffset = yOffset;
	}

	@Override
	public void renderComponent(AxoRenderContext ctx, float delta) {
		renderPlayer(ctx, false, getTrueContentX(), getTrueContentY(), delta);
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext ctx, float delta) {
		renderPlayer(ctx, true, getTrueContentX(), getTrueContentY(), 0);
	}

	protected abstract void renderPlayer(AxoRenderContext ctx, boolean placeholder, double x, double y, float delta);

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
}
