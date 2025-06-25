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

package io.github.axolotlclient.modules.hud.gui.hud.vanilla;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.entry.BoxHudEntry;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.Identifier;

public class InventoryHud extends BoxHudEntry implements DynamicallyPositionable {
	public static final Identifier ID = new Identifier(AxolotlClient.MODID, "inventoryhud");
	private static final ItemStack[] PLACEHOLDER = new ItemStack[]{
		new ItemStack(Blocks.STONE), new ItemStack(Blocks.STONE), new ItemStack(Blocks.STONE), new ItemStack(Blocks.STONE), new ItemStack(Blocks.STONE), new ItemStack(Blocks.STONE), new ItemStack(Blocks.STONE), new ItemStack(Blocks.STONE), new ItemStack(Blocks.STONE),
		null, null, null, null, null, null, null, null, null,
		new ItemStack(Items.STONE_SWORD), new ItemStack(Items.STONE_PICKAXE), new ItemStack(Items.STONE_AXE), new ItemStack(Items.STONE_SHOVEL), new ItemStack(Items.STONE_HOE), null, null, null, new ItemStack(Items.GLOWSTONE_DUST, 63)
	};
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
	public void renderComponent(float delta) {
		ItemStack[] inventorySlots = client.player.inventory.inventorySlots;
		var pos = getPos();
		int x = pos.x() + 2;
		int y = pos.y() + 2;
		for (int i = 0, inventorySlotsLength = inventorySlots.length; i < inventorySlotsLength; i++) {
			ItemStack stack = inventorySlots[i];
			if (stack != null) {
				renderStack(x + (i%9) * ITEM_SIZE, y + (i / 9) * ITEM_SIZE, stack);
			}
		}
	}


	private void renderStack(int x, int y, ItemStack stack) {
		if (background.get() && backgroundColor.get().getAlpha() > 0) {
			fillRect(x, y, ITEM_TILE_SIZE, ITEM_TILE_SIZE, backgroundColor.get().toInt());
		}
		ItemUtil.renderGuiItemModel(stack, x, y);
		ItemUtil.renderGuiItemOverlay(client.textRenderer, stack, x, y, null, -1, true);
	}

	@Override
	public void renderPlaceholderComponent(float delta) {
		ItemStack[] inventorySlots = PLACEHOLDER;
		var pos = getPos();
		int x = pos.x() + 2;
		int y = pos.y() + 2;
		for (int i = 0, inventorySlotsLength = inventorySlots.length; i < inventorySlotsLength; i++) {
			ItemStack stack = inventorySlots[i];
			if (stack != null) {
				renderStack(x + (i%9) * ITEM_SIZE, y + (i / 9) * ITEM_SIZE, stack);
			}
		}
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public AnchorPoint getAnchor() {
		return AnchorPoint.MIDDLE_MIDDLE;
	}
}
