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
import io.github.axolotlclient.bridge.internal.RequiresImpl;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("unused")
public interface AxoRenderContext {
	// Matrix management
	@RequiresImpl
	default void br$popMatrix() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default void br$pushMatrix() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default void br$scaleMatrix(float sx, float sy) {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default void br$translateMatrix(float x, float y) {
		throw BridgeUtil.noImpl();
	}

	/**
	 * Rotate the current matrix
	 * @param ang the angle, in radians.
	 * @see Math#toRadians(double)
	 */
	@RequiresImpl
	default void br$rotateMatrix(float ang) {
		throw BridgeUtil.noImpl();
	}

	/**
	 * Rotate the current matrix
	 * @param ang the angle, in radians.
	 * @param x the x-coordinate of the rotation origin
	 * @param y the y-coordinate of the rotation origin
	 * @see Math#toRadians(double)
	 */
	default void br$rotateMatrixAround(float ang, float x, float y) {
		// naive default impl, may be overridden if a better impl is available in a version.
		br$translateMatrix(x, y);
		br$rotateMatrix(ang);
		br$translateMatrix(-x, -y);
	}

	// scissor
	@RequiresImpl
	default void br$pushScissor(int x, int y, int w, int h) {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default void br$popScissor() {
		throw BridgeUtil.noImpl();
	}

	// GL state management
	@RequiresImpl
	default void br$glEnableBlend() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default void br$glEnableAlpha() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default void br$glDisableBlend() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default void br$glDisableAlpha() {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default void br$glColor4(float r, float g, float b, float a) {
		throw BridgeUtil.noImpl();
	}

	// string rendering
	@RequiresImpl
	default int br$drawString(String value, int x, int y, int color, boolean shadow) {
		throw BridgeUtil.noImpl();
	}

	@ApiStatus.NonExtendable
	default int br$drawString(String value, int x, int y, int color) {
		return br$drawString(value, x, y, color, true);
	}

	@RequiresImpl
	default int br$drawString(AxoText value, int x, int y, int color, boolean shadow) {
		throw BridgeUtil.noImpl();
	}

	@ApiStatus.NonExtendable
	default int br$drawString(AxoText value, int x, int y, int color) {
		return br$drawString(value, x, y, color, true);
	}

	@ApiStatus.NonExtendable
	default int br$drawString(AxoText value, int x, int y, Color color) {
		return br$drawString(value, x, y, color, true);
	}

	@ApiStatus.NonExtendable
	default int br$drawString(String value, int x, int y, Color color, boolean shadow) {
		return br$drawString(value, x, y, color.toInt(), shadow);
	}

	@ApiStatus.NonExtendable
	default int br$drawString(String value, int x, int y, Color color) {
		return br$drawString(value, x, y, color, true);
	}

	@ApiStatus.NonExtendable
	default void br$drawCenteredString(String value, int x, int y, int color, boolean shadow) {
		br$drawString(value, x - br$getFont().br$getWidth(value) / 2, y, color, shadow);
	}

	@ApiStatus.NonExtendable
	default void br$drawCenteredString(String value, int x, int y, int color) {
		br$drawCenteredString(value, x, y, color, true);
	}

	@ApiStatus.NonExtendable
	default void br$drawCenteredString(String value, int x, int y, Color color, boolean shadow) {
		br$drawCenteredString(value, x, y, color.toInt(), shadow);
	}

	@ApiStatus.NonExtendable
	default void br$drawCenteredString(String value, int x, int y, Color color) {
		br$drawCenteredString(value, x, y, color, true);
	}

	@ApiStatus.NonExtendable
	default int br$drawString(AxoText value, int x, int y, Color color, boolean shadow) {
		return br$drawString(value, x, y, color.toInt(), shadow);
	}

	@ApiStatus.NonExtendable
	default void br$drawCenteredString(AxoText value, int x, int y, int color, boolean shadow) {
		br$drawString(value, x - br$getFont().br$getWidth(value) / 2, y, color, shadow);
	}

	@ApiStatus.NonExtendable
	default void br$drawCenteredString(AxoText value, int x, int y, Color color, boolean shadow) {
		br$drawCenteredString(value, x, y, color.toInt(), shadow);
	}

	@ApiStatus.NonExtendable
	default void br$drawCenteredString(AxoText value, int x, int y, Color color) {
		br$drawCenteredString(value, x, y, color, true);
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

	@RequiresImpl
	default void br$fillRect(int x, int y, int width, int height, int color) {
		throw BridgeUtil.noImpl();
	}

	@ApiStatus.NonExtendable
	default void br$fillRectRound(Rectangle rect, Color color, float rounding) {
		br$fillRectRound(rect.x, rect.y, rect.width, rect.height, color.toInt(), rounding);
	}

	@ApiStatus.NonExtendable
	default void br$fillRectRound(Rectangle rect, int color, float rounding) {
		br$fillRectRound(rect.x, rect.y, rect.width, rect.height, color, rounding);
	}

	@ApiStatus.NonExtendable
	default void br$fillRectRound(int x, int y, int width, int height, Color color, float rounding) {
		br$fillRectRound(x, y, width, height, color.toInt(), rounding);
	}

	@RequiresImpl
	default void br$fillRectRound(int x, int y, int width, int height, int color, float rounding) {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default void br$fillRectGradientVert(int x, int y, int width, int height, int color1, int color2) {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default void br$fillRectGradientHoriz(int x, int y, int width, int height, int color1, int color2) {
		throw BridgeUtil.noImpl();
	}

	// outlineRect overloads
	@ApiStatus.NonExtendable
	default void br$outlineRect(Rectangle rect, Color color) {
		br$outlineRect(rect.x, rect.y, rect.width, rect.height, color.toInt());
	}

	@ApiStatus.NonExtendable
	default void br$outlineRect(int x, int y, int width, int height, Color color) {
		br$outlineRect(x, y, width, height, color.toInt());
	}

	@RequiresImpl
	default void br$outlineRect(int x, int y, int width, int height, int color) {
		throw BridgeUtil.noImpl();
	}

	@ApiStatus.NonExtendable
	default void br$outlineRectRound(Rectangle rect, Color color, float rounding) {
		br$outlineRectRound(rect.x, rect.y, rect.width, rect.height, color.toInt(), rounding);
	}

	@ApiStatus.NonExtendable
	default void br$outlineRectRound(int x, int y, int width, int height, Color color, float rounding) {
		br$outlineRectRound(x, y, width, height, color.toInt(), rounding);
	}

	@RequiresImpl
	default void br$outlineRectRound(int x, int y, int width, int height, int color, float rounding) {
		throw BridgeUtil.noImpl();
	}

	// texture drawing
	@ApiStatus.NonExtendable
	default void br$drawTexture(Rectangle coords, AxoSprite texture) {
		br$drawTexture(coords.x, coords.y, coords.width, coords.height, texture);
	}

	@RequiresImpl
	default void br$drawTexture(int x, int y, int width, int height, AxoSprite sprite) {
		throw BridgeUtil.noImpl();
	}

	// item model rendering

	@RequiresImpl
	default void br$renderGuiItemModel(AxoItemStack stack, int x, int y) {
		throw BridgeUtil.noImpl();
	}

	@RequiresImpl
	default void br$renderGuiItemOverlay(AxoItemStack stack, int x, int y, String countLabel, int textColor, boolean shadow) {
		throw BridgeUtil.noImpl();
	}

	@ApiStatus.NonExtendable
	default void br$renderGuiItemOverlay(AxoItemStack stack, int x, int y, String countLabel) {
		br$renderGuiItemOverlay(stack, x, y, countLabel, 0xffffffff, true);
	}

	// misc methods
	@RequiresImpl
	default AxoFont br$getFont() {
		throw BridgeUtil.noImpl();
	}
}
