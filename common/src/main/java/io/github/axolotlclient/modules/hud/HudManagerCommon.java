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

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.gson.stream.JsonWriter;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.bridge.Platform;
import io.github.axolotlclient.bridge.events.Events;
import io.github.axolotlclient.bridge.key.AxoKeybinding;
import io.github.axolotlclient.bridge.key.AxoKeys;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.render.AxoWindow;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.bridge.util.AxoProfiler;
import io.github.axolotlclient.config.profiles.ProfileAware;
import io.github.axolotlclient.modules.AbstractCommonModule;
import io.github.axolotlclient.modules.hud.gui.component.HudEntry;
import io.github.axolotlclient.modules.hud.gui.component.Positionable;
import io.github.axolotlclient.modules.hud.gui.entry.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.gui.hud.*;
import io.github.axolotlclient.modules.hud.gui.hud.simple.*;
import io.github.axolotlclient.modules.hud.gui.hud.item.ArmorHud;
import io.github.axolotlclient.modules.hud.gui.hud.item.ArrowHud;
import io.github.axolotlclient.modules.hud.gui.hud.item.ItemUpdateHud;
import io.github.axolotlclient.modules.hud.gui.hud.vanilla.InventoryHud;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import io.github.axolotlclient.modules.hypixel.bedwars.BedwarsMod;
import io.github.axolotlclient.util.GsonHelper;
import io.github.axolotlclient.util.options.GenericOption;
import lombok.Getter;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */
public abstract class HudManagerCommon extends AbstractCommonModule implements ProfileAware {
	@Getter
	private static HudManagerCommon instance;

	private final static String CUSTOM_MODULE_SAVE_FILE_NAME = "custom_hud.json";
	private final AxoKeybinding key = AxoKeybinding.create(AxoKeys.KEY_RSHIFT, "key.openHud", "category.axolotlclient");
	private final AxoKeybinding toggleHud = AxoKeybinding.create(AxoKeys.KEY_UNKNOWN, "key.toggle_hud", "category.axolotlclient");
	private final OptionCategory hudCategory = OptionCategory.create("hud");
	private final BooleanOption enabled = new BooleanOption("enabled", true);
	private final Map<AxoIdentifier, HudEntry> entries;

	protected HudManagerCommon() {
		Preconditions.checkState(instance == null, "singleton already initialized");
		// bad antipattern, but whatever
		instance = this;
		this.entries = new LinkedHashMap<>();
	}

	public void init() {
		key.br$registerOnConsumeClick(this::openScreen);
		toggleHud.br$registerOnConsumeClick(enabled::toggle);
		Platform.getConfig().addCategory(hudCategory);
		hudCategory.add(enabled);
		add(new PingHud());
		add(new FPSHud());
		add(new CPSHud());
		add(new ArmorHud());
		add(new PotionsHud());
		add(new ToggleSprintHud());
		add(new IPHud());
		add(new IconHud());
		add(new SpeedHud());
		add(new CoordsHud());
		add(new ArrowHud());
		add(new ItemUpdateHud());
		add(new IRLTimeHud());
		add(new ReachHud());
		add(new MemoryHud());
		add(new PlayerCountHud());
		add(new CompassHud());
		add(new TPSHud());
		add(new ComboHud());
		add(new MouseMovementHud());
		add(new DayCounterHud());
		add(new InventoryHud());

		addExtraHud();

		addNonConfigured(BedwarsMod.getInstance().getUpgradesOverlay());
		addNonConfigured(BedwarsMod.getInstance().getResourceOverlay());
		addNonConfigured(BedwarsMod.getInstance().getStatsOverlay());

		entries.values().forEach(HudEntry::init);

		hudCategory.add(new GenericOption("hud.custom_entry", "hud.custom_entry.add", () -> {
			CustomHudEntry entry = new CustomHudEntry();
			entry.setEnabled(true);
			entry.init();
			entry.onBoundsUpdate();
			entries.put(entry.getId(), entry);
			hudCategory.add(entry.getAllOptions(), false);
			client.br$reinitScreen();
			saveCustomEntries();
		}));

		Events.CLIENT_START.register(this::loadCustomEntries);
		Events.CLIENT_STOP.register(this::saveCustomEntries);
	}

	@Override
	public void lateInit() {
		if (AxoWindow.getWindow() == null) {
			Events.CLIENT_READY.register(this::refreshAllBounds);
		} else {
			refreshAllBounds();
		}
	}

	@SuppressWarnings("unchecked")
	public void loadCustomEntries() {
		try {
			var path = AxolotlClientCommon.resolveProfileConfigFile(CUSTOM_MODULE_SAVE_FILE_NAME);
			if (Files.exists(path)) {
				var obj = (List<Object>) GsonHelper.read(Files.readString(path));
				obj.forEach(o -> {
					CustomHudEntry entry = new CustomHudEntry();
					var values = (Map<String, Object>) o;
					entry.getAllOptions().getOptions().forEach(opt -> {
						if (values.containsKey(opt.getName())) {
							opt.fromSerializedValue((String) values.get(opt.getName()));
						}
					});
					entries.put(entry.getId(), entry);
					hudCategory.add(entry.getAllOptions(), false);
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
			var path = AxolotlClientCommon.resolveProfileConfigFile(CUSTOM_MODULE_SAVE_FILE_NAME);
			Files.createDirectories(path.getParent());
			var writer = Files.newBufferedWriter(path);
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
	public final void tick() {
		AxoProfiler.get().br$push("hud_modules");
		entries.values().stream()
			.filter(hudEntry -> hudEntry.isEnabled() && hudEntry.tickable())
			.forEach(hudEntry -> {
				AxoProfiler.get().br$push(hudEntry.getNameKey());
				AxoProfiler.get().br$push("tick");
				hudEntry.tick();
				AxoProfiler.get().br$pop();
				AxoProfiler.get().br$pop();
			});
		AxoProfiler.get().br$pop();
	}

	public final HudManagerCommon add(AbstractHudEntry entry) {
		addNonConfigured(entry);
		hudCategory.add(entry.getAllOptions());
		return this;
	}

	public final HudManagerCommon addNonConfigured(AbstractHudEntry entry) {
		entries.put(entry.getId(), entry);
		return this;
	}

	public final void refreshAllBounds() {
		for (HudEntry entry : getEntries()) {
			entry.onBoundsUpdate();
		}
	}

	public final List<HudEntry> getEntries() {
		if (!entries.isEmpty()) {
			return new ArrayList<>(entries.values());
		}
		return new ArrayList<>();
	}

	public final HudEntry get(AxoIdentifier identifier) {
		return entries.get(identifier);
	}

	public final void removeEntry(AxoIdentifier identifier) {
		final var removed = entries.remove(identifier);
		if (removed != null) {
			hudCategory.getSubCategories().remove(removed.getCategory());
		}
	}

	public void render(AxoRenderContext context, float delta) {
		if (!enabled.get()) return;
		for (HudEntry hud : getEntries()) {
			if (hud.isEnabled()) {
				hud.render(context, delta);
			}
		}
	}

	public final Optional<HudEntry> getEntryXY(int x, int y) {
		for (HudEntry entry : getMoveableEntries()) {
			Rectangle bounds = entry.getTrueBounds();
			if (bounds.x() <= x && bounds.x() + bounds.width() >= x && bounds.y() <= y
				&& bounds.y() + bounds.height() >= y) {
				return Optional.of(entry);
			}
		}
		return Optional.empty();
	}

	public final List<HudEntry> getMoveableEntries() {
		if (!entries.isEmpty()) {
			return entries.values().stream().filter((entry) -> entry.isEnabled() && entry.movable())
				.collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	public final void renderPlaceholder(AxoRenderContext context, float delta) {
		for (HudEntry hud : getEntries()) {
			if (hud.isEnabled()) {
				hud.renderPlaceholder(context, delta);
			}
		}
	}

	public final List<Rectangle> getAllBounds() {
		return getMoveableEntries()
			.stream()
			.map(Positionable::getTrueBounds)
			.collect(Collectors.toList());
	}

	@Override
	public void reloadConfig() {
		entries.entrySet().removeIf(entry -> entry.getValue() instanceof CustomHudEntry);
		loadCustomEntries();
		for (var hud : getEntries()) {
			if (hud instanceof ProfileAware p) {
				p.reloadConfig();
			}
		}
	}

	@Override
	public void saveConfig() {
		saveCustomEntries();
		for (var hud : getEntries()) {
			if (hud instanceof ProfileAware p) {
				p.saveConfig();
			}
		}
	}

	protected abstract void openScreen();

	protected abstract void addExtraHud();
}
