package io.github.axolotlclient.bridge.entity.effect;

import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.internal.PlatformImplInternal;

public interface AxoStatusEffectInstance {
	static AxoStatusEffectInstance create(AxoStatusEffect jumpBoost, int duration) {
		return PlatformImplInternal.createStatusEffectInstance(jumpBoost, duration);
	}

	default void setPermanent(boolean b) {
		throw BridgeUtil.noImpl();
	}
}
