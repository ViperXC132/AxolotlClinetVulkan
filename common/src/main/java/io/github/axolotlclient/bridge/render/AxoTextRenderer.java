package io.github.axolotlclient.bridge.render;

import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.bridge.internal.BridgeUtil;

public interface AxoTextRenderer {
	default int getWidth(AxoText text) {
		throw BridgeUtil.noImpl();
	}

	default int getWidth(String text) {
		throw BridgeUtil.noImpl();
	}

	default int drawString(AxoRenderContext context, String value, int x, int y, int color, boolean shadow) {
		throw BridgeUtil.noImpl();
	}

	default void drawCenteredString(AxoRenderContext context, String value, int x, int y, int color, boolean shadow) {
		throw BridgeUtil.noImpl();
	}

    default int getFontHeight() {
		throw BridgeUtil.noImpl();
	}
}
