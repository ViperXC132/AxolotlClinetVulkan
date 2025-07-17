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

import java.util.List;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.axolotlclient.bridge.PlatformDispatch;
import io.github.axolotlclient.bridge.events.Events;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.gui0.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui0.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.gui0.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.util.DrawPosition;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */
public class IPHud extends TextHudEntry implements DynamicallyPositionable {

	public static final AxoIdentifier ID = AxoIdentifier.of("kronhud", "iphud");
	private final BooleanOption showIcon = new BooleanOption("iphud.show_icon", false);
	private AxoSprite.Dynamic sprite;
	private final IntegerOption height = new IntegerOption("iphud.height", 13, 9, 64);
	private final EnumOption<AnchorPoint> anchor = new EnumOption<>("anchorpoint", AnchorPoint.class,
		AnchorPoint.TOP_LEFT);

	public IPHud() {
		super(115, 13, true);

		Events.DISCONNECT.register(() -> {
			if (sprite != null) {
				sprite.close();
				sprite = null;
			}
		});

		Events.CONNECT.register(() -> {
			if (showIcon.get()) {
				sprite = PlatformDispatch.ipHud$getServerIcon();
			}
		});
	}

	@Override
	public AxoIdentifier getId() {
		return ID;
	}

	public String getValue() {
		if (client.br$isLocalServer() || client.br$getServerAddress() == null) {
			return "Singleplayer";
		}

		return client.br$getServerAddress();
	}

	private void updateSize(AxoRenderContext graphics) {
		int w = getWidth();
		int h = getHeight();
		int hNew = height.get();
		boolean updated = false;
		if (h != hNew) {
			setHeight(hNew);
			updated = true;
		}
		int req = graphics.br$getFont().br$getWidth(getValue()) + 4;
		if (showIcon.get()) {
			req += getHeight() + 1;
		}
		if (w != req) {
			setWidth(req);
			updated = true;
		}
		if (updated) {
			onBoundsUpdate();
		}
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		var options = super.getConfigurationOptions();
		options.add(showIcon);
		options.add(height);
		options.add(anchor);
		return options;
	}

	@Override
	public void renderComponent(AxoRenderContext graphics, float delta) {
		updateSize(graphics);
		DrawPosition pos = getPos();
		int textX = pos.x() + getWidth() / 2;

		if (showIcon.get() && sprite != null) {
			int imageSize = getHeight() - 2 + 1;
			textX += imageSize / 2;
			graphics.br$drawTexture(pos.x() + 1, pos.y() + 1, imageSize, imageSize, sprite);
		}

		graphics.br$drawCenteredString(getValue(), textX, pos.y() + getHeight() / 2 - client.br$getFont().br$getFontHeight() / 2, textColor.get().toInt(), true);
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext graphics, float delta) {
		renderComponent(graphics, delta);
	}

	@Override
	public AnchorPoint getAnchor() {
		return anchor.get();
	}
}
