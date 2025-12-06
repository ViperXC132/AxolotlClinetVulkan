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

package io.github.axolotlclient.modules.hud.gui.hud.vanilla;

import java.util.ArrayList;
import java.util.List;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.util.DefaultOptions;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Identifier;

import static io.github.axolotlclient.modules.hud.util.DrawUtil.drawCenteredString;

public class HotbarHud extends TextHudEntry implements DynamicallyPositionable {
	public static final Identifier ID = Identifier.of("axolotlclient", "hotbarhud");
	private final EnumOption<AnchorPoint> anchor = DefaultOptions.getAnchorPoint(this);

	public HotbarHud() {
		super(182, 22, false);
		supportsScaling = false;
	}

	@Override
	public void renderComponent(AxoRenderContext context, float delta) {
		// this is just a matrix translate in InGameHudMixin
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext context, float delta) {
		final var graphics = (GuiGraphics) context;

		DrawPosition pos = getPos();

		drawCenteredString(graphics, MinecraftClient.getInstance().textRenderer, getName(), pos.x() + width / 2,
			pos.y() + height / 2 - 4, -1, true);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public boolean overridesF3() {
		return true;
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> list = new ArrayList<>();
		list.add(enabled);
		list.add(hide);
		list.add(anchor);
		return list;
	}

	@Override
	public AnchorPoint getAnchor() {
		return anchor.get();
	}
}
