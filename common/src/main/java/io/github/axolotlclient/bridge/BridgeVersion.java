package io.github.axolotlclient.bridge;

import io.github.axolotlclient.bridge.internal.PlatformImplInternal;
import lombok.Getter;

@Getter
public enum BridgeVersion {
	V1_8("1.8.9"),
	V1_16_COMBAT("1.16_combat-6"),
	V1_20("1.20"),
	V1_21("1.21"),
	V1_21_4("1.21.4");

	private final String name;

	BridgeVersion(String name) {
		this.name = name;
	}

	public static BridgeVersion version() {
		return PlatformImplInternal.getBridgeApiVersion();
	}

	public boolean isCurrent() {
		return this == version();
	}
}
