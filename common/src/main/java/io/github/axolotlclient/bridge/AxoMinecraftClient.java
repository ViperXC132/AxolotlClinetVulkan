package io.github.axolotlclient.bridge;

import io.github.axolotlclient.bridge.entity.AxoPlayer;
import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.internal.PlatformImplInternal;
import io.github.axolotlclient.bridge.key.AxoClientKeybinds;
import io.github.axolotlclient.bridge.render.AxoTextRenderer;
import io.github.axolotlclient.bridge.world.AxoWorld;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public interface AxoMinecraftClient {
	static AxoMinecraftClient getInstance() {
		return PlatformImplInternal.getMinecraftClientInstance();
	}

	@Contract(pure = true)
	@Nullable
	default AxoPlayer getPlayer() {
		throw BridgeUtil.noImpl();
	}

	default AxoWorld getWorld() {
		throw BridgeUtil.noImpl();
	}

	default AxoTextRenderer getTextRenderer() {
		throw BridgeUtil.noImpl();
	}

	default AxoClientKeybinds getKeybinds() {
		throw BridgeUtil.noImpl();
	}
}
