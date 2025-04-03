package io.github.axolotlclient.bridge;

import io.github.axolotlclient.bridge.internal.BridgeUtil;
import java.util.UUID;

public interface AxoPlayerProfile {
	default String br$getName() {
		throw BridgeUtil.noImpl();
	}

	default UUID br$getId() {
		throw BridgeUtil.noImpl();
	}
}
