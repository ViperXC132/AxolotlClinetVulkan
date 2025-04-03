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

package io.github.axolotlclient.modules.hypixel.bedwars;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.bridge.item.AxoItem;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.item.AxoItems;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.gui0.entry.BoxHudEntry;
import io.github.axolotlclient.util.ItemUtil;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ResourceOverlay extends BoxHudEntry {
	public final static AxoIdentifier ID = AxoIdentifier.of("axolotlclient", "bedwars_resources");
	private final BooleanOption renderWhenRelevant = new BooleanOption(ID.br$getPath() + ".renderWhenRelevant", true);
	private static final List<AxoItem> RESOURCES = List.of(AxoItems.IRON_INGOT, AxoItems.GOLD_INGOT, AxoItems.DIAMOND, AxoItems.EMERALD);
	private static final Map<AxoItem, Integer> PLACEHOLDER = Map.of(
		AxoItems.IRON_INGOT, 43,
		AxoItems.GOLD_INGOT, 7,
		AxoItems.DIAMOND, 7,
		AxoItems.EMERALD, 4
	);
	private final BedwarsMod mod;

	public ResourceOverlay(BedwarsMod mod) {
		super(4 * 18 + 1, 18 + 1, true);
		this.mod = mod;
	}

	@Override
	public void render(AxoRenderContext context, float delta) {
		if (!renderWhenRelevant.get() || mod.inGame()) {
			super.render(context, delta);
		}
	}

	@Override
	public void renderComponent(AxoRenderContext context, float delta) {
		draw(context, s -> ItemUtil.getTotal(client, s));
	}

	private void draw(AxoRenderContext context, Function<AxoItem, Integer> countFunction) {
		var pos = getPos();
		int x = pos.x() + 1;
		int y = pos.y() + 1;
		for (AxoItem item : RESOURCES) {
			int amount = countFunction.apply(item);
			final var stack = AxoItemStack.of(item, amount);
			if (amount > 0) {
				context.br$renderGuiItemModel(stack, x, y);
				context.br$renderGuiItemOverlay(stack, x, y, String.valueOf(amount), -1, true);
				x += 18;
			}
		}
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext context, float delta) {
		draw(context, PLACEHOLDER::get);
	}

	@Override
	public AxoIdentifier getId() {
		return ID;
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(renderWhenRelevant);
		return options;
	}
}
