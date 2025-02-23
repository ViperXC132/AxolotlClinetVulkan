package io.github.axolotlclient.bridge.entity;

import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.item.AxoPlayerInventory;

public interface AxoPlayer extends AxoEntity {
	default AxoPlayerInventory getInventory() {
		throw BridgeUtil.noImpl();
	}
}
