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

package io.github.axolotlclient.modules.hud.gui0.hud.vanilla;

import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.item.AxoItems;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.gui0.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui0.entry.BoxHudEntry;
import io.github.axolotlclient.modules.hud.gui0.layout.AnchorPoint;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class InventoryHud extends BoxHudEntry implements DynamicallyPositionable {
	public static final AxoIdentifier ID = AxoIdentifier.of(AxolotlClientCommon.MODID, "inventoryhud");
	private static final List<AxoItemStack> PLACEHOLDER = Stream.of(
		IntStream.range(0, 9).mapToObj(x -> AxoItemStack.of(AxoItems.STONE)),
		IntStream.range(0, 9).mapToObj(x -> (AxoItemStack) null),
		Stream.of(
			AxoItemStack.of(AxoItems.STONE_SWORD),
			AxoItemStack.of(AxoItems.STONE_PICKAXE),
			AxoItemStack.of(AxoItems.STONE_AXE),
			AxoItemStack.of(AxoItems.STONE_SHOVEL),
			AxoItemStack.of(AxoItems.STONE_HOE),
			null, null, null,
			AxoItemStack.of(AxoItems.GLOWSTONE_DUST, 63)
		)
	).flatMap(x -> x).toList();

	private static final int ITEM_SIZE = 18;
	private static final int ITEM_TILE_SIZE = 16;

	public InventoryHud() {
		super(164, 56, true);
	}

	@Override
	public double getDefaultX() {
		return 0.5;
	}

	@Override
	public double getDefaultY() {
		return 0.76;
	}

	@Override
	public void renderComponent(AxoRenderContext graphics, float delta) {
		render(graphics, client.br$getPlayer().br$getInventory().br$getNonEquipmentItems());
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext graphics, float delta) {
		render(graphics, PLACEHOLDER);
	}

	private void render(AxoRenderContext graphics, List<? extends AxoItemStack> inventorySlots) {
		var pos = getPos();
		int x = pos.x() + 2;
		int y = pos.y() + 2;

		for (int i = 0, inventorySlotsLength = inventorySlots.size(); i < inventorySlotsLength; i++) {
			AxoItemStack stack = inventorySlots.get(i);
			if (stack != null && !stack.br$isEmpty()) {
				renderStack(graphics, x + (i % 9) * ITEM_SIZE, y + (i / 9) * ITEM_SIZE, stack);
			}
		}
	}


	private void renderStack(AxoRenderContext graphics, int x, int y, AxoItemStack itemStack) {
		if (background.get() && backgroundColor.get().getAlpha() > 0) {
			graphics.br$fillRect(x, y, ITEM_TILE_SIZE, ITEM_TILE_SIZE, backgroundColor.get().toInt());
		}

		graphics.br$renderGuiItemModel(itemStack, x, y);
		graphics.br$renderGuiItemOverlay(itemStack, x, y, null);
	}

	@Override
	public AxoIdentifier getId() {
		return ID;
	}

	@Override
	public AnchorPoint getAnchor() {
		return AnchorPoint.MIDDLE_MIDDLE;
	}
}
