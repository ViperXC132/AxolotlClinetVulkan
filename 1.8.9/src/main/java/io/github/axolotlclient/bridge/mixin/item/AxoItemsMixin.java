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

import io.github.axolotlclient.bridge.impl.AirItemImpl;
import io.github.axolotlclient.bridge.item.AxoItem;
import io.github.axolotlclient.bridge.item.AxoItems;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AxoItems.class, remap = false)
public class AxoItemsMixin {
	@Mutable
	@Shadow
	@Final
	public static AxoItem AIR;

	@Mutable
	@Shadow
	@Final
	public static AxoItem IRON_HELMET;

	@Mutable
	@Shadow
	@Final
	public static AxoItem IRON_LEGGINGS;

	@Mutable
	@Shadow
	@Final
	public static AxoItem IRON_CHESTPLATE;

	@Mutable
	@Shadow
	@Final
	public static AxoItem IRON_BOOTS;

	@Mutable
	@Shadow
	@Final
	public static AxoItem IRON_SWORD;

	@Mutable
	@Shadow
	@Final
	public static AxoItem ARROW;

	@Inject(method = "<clinit>", at = @At("HEAD"), cancellable = true)
	private static void setStaticValues(CallbackInfo info) {
		AIR = AirItemImpl.getInstance();
		IRON_HELMET = Items.IRON_HELMET;
		IRON_CHESTPLATE = Items.IRON_CHESTPLATE;
		IRON_LEGGINGS = Items.IRON_LEGGINGS;
		IRON_BOOTS = Items.IRON_BOOTS;
		IRON_SWORD = Items.IRON_SWORD;
		ARROW = Items.ARROW;
		info.cancel();
	}
}
