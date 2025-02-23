package io.github.axolotlclient.bridge.render;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import org.jetbrains.annotations.ApiStatus;

public interface AxoRenderContext {
	// Matrix management
	default void popMatrix() {
		throw BridgeUtil.noImpl();
	}

	default void pushMatrix() {
		throw BridgeUtil.noImpl();
	}

	default void scaleMatrix(float sx, float sy, float sz) {
		throw BridgeUtil.noImpl();
	}

	default void translateMatrix(float x, float y, float z) {
		throw BridgeUtil.noImpl();
	}

	// GL state management
	default void glEnableTexture() {
		throw BridgeUtil.noImpl();
	}

	default void glEnableBlend() {
		throw BridgeUtil.noImpl();
	}

	default void glDisableTexture() {
		throw BridgeUtil.noImpl();
	}

	default void glDisableBlend() {
		throw BridgeUtil.noImpl();
	}


	default void glColor4(float r, float g, float b, float a) {
		throw BridgeUtil.noImpl();
	}

	// string rendering
	@ApiStatus.NonExtendable
	default int drawString(String value, int x, int y, int color, boolean shadow) {
		return getTextRenderer().drawString(this, value, x, y, color, shadow);
	}

	@ApiStatus.NonExtendable
	default int drawString(String value, int x, int y, Color color, boolean shadow) {
		return getTextRenderer().drawString(this, value, x, y, color.toInt(), shadow);
	}

	@ApiStatus.NonExtendable
	default void drawCenteredString(String value, int x, int y, int color, boolean shadow) {
		getTextRenderer().drawCenteredString(this, value, x, y, color, shadow);
	}

	@ApiStatus.NonExtendable
	default void drawCenteredString(String value, int x, int y, Color color, boolean shadow) {
		getTextRenderer().drawCenteredString(this, value, x, y, color.toInt(), shadow);
	}

	// fillRect overloads
	@ApiStatus.NonExtendable
	default void fillRect(Rectangle rect, Color color) {
		fillRect(rect.x, rect.y, rect.width, rect.height, color.toInt());
	}

	@ApiStatus.NonExtendable
	default void fillRect(Rectangle rect, int color) {
		fillRect(rect.x, rect.y, rect.width, rect.height, color);
	}

	@ApiStatus.NonExtendable
	default void fillRect(int x, int y, int width, int height, Color color) {
		fillRect(x, y, width, height, color.toInt());
	}

	default void fillRect(int x, int y, int width, int height, int color) {
		throw BridgeUtil.noImpl();
	}

	// outlineRect overloads
	@ApiStatus.NonExtendable
	default void outlineRect(Rectangle rect, Color color) {
		outlineRect(rect.x, rect.y, rect.width, rect.height, color.toInt());
	}

	default void outlineRect(int x, int y, int width, int height, Color color) {
		outlineRect(x, y, width, height, color.toInt());
	}

	default void outlineRect(int x, int y, int width, int height, int color) {
		throw BridgeUtil.noImpl();
	}

	// texture drawing

	@ApiStatus.NonExtendable
	default void drawTexture(Rectangle screenPos, Rectangle texturePos, AxoIdentifier texture) {
		drawTexture(
			screenPos.x, screenPos.y, screenPos.width, screenPos.height,
			texturePos.x, texturePos.y, texturePos.width, texturePos.height,
			texture
		);
	}

	default void drawTexture(
		int sx, int sy, int sw, int sh,
		int tx, int ty, int tw, int th,
		AxoIdentifier texture
	) {
		throw BridgeUtil.noImpl();
	}

	// item model rendering

	default void renderGuiItemModel(AxoItemStack stack, int x, int y) {
		throw BridgeUtil.noImpl();
	}

	default void renderGuiItemOverlay(AxoItemStack stack, int x, int y, String countLabel, int textColor, boolean shadow) {
		throw BridgeUtil.noImpl();
	}
	// misc methods
	default AxoTextRenderer getTextRenderer() {
		throw BridgeUtil.noImpl();
	}
}
