package io.github.axolotlclient.bridge.util;

import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.internal.PlatformImplInternal;

public interface AxoIdentifier {
	static AxoIdentifier of(String ns, String path) {
		return PlatformImplInternal.createIdentifier(ns, path);
	}

	static AxoIdentifier of(String path) {
		return of("minecraft", path);
	}

	default String getPath() {
		throw BridgeUtil.noImpl();
	}
}
