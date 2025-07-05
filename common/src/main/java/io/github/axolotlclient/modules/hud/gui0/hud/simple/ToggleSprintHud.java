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
import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringOption;
import io.github.axolotlclient.bridge.key.AxoKeybinding;
import io.github.axolotlclient.bridge.key.AxoKeys;
import io.github.axolotlclient.bridge.util.AxoI18n;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.gui0.entry.SimpleTextHudEntry;
import io.github.axolotlclient.util.options.ForceableBooleanOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

public class ToggleSprintHud extends SimpleTextHudEntry {

	public static final AxoIdentifier ID = AxoIdentifier.of("kronhud", "togglesprint");
	public final ForceableBooleanOption toggleSneak = new ForceableBooleanOption("toggleSneak", false);
	private final BooleanOption toggleSprint = new BooleanOption("toggleSprint", false);
	private final BooleanOption randomPlaceholder = new BooleanOption("randomPlaceholder", false);
	private final StringOption placeholder = new StringOption("placeholder", "No keys pressed");

	private final AxoKeybinding sprintToggle = AxoKeybinding.create(AxoKeys.KEY_K,"key.toggleSprint", "category.axolotlclient");
	private final AxoKeybinding sneakToggle = AxoKeybinding.create(AxoKeys.KEY_I, "key.toggleSneak", "category.axolotlclient");

	@Getter
	private final BooleanOption sprintToggled = new BooleanOption("sprintToggled", false);
	@Getter
	private final BooleanOption sneakToggled = new BooleanOption("sneakToggled", false);

	private final List<String> texts = new ArrayList<>();
	private String text = "";

	public ToggleSprintHud() {
		super(100);
	}

	@Override
	public void init() {
		sprintToggle.br$registerOnClicked(sprintToggled::toggle);
		sneakToggle.br$registerOnClicked(sneakToggled::toggle);
	}

	@Override
	public List<Option<?>> getSaveOptions() {
		List<Option<?>> options = super.getSaveOptions();
		options.add(sprintToggled);
		options.add(sneakToggled);
		return options;
	}

	@Override
	public AxoIdentifier getId() {
		return ID;
	}

	@Override
	public boolean tickable() {
		return true;
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(toggleSprint);
		options.add(toggleSneak);
		options.add(randomPlaceholder);
		options.add(placeholder);
		return options;
	}

	@Override
	public String getValue() {
		if (client.br$getKeybinds().br$getSneakKeybind().br$isPressed()) {
			return AxoI18n.translate("sneaking_pressed");
		}

		if (client.br$getKeybinds().br$getSprintKeybind().br$isPressed()) {
			return AxoI18n.translate("sprinting_pressed");
		}

		if (toggleSneak.get() && sneakToggled.get()) {
			return AxoI18n.translate("sneaking_toggled");
		}

		if (toggleSprint.get() && sprintToggled.get()) {
			return AxoI18n.translate("sprinting_toggled");
		}
		return getPlaceholder();
	}

	private String getRandomPlaceholder() {
		if (Objects.equals(text, "")) {
			loadRandomPlaceholder();
		}
		return text;
	}

	private void loadRandomPlaceholder() {
		try {
			/* TODO
			BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(
					Minecraft.getInstance().getResourceManager()
						.getResource(new Identifier("texts/splashes.txt")).asStream(),
					StandardCharsets.UTF_8));
			String string;
			while ((string = bufferedReader.readLine()) != null) {
				string = string.trim();
				if (!string.isEmpty()) {
					texts.add(string);
				}
			}

			text = texts.get(new Random().nextInt(texts.size()));*/
		} catch (Exception e) {
			text = "";
		}
	}

	@Override
	public String getPlaceholder() {
		return randomPlaceholder.get() ? getRandomPlaceholder() : placeholder.get();
	}
}
