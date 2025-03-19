/*
 * Copyright © 2025 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.bridge.render;

import io.github.axolotlclient.AxolotlClientConfig.api.util.Color;
import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import org.jetbrains.annotations.ApiStatus;

public interface AxoRenderContext {
	// Matrix management
	default void br$popMatrix() {
		throw BridgeUtil.noImpl();
	}

	default void br$pushMatrix() {
		throw BridgeUtil.noImpl();
	}

	default void br$scaleMatrix(float sx, float sy, float sz) {
		throw BridgeUtil.noImpl();
	}

	default void br$translateMatrix(float x, float y, float z) {
		throw BridgeUtil.noImpl();
	}

	// GL state management
	default void br$glEnableBlend() {
		throw BridgeUtil.noImpl();
	}

	default void br$glDisableBlend() {
		throw BridgeUtil.noImpl();
	}

	default void br$glColor4(float r, float g, float b, float a) {
		throw BridgeUtil.noImpl();
	}

	// string rendering
	default int br$drawString(String value, int x, int y, int color, boolean shadow) {
		throw BridgeUtil.noImpl();
	}

	@ApiStatus.NonExtendable
	default int br$drawString(String value, int x, int y, Color color, boolean shadow) {
		return br$drawString(value, x, y, color.toInt(), shadow);
	}

	@ApiStatus.NonExtendable
	default void br$drawCenteredString(String value, int x, int y, int color, boolean shadow) {
		br$drawString(value, x - br$getFont().br$getWidth(value) / 2, y, color, shadow);
	}

	@ApiStatus.NonExtendable
	default void br$drawCenteredString(String value, int x, int y, Color color, boolean shadow) {
		br$drawCenteredString(value, x, y, color.toInt(), shadow);
	}
	// fillRect overloads
	@ApiStatus.NonExtendable
	default void br$fillRect(Rectangle rect, Color color) {
		br$fillRect(rect.x, rect.y, rect.width, rect.height, color.toInt());
	}

	@ApiStatus.NonExtendable
	default void br$fillRect(Rectangle rect, int color) {
		br$fillRect(rect.x, rect.y, rect.width, rect.height, color);
	}

	@ApiStatus.NonExtendable
	default void br$fillRect(int x, int y, int width, int height, Color color) {
		br$fillRect(x, y, width, height, color.toInt());
	}

	default void br$fillRect(int x, int y, int width, int height, int color) {
		throw BridgeUtil.noImpl();
	}

	// outlineRect overloads
	@ApiStatus.NonExtendable
	default void br$outlineRect(Rectangle rect, Color color) {
		br$outlineRect(rect.x, rect.y, rect.width, rect.height, color.toInt());
	}

	default void br$outlineRect(int x, int y, int width, int height, Color color) {
		br$outlineRect(x, y, width, height, color.toInt());
	}

	default void br$outlineRect(int x, int y, int width, int height, int color) {
		throw BridgeUtil.noImpl();
	}

	// texture drawing

	@ApiStatus.NonExtendable
	default void br$drawTexture(Rectangle screenPos, Rectangle texturePos, AxoIdentifier texture) {
		br$drawTexture(
			screenPos.x, screenPos.y, texturePos.x, texturePos.y, screenPos.width, screenPos.height,
			texturePos.width, texturePos.height,
			texture
		);
	}

	default void br$drawTexture(
		int sx, int sy, int tx, int ty, int sw, int sh,
		int tw, int th,
		AxoIdentifier texture
	) {
		throw BridgeUtil.noImpl();
	}

	// item model rendering

	default void br$renderGuiItemModel(AxoItemStack stack, int x, int y) {
		throw BridgeUtil.noImpl();
	}

	default void br$renderGuiItemOverlay(AxoItemStack stack, int x, int y, String countLabel, int textColor, boolean shadow) {
		throw BridgeUtil.noImpl();
	}
	// misc methods
	default AxoFont br$getFont() {
		throw BridgeUtil.noImpl();
	}
}
