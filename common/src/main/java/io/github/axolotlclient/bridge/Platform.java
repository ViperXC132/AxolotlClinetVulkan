package io.github.axolotlclient.bridge;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfigCommon;
import io.github.axolotlclient.bridge.internal.PlatformImplInternal;
import io.github.axolotlclient.bridge.util.AxoIdentifier;

public interface Platform {
	static long getMeasuringTimeMs() {
		return PlatformImplInternal.getMeasuringTimeMs();
	}

	static AxolotlClientConfigCommon getConfig() {
		return PlatformImplInternal.getConfig();
	}

	static AxoIdentifier getTexture(GraphicsOption option) {
		return PlatformImplInternal.getTexture(option);
	}
}
