package io.github.axolotlclient.bridge.item;

import io.github.axolotlclient.bridge.internal.BridgeUtil;

public interface AxoItem {
	default boolean is(AxoItemClass itemClass) {
		throw BridgeUtil.noImpl();
	}
}
