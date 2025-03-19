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

package io.github.axolotlclient.bridge.internal;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.bridge.AxoMinecraftClient;
import io.github.axolotlclient.bridge.BridgeVersion;
import io.github.axolotlclient.AxolotlClientConfigCommon;
import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffect;
import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffectInstance;
import io.github.axolotlclient.bridge.item.AxoItem;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.key.AxoKey;
import io.github.axolotlclient.bridge.key.AxoKeybinding;
import io.github.axolotlclient.bridge.render.AxoWindow;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Internal implementations for static platform methods.
 */
@ApiStatus.Internal
public class PlatformImplInternal {
	public static @Nullable AxoWindow getWindow() {
		throw BridgeUtil.noImpl();
	}

	public static AxoMinecraftClient getMinecraftClientInstance() {
		throw BridgeUtil.noImpl();
	}

	public static String getTranslatedString(String nameKey, Object[] args) {
		throw BridgeUtil.noImpl();
	}

	public static BridgeVersion getBridgeApiVersion() {
		throw BridgeUtil.noImpl();
	}

	public static long getMeasuringTimeMs() {
		throw BridgeUtil.noImpl();
	}

	public static AxolotlClientConfigCommon getConfig() {
		throw BridgeUtil.noImpl();
	}

	public static int getCurrentFps() {
		throw BridgeUtil.noImpl();
	}

	public static AxoIdentifier getTexture(GraphicsOption option) {
		throw BridgeUtil.noImpl();
	}

	// constructors
	public static AxoItemStack createItemStack(AxoItem item, int count) {
		throw BridgeUtil.noImpl();
	}

	public static AxoIdentifier createIdentifier(String ns, String path) {
		throw BridgeUtil.noImpl();
	}

	public static AxoKeybinding createKeyBinding(AxoKey defaultKey, String name, String category) {
		throw BridgeUtil.noImpl();
	}

	public static AxoStatusEffectInstance createStatusEffectInstance(AxoStatusEffect jumpBoost, int duration) {
		throw BridgeUtil.noImpl();
	}
}
