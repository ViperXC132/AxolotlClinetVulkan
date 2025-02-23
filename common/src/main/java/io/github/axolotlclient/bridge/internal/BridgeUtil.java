package io.github.axolotlclient.bridge.internal;

import net.fabricmc.loader.api.FabricLoader;

public class BridgeUtil {
	/**
	 * Don't invoke any methods relating to {@link PlatformImplInternal}, so we need to get the version via {@link FabricLoader}
	 */
	private static final String MC_VERSION = FabricLoader.getInstance()
		.getModContainer("minecraft")
		.orElseThrow()
		.getMetadata()
		.getVersion()
		.getFriendlyString();

	public static AssertionError noImpl() {
		throw new AbstractMethodError("Bridge method not implemented for " + MC_VERSION);
	}

	public static <T> T noImplValue() {
		throw noImpl();
	}
}
