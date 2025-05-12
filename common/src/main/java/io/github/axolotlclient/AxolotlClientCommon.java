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

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.VersionedJsonConfigManager;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.bridge.events.Events;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.Module;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.OSUtil;
import io.github.axolotlclient.util.notifications.NotificationProvider;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.fabricmc.loader.api.FabricLoader;

public abstract class AxolotlClientCommon {
	public static final AxoIdentifier BADGE_PATH = AxoIdentifier.of("axolotlclient", "textures/badge.png");
	public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("AxolotlClient.json");

	// static utility methods
	public static Path resolveConfigFile(String file) {
		return FabricLoader.getInstance().getConfigDir().resolve("axolotlclient").resolve(file);
	}

	public static final boolean NVG_SUPPORTED = OSUtil.getOS() != OSUtil.OperatingSystem.OTHER &&
		!Objects.requireNonNullElse(System.getenv("TMPDIR"), "").contains("/Android/data/net.kdt.pojavlaunch/");

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

	// init logic

	private void earlyModuleInit() {
		modules.forEach(Module::init);
	}

	private void lateModuleInit() {
		modules.forEach(Module::lateInit);
	}

	private void initConfig() {
		configManager = new VersionedJsonConfigManager(CONFIG_PATH,
			config.getConfig(), 2, (oldVersion, newVersion, config, json) -> {
			if (oldVersion.getMajor() == 1) {
				var keystrokes = json.get("hud").getAsJsonObject().get("keystrokehud")
					.getAsJsonObject();
				var mousemovement = new JsonObject();
				mousemovement.addProperty("enabled", keystrokes.get("enabled").getAsBoolean() && keystrokes.get(
                    "mousemovement").getAsBoolean());
				mousemovement.addProperty("mouseMovementIndicator",
                    keystrokes.get("mouseMovementIndicator").getAsString());
				mousemovement.addProperty("mouseMovementIndicatorOuter",
                    keystrokes.get("mouseMovementIndicatorOuter").getAsString());
				json.get("hud").getAsJsonObject().add("mousemovementhud", mousemovement);
			}
			return json;
		});

		AxolotlClientConfig.getInstance().register(configManager);

		configManager.load();
		configManager.suppressName("x");
		configManager.suppressName("y");
		configManager.suppressName(config.hidden.getName());
	}

	protected final void init(Logger logger, NotificationProvider provider) {
		Preconditions.checkState(!initializing);
		Preconditions.checkState(instance == null);
		initializing = true;
		instance = this;

		this.logger = logger;
		this.notificationProvider = provider;
		config = createConfig();

		earlyModuleInit();
		initConfig();
		lateModuleInit();

		// register events
		Events.TICK.register(() -> modules.forEach(Module::tick));
		Events.CLIENT_STOP.register(() -> API.getInstance().shutdown());
	}

	protected final void registerModule(Module module) {
		Preconditions.checkState(!initializing);
		modules.add(module);
	}

	protected abstract AxolotlClientConfigCommon createConfig();

	// random stuff

	public void saveConfig() {
		getConfigManager().save();
	}
}
