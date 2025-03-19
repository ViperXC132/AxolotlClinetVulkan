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

package io.github.axolotlclient.bridge.mixin.key;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.bridge.impl.AxoKeyImpl;
import io.github.axolotlclient.bridge.key.AxoKey;
import io.github.axolotlclient.bridge.key.AxoKeybinding;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * An abstract representation of a keybind
 */
@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements AxoKeybinding {
	@Shadow
	public abstract boolean isPressed();

	@Shadow
	private int keyCode;
	@Unique
	private final List<Runnable> axolotlclient$onClicked = new ArrayList<>();

	@Unique
	private final List<Runnable> axolotlclient$onReleased = new ArrayList<>();

	@Inject(method = "set", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/KeyBinding;pressed:Z"))
	private static void dispatchHandlers(int i, boolean bl, CallbackInfo ci, @Local KeyBinding binding) {
		if (bl) {
			((KeyBindingMixin) (Object) binding).axolotlclient$onClicked.forEach(Runnable::run);
		} else {
			((KeyBindingMixin) (Object) binding).axolotlclient$onReleased.forEach(Runnable::run);
		}
	}

	@Override
	public void br$registerOnClicked(Runnable runnable) {
		axolotlclient$onClicked.add(runnable);
	}

	@Override
	public void br$registerOnReleased(Runnable runnable) {
		axolotlclient$onReleased.add(runnable);
	}

	@Override
	public boolean br$isPressed() {
		return isPressed();
	}

	@Override
	public AxoKey br$getBoundKey() {
		return AxoKeyImpl.get(keyCode);
	}
}
