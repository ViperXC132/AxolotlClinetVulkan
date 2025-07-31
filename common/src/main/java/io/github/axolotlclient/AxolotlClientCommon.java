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


package io.github.axolotlclient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.ui.ConfigUI;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.VersionedJsonConfigManager;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.bridge.events.Events;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.Module;
import io.github.axolotlclient.modules.hud.ClickInputTracker;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.OSUtil;
import io.github.axolotlclient.util.notifications.NotificationProvider;
import net.fabricmc.loader.api.FabricLoader;

public abstract class AxolotlClientCommon {
	public static final String MODID = "axolotlclient";
	public static final AxoIdentifier BADGE_PATH = AxoIdentifier.of(MODID, "textures/badge.png");

	// static utility methods
	public static Path resolveConfigFile(String file) {
		return FabricLoader.getInstance().getConfigDir().resolve(MODID).resolve(file);
	}

	public static final boolean NVG_SUPPORTED = OSUtil.getOS() != OSUtil.OperatingSystem.OTHER &&
		!Objects.requireNonNullElse(System.getenv("TMPDIR"), "").contains("Android");

	public static final String VERSION = FabricLoader.getInstance()
		.getModContainer("axolotlclient-common")
		.orElseThrow()
		.getMetadata()
		.getVersion()
		.getFriendlyString();

	public static final String GAME_VERSION = FabricLoader.getInstance()
		.getModContainer("minecraft")
		.orElseThrow()
		.getMetadata()
		.getVersion()
		.getFriendlyString();

	private static AxolotlClientCommon instance;

	private AxolotlClientConfigCommon config;
	private Logger logger;
	private NotificationProvider notificationProvider;
	private ConfigManager configManager;
	private boolean initializing = false;
	public final List<Module> modules = new ArrayList<>();

	protected AxolotlClientCommon() {
	}

	// getters

	public AxolotlClientConfigCommon getConfig() {
		Preconditions.checkState(initializing && config != null);
		return config;
	}

	public ConfigManager getConfigManager() {
		Preconditions.checkState(initializing && configManager != null);
		return configManager;
	}

	public Logger getLogger() {
		Preconditions.checkState(initializing && logger != null);
		return logger;
	}

	public NotificationProvider getNotificationProvider() {
		Preconditions.checkState(initializing && notificationProvider != null);
		return notificationProvider;
	}

	public static AxolotlClientCommon getInstance() {
		Preconditions.checkState(instance != null);
		return instance;
	}

	private void addBuiltinCommonModules() {
		registerModule(ClickInputTracker.getInstance());
	}

	// init logic

	private void earlyModuleInit() {
		modules.forEach(Module::init);
	}

	private void lateModuleInit() {
		modules.forEach(Module::lateInit);
	}

	private void initConfig() {
		AxolotlClientConfig.getInstance()
			.register(configManager = new VersionedJsonConfigManager(getMainConfigFile(),
				config.getConfig(), 5, (oldVersion, newVersion, config, json) -> {
				if (oldVersion.getMajor() <= 1) {
					if (json.has("hud")) {
						var hud = json.get("hud").getAsJsonObject();
						if (hud.has("keystrokehud")) {
							var keystrokes = hud.get("keystrokehud")
								.getAsJsonObject();
							var mousemovement = new JsonObject();
							mousemovement.addProperty("enabled", keystrokes.get("enabled").getAsBoolean() && keystrokes.get("mousemovement").getAsBoolean());
							mousemovement.addProperty("mouseMovementIndicator", keystrokes.get("mouseMovementIndicator").getAsString());
							mousemovement.addProperty("mouseMovementIndicatorOuter", keystrokes.get("mouseMovementIndicatorOuter").getAsString());
							hud.add("mousemovementhud", mousemovement);
						}
					}
				}
				if (oldVersion.getMajor() <= 2) {
					if (json.has("hud")) {
						var hud = json.get("hud").getAsJsonObject();
						if (hud.has("armorhud")) {
							var armorhud = hud.get("armorhud").getAsJsonObject();
							if (armorhud.has("armorhud.main_hand_item_top")) {
								var mainItemTop = armorhud.get("armorhud.main_hand_item_top").getAsBoolean();
								if (mainItemTop) {
									armorhud.addProperty("armorhud.main_hand_item_position", "armorhud.main_hand_item_position.top");
								}
							}
						}
					}
				}
				if (oldVersion.getMajor() <= 3) {
					if (json.has("storedOptions")) {
						var hiddenOptions = json.get("storedOptions").getAsJsonObject();

						JsonObject apiOptions;
						if (json.has("api.category")) {
							apiOptions = json.get("api.category").getAsJsonObject();
						} else {
							apiOptions = new JsonObject();
							json.add("api.category", apiOptions);
						}

						apiOptions.addProperty("api.privacy_policy_accepted", "privacy_policy_state." + hiddenOptions.get("privacyPolicyAccepted").getAsString().toLowerCase(Locale.ROOT));
					}
				}
				if (oldVersion.getMajor() <= 4) {
					if (json.has("hypixel-mods")) {
						var hypixel = json.get("hypixel-mods").getAsJsonObject();
						var autoboop = hypixel.get("autoboop");
						if (autoboop != null) {
							var filterlist = autoboop.getAsJsonObject().get("autoboop.filterlist");
							if (filterlist != null) {
								filterlist.getAsString();
								autoboop.getAsJsonObject().addProperty("autoboop.filterlist", Arrays.stream(filterlist.getAsString().split(","))
									.map(s -> s.getBytes(StandardCharsets.UTF_8))
									.map(s -> Base64.getEncoder().encodeToString(s)).collect(Collectors.joining(",")));
							}
						}
					}
				}
				return json;
			}));

		AxolotlClientConfig.getInstance().register(configManager);

		configManager.suppressName("x");
		configManager.suppressName("y");
		configManager.suppressName(config.hidden.getName());
	}

	protected final void init(Logger logger, NotificationProvider provider) {
		Preconditions.checkState(!initializing);
		Preconditions.checkState(instance == null);

		addBuiltinCommonModules();

		initializing = true;
		instance = this;

		this.logger = logger;
		this.notificationProvider = provider;
		config = createConfig();

		earlyModuleInit();
		initConfig();

		ConfigUI.getInstance().runWhenLoaded(() -> {
			lateModuleInit();
			Events.TICK.register(() -> modules.forEach(Module::tick));
			initFeatureDisabler();
		});

		// register events

		Events.CLIENT_STOP.register(() -> API.getInstance().shutdown());
	}

	protected final void registerModule(Module module) {
		Preconditions.checkState(!initializing);
		modules.add(module);
	}

	protected abstract void initFeatureDisabler();

	protected abstract AxolotlClientConfigCommon createConfig();

	// random stuff

	public void saveConfig() {
		getConfigManager().save();
	}

	public Path getMainConfigFile() {
		var legacy = FabricLoader.getInstance().getConfigDir().resolve("AxolotlClient.json");
		var current = resolveConfigFile("axolotlclient.json");
		try {
			if (Files.exists(legacy)) {
				Files.createDirectories(current.getParent());
				Files.move(legacy, current);
			}
		} catch (IOException e) {
			logger.warn("Failed to move config file, it might get reset! ", e);
		}
		return current;
	}
}
