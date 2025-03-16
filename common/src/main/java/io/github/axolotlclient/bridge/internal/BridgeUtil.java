package io.github.axolotlclient.bridge.internal;

import io.github.axolotlclient.AxolotlClientCommon;

public class BridgeUtil {
	public static AssertionError noImpl() {
		throw new AbstractMethodError("Bridge method not implemented for " + AxolotlClientCommon.GAME_VERSION);
	}

	public static <T> T noImplValue() {
		throw noImpl();
	}
}
