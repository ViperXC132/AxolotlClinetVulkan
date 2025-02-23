package io.github.axolotlclient.bridge.render;

import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.internal.PlatformImplInternal;
import org.jetbrains.annotations.Nullable;

public interface AxoWindow {
	static @Nullable AxoWindow getWindow() {
		return PlatformImplInternal.getWindow();
	}

	default double getScaledWidth() {
		throw BridgeUtil.noImpl();
	}

	default double getScaledHeight() {
		throw BridgeUtil.noImpl();
	}
}
