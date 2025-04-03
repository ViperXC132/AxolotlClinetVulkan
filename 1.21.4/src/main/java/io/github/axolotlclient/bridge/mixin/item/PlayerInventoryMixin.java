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

package io.github.axolotlclient.bridge.mixin.item;

import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.item.AxoPlayerInventory;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Inventory.class)
public abstract class PlayerInventoryMixin implements AxoPlayerInventory {
	@Shadow
	public abstract ItemStack getSelected();

	@Shadow
	public abstract int getContainerSize();

	@Shadow
	@Final
	public NonNullList<ItemStack> armor;

	@Shadow
	public abstract ItemStack getArmor(int slot);

	@Shadow
	public abstract ItemStack getItem(int slot);

	@Override
	public AxoItemStack br$getMainHand() {
		return getSelected();
	}

	@Override
	public List<? extends AxoItemStack> br$getItems() {
		return IntStream.range(0, getContainerSize())
			.mapToObj(this::getItem)
			.toList();
	}

	@Override
	public List<? extends AxoItemStack> br$getArmor() {
		return IntStream.range(0, armor.size())
			.mapToObj(this::getArmor)
			.toList();
	}
}
