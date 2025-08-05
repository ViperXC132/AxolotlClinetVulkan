/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.render.AxoSprites;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.gui.entry.BoxHudEntry;

public class IconHud extends BoxHudEntry {
	private static final AxoIdentifier ID = AxoIdentifier.of("axolotlclient", "iconhud");

	public IconHud() {
		super(16, 16, false);
	}

	@Override
	public AxoIdentifier getId() {
		return ID;
	}

	@Override
	public void renderComponent(AxoRenderContext ctx, float delta) {
		ctx.br$glColor4(1, 1, 1, 1);
		ctx.br$glEnableBlend();
		ctx.br$drawTexture(getX(), getY(), width, height, AxoSprites.BADGE);
		ctx.br$glDisableBlend();
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext ctx, float delta) {
		renderComponent(ctx, delta);
	}
}
