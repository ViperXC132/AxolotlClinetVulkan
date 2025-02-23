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

package io.github.axolotlclient.modules.hud.gui0.hud.simple;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.bridge.Platform;
import io.github.axolotlclient.bridge.events.Events;
import io.github.axolotlclient.bridge.key.AxoKey;
import io.github.axolotlclient.bridge.key.AxoKeys;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.gui0.entry.SimpleTextHudEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

public class CPSHud extends SimpleTextHudEntry {

	public static final AxoIdentifier ID = AxoIdentifier.of("kronhud", "cpshud");

	private final BooleanOption fromKeybindings = new BooleanOption("cpskeybind", false);
	private final BooleanOption rmb = new BooleanOption("rightcps", false);

	private final ClickList left = new ClickList();
	private final ClickList right = new ClickList();

	public CPSHud() {
		super();

		Events.KEY_INPUT.defaultPhase().register(key -> {
			if (fromKeybindings.get()) {
				if (key.equals(client.getKeybinds().getAttackKey().getBoundKey())) {
					left.click();
				} else if (key.equals(client.getKeybinds().getUseKey().getBoundKey())) {
					right.click();
				}
			} else {
				if (key.equals(AxoKeys.MOUSE_LEFT)) {
					left.click();
				} else if (key.equals(AxoKeys.MOUSE_RIGHT)) {
					right.click();
				}
			}
		});
	}

	@Override
	public boolean tickable() {
		return true;
	}

	@Override
	public void tick() {
		left.update();
		right.update();
	}

	@Override
	public AxoIdentifier getId() {
		return ID;
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(fromKeybindings);
		options.add(rmb);
		return options;
	}

	@Override
	public String getValue() {
		if (rmb.get()) {
			return left.clicks() + " | " + right.clicks() + " CPS";
		} else {
			return left.clicks() + " CPS";
		}
	}

	@Override
	public String getPlaceholder() {
		if (rmb.get()) {
			return "0 | 0 CPS";
		} else {
			return "0 CPS";
		}
	}

	public static class ClickList {
		private final List<Long> clicks;

		public ClickList() {
			clicks = new ArrayList<>();
		}

		public void update() {
			clicks.removeIf((click) -> Platform.getMeasuringTimeMs() - click > 1000);
		}

		public void click() {
			clicks.add(Platform.getMeasuringTimeMs());
		}

		public int clicks() {
			return clicks.size();
		}
	}
}
