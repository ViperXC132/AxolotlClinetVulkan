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

package io.github.axolotlclient;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringOption;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;

public abstract class AxolotlClientConfigCommon {
	public enum MenuButtonMode {
		DISABLED,
		MODMENU () {
			@Override
			public boolean showButton() {
				return !(FabricLoader.getInstance().isModLoaded("modmenu") && !FabricLoader.getInstance().isModLoaded("axolotlclient-modmenu"));
			}
		},
		ALWAYS() {
			@Override
			public boolean showButton() {
				return true;
			}
		}
		;

		@Override
		public String toString() {
			return "menu_button_mode."+super.toString().toLowerCase(Locale.ROOT);
		}

		public boolean showButton() {
			return false;
		}
	}

	// options
	public final OptionCategory config = OptionCategory.create("config");
	public final OptionCategory hidden = OptionCategory.create("storedOptions");
	public final StringOption datetimeFormat = new StringOption("datetime_format", "yyyy/MM/dd HH:mm:ss", s -> dateTimeFormatter = DateTimeFormatter.ofPattern(s));
	public final EnumOption<MenuButtonMode> titleScreenOptionButtonMode = new EnumOption<>("title_screen_button_mode", MenuButtonMode.class, MenuButtonMode.MODMENU);
	public final EnumOption<MenuButtonMode> gameMenuScreenOptionButtonMode = new EnumOption<>("game_menu_screen_button_mode", MenuButtonMode.class, MenuButtonMode.MODMENU);

	private DateTimeFormatter dateTimeFormatter;

	public DateTimeFormatter getDateTimeFormatter() {
		if(dateTimeFormatter == null) {
			dateTimeFormatter = DateTimeFormatter.ofPattern(datetimeFormat.get());
		}

		return dateTimeFormatter;
	}

	public static AxolotlClientConfigCommon instance() {
		return AxolotlClientCommon.getInstance().getConfig();
	}

	public final void add(Option<?> option) {
		config.add(option);
	}

	public final void addCategory(OptionCategory cat) {
		config.add(cat);
	}

	public final OptionCategory getConfig() {
		return config;
	}
}
