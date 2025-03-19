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

package io.github.axolotlclient.modules.hud;

import com.google.gson.stream.JsonWriter;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.bridge.Platform;
import io.github.axolotlclient.bridge.events.Events;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.AbstractModule0;
import io.github.axolotlclient.modules.hud.gui0.component.HudEntry;
import io.github.axolotlclient.modules.hud.gui0.component.Positionable;
import io.github.axolotlclient.modules.hud.gui0.entry.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.gui0.hud.CompassHud;
import io.github.axolotlclient.modules.hud.gui0.hud.CoordsHud;
import io.github.axolotlclient.modules.hud.gui0.hud.IconHud;
import io.github.axolotlclient.modules.hud.gui0.hud.MemoryHud;
import io.github.axolotlclient.modules.hud.gui0.hud.MouseMovementHud;
import io.github.axolotlclient.modules.hud.gui0.hud.item.ArmorHud;
import io.github.axolotlclient.modules.hud.gui0.hud.item.ArrowHud;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.CPSHud;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.ComboHud;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.CustomHudEntry;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.DayCounterHud;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.FPSHud;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.IRLTimeHud;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.PingHud;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.PlayerCountHud;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.ReachHud;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.SpeedHud;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.TPSHud;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.ToggleSprintHud;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import io.github.axolotlclient.util.GsonHelper;
import io.github.axolotlclient.util.options.GenericOption;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

public class HudManager0 extends AbstractModule0 {
	private final static Path CUSTOM_MODULE_SAVE_PATH = AxolotlClientCommon.resolveConfigFile("custom_hud.json");
	private final static HudManager0 INSTANCE = new HudManager0();
	//static KeyBinding key = new KeyBinding("key.openHud", Keyboard.KEY_RSHIFT, "category.axolotlclient");
	private final OptionCategory hudCategory = OptionCategory.create("hud");
	private final Map<AxoIdentifier, HudEntry> entries;

	private HudManager0() {
		this.entries = new LinkedHashMap<>();
	}

	public static HudManager0 getInstance() {
		return INSTANCE;
	}

	public void init() {
		//	KeyBindingEvents.REGISTER_KEYBINDS.register(r -> r.register(key));

		Platform.getConfig().addCategory(hudCategory);

		add(new ComboHud());
		add(new CPSHud());
		add(new DayCounterHud());
		add(new FPSHud());
		add(new IRLTimeHud());
		add(new PingHud());
		add(new PlayerCountHud());
		add(new ReachHud());
		add(new SpeedHud());
		add(new ToggleSprintHud());
		add(new TPSHud());
		add(new CompassHud());
		add(new IconHud());
		add(new MemoryHud());
		add(new CoordsHud());
		add(new MouseMovementHud());
		add(new ArmorHud());
		add(new ArrowHud());

		entries.values().forEach(HudEntry::init);
		refreshAllBounds();

		hudCategory.add(new GenericOption("hud.custom_entry", "hud.custom_entry.add", () -> {
			CustomHudEntry entry = new CustomHudEntry();
			entry.setEnabled(true);
			entry.init();
			entry.onBoundsUpdate();
			entry.getAllOptions().includeInParentTree(false);
			add(entry);
			// TODO: ??
			// client.screen.resize(client, client.screen.width, client.screen.height);
			saveCustomEntries();
		}));

		Events.CLIENT_START.register(this::loadCustomEntries);
		Events.CLIENT_STOP.register(this::saveCustomEntries);
	}

	@SuppressWarnings("unchecked")
	private void loadCustomEntries() {
		try {
			if (Files.exists(CUSTOM_MODULE_SAVE_PATH)) {
				var obj = (List<Object>) GsonHelper.read(Files.readString(CUSTOM_MODULE_SAVE_PATH));
				obj.forEach(o -> {
					CustomHudEntry entry = new CustomHudEntry();
					var values = (Map<String, Object>) o;
					entry.getAllOptions().getOptions().forEach(opt -> {
						if (values.containsKey(opt.getName())) {
							opt.fromSerializedValue((String) values.get(opt.getName()));
						}
					});
					entry.getCategory().includeInParentTree(false);
					add(entry);
					entry.init();
					entry.onBoundsUpdate();
				});
			}
		} catch (IOException e) {
			AxolotlClientCommon.getInstance().getLogger().warn("Failed to load custom hud modules!", e);
		}
	}

	public void saveCustomEntries() {
		try {
			Files.createDirectories(CUSTOM_MODULE_SAVE_PATH.getParent());
			var writer = Files.newBufferedWriter(CUSTOM_MODULE_SAVE_PATH);
			var json = new JsonWriter(writer);
			json.beginArray();
			for (Map.Entry<AxoIdentifier, HudEntry> entry : entries.entrySet()) {
				HudEntry hudEntry = entry.getValue();
				if (hudEntry instanceof CustomHudEntry hud) {
					json.beginObject();
					for (Option<?> opt : hud.getCategory().getOptions()) {
						var value = opt.toSerializedValue();
						if (value != null) {
							json.name(opt.getName());
							json.value(value);
						}
					}
					json.endObject();
				}
			}
			json.endArray();
			json.close();
		} catch (IOException e) {
			AxolotlClientCommon.getInstance().getLogger().warn("Failed to save custom hud modules!", e);
		}
	}

	@Override
	public void tick() {
		// if (key.isPressed())
		//	Minecraft.getInstance().openScreen(new HudEditScreen());
		entries.values().stream().filter(hudEntry -> hudEntry.isEnabled() && hudEntry.tickable())
			.forEach(HudEntry::tick);
	}

	public HudManager0 add(AbstractHudEntry entry) {
		entries.put(entry.getId(), entry);
		hudCategory.add(entry.getAllOptions());
		return this;
	}

	public void refreshAllBounds() {
		for (HudEntry entry : getEntries()) {
			entry.onBoundsUpdate();
		}
	}

	public List<HudEntry> getEntries() {
		if (!entries.isEmpty()) {
			return new ArrayList<>(entries.values());
		}
		return new ArrayList<>();
	}

	public HudEntry get(AxoIdentifier identifier) {
		return entries.get(identifier);
	}

	public void removeEntry(AxoIdentifier identifier) {
		hudCategory.getSubCategories().remove(entries.remove(identifier).getCategory());
	}

	public void render(AxoRenderContext context, float delta) {
		for (HudEntry hud : getEntries()) {
			if (hud.isEnabled()) {
				hud.render(context, delta);
			}
		}
	}

	public Optional<HudEntry> getEntryXY(int x, int y) {
		for (HudEntry entry : getMoveableEntries()) {
			Rectangle bounds = entry.getTrueBounds();
			if (bounds.x() <= x && bounds.x() + bounds.width() >= x && bounds.y() <= y
				&& bounds.y() + bounds.height() >= y) {
				return Optional.of(entry);
			}
		}
		return Optional.empty();
	}

	public List<HudEntry> getMoveableEntries() {
		if (!entries.isEmpty()) {
			return entries.values().stream().filter((entry) -> entry.isEnabled() && entry.movable())
				.collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	public void renderPlaceholder(AxoRenderContext context, float delta) {
		for (HudEntry hud : getEntries()) {
			if (hud.isEnabled()) {
				hud.renderPlaceholder(context, delta);
			}
		}
	}

	public List<Rectangle> getAllBounds() {
		return getMoveableEntries()
			.stream()
			.map(Positionable::getTrueBounds)
			.collect(Collectors.toList());
	}
}
