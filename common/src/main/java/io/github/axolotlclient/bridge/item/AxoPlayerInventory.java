package io.github.axolotlclient.bridge.item;

import io.github.axolotlclient.bridge.internal.BridgeUtil;
import java.util.List;

public interface AxoPlayerInventory {
	default AxoItemStack getMainHand() {
		throw BridgeUtil.noImpl();
	}

	default List<AxoItemStack> getItems() {
		throw BridgeUtil.noImpl();
	}

	default List<AxoItemStack> getArmor() {
		throw BridgeUtil.noImpl();
	}
}
