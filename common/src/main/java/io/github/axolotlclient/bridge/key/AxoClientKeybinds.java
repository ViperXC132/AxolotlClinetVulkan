package io.github.axolotlclient.bridge.key;

import io.github.axolotlclient.bridge.internal.BridgeUtil;

public interface AxoClientKeybinds {
	default AxoKeybinding getSprintKeybind() {
		throw BridgeUtil.noImpl();
	}

	default AxoKeybinding getSneakKeybind() {
		throw BridgeUtil.noImpl();
	}

	default AxoKeybinding getAttackKey() {
		throw BridgeUtil.noImpl();
	}

	default AxoKeybinding getUseKey() {
		throw BridgeUtil.noImpl();
	}
}
