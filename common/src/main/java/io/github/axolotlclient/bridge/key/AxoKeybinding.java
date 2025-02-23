package io.github.axolotlclient.bridge.key;

import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.internal.PlatformImplInternal;

/**
 * An abstract representation of a keybind
 */
public interface AxoKeybinding {
	static AxoKeybinding create(AxoKey defaultKey, String name, String category) {
		return PlatformImplInternal.createKeyBinding(defaultKey, name, category);
	}

	default void registerOnClicked(Runnable runnable) {
		throw BridgeUtil.noImpl();
	}

	default void registerOnReleased(Runnable runnable) {
		throw BridgeUtil.noImpl();
	}

	default AxoKey getBoundKey() {
		throw BridgeUtil.noImpl();
	}

	default boolean isPressed() {
		throw BridgeUtil.noImpl();
	}
}
