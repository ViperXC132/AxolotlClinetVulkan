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

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.axolotlclient.bridge.key.AxoKey;
import io.github.axolotlclient.bridge.key.AxoKeybinding;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * An abstract representation of a keybind
 */
@Mixin(KeyMapping.class)
public abstract class KeyBindingMixin implements AxoKeybinding {
	@Shadow
	public abstract boolean isDown();

	@Shadow
	private InputConstants.Key key;
	@Unique
	private final List<Runnable> axolotlclient$onClicked = new ArrayList<>();

	@Unique
	private final List<Runnable> axolotlclient$onReleased = new ArrayList<>();

	@Inject(method = "setDown", at = @At("HEAD"))
	private void dispatchHandlers(boolean pressed, CallbackInfo ci) {
		if (pressed) {
			this.axolotlclient$onClicked.forEach(Runnable::run);
		} else {
			this.axolotlclient$onReleased.forEach(Runnable::run);
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
		return isDown();
	}

	@Override
	public AxoKey br$getBoundKey() {
		return key;
	}
}
