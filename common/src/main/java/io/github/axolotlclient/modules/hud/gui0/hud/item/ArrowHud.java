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

package io.github.axolotlclient.modules.hud.gui0.hud.item;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.bridge.entity.AxoPlayer;
import io.github.axolotlclient.bridge.item.AxoItemClass;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.item.AxoItems;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.gui0.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.util.ItemUtil;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */
public class ArrowHud extends TextHudEntry {
	private static final AxoIdentifier ID = AxoIdentifier.of("kronhud", "arrowhud");
	private static final AxoItemStack ITEM = AxoItemStack.of(AxoItems.ARROW);
	private final BooleanOption dynamic = new BooleanOption("dynamic", false);
	private int arrows = 0;

	public ArrowHud() {
		super(20, 30, true);
	}

	@Override
	public void render(AxoRenderContext context, float delta) {
		if (dynamic.get()) {
			AxoPlayer player = client.br$getPlayer();
			if (player == null) {
				return;
			}

			if(!player.br$getInventory().br$getMainHand().br$getItem().br$is(AxoItemClass.BOW)) {
				return;
			}
		}
		super.render(context, delta);
	}

	private void doRender(AxoRenderContext context, int count) {
		DrawPosition pos = getPos();
		context.br$drawCenteredString(
			String.valueOf(count), pos.x() + getWidth() / 2,
			pos.y() + getHeight() - 10,
			textColor.get().toInt(),
			shadow.get()
		);
		context.br$renderGuiItemModel(ITEM, pos.x() + 2, pos.y() + 2);
	}

	@Override
	public void renderComponent(AxoRenderContext context,float delta) {
		doRender(context, arrows);
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext context, float delta) {
		doRender(context, 64);
	}

	@Override
	public boolean tickable() {
		return true;
	}

	@Override
	public void tick() {
		arrows = ItemUtil.getTotal(client, AxoItems.ARROW);
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(dynamic);
		return options;
	}

	@Override
	public AxoIdentifier getId() {
		return ID;
	}
}
