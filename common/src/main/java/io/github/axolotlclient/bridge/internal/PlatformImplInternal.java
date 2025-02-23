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
