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

package io.github.axolotlclient.bridge.mixin;

import io.github.axolotlclient.bridge.AxoMinecraftClient;
import io.github.axolotlclient.bridge.entity.AxoPlayer;
import io.github.axolotlclient.bridge.key.AxoClientKeybinds;
import io.github.axolotlclient.bridge.render.AxoFont;
import io.github.axolotlclient.bridge.world.AxoWorld;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> implements AxoMinecraftClient {
	@Final
	@Shadow
	public TextRenderer textRenderer;

	@Shadow
	public ClientPlayerEntity player;

	@Shadow
	public ClientWorld world;

	@Final
	@Shadow
	public GameOptions options;

	public MinecraftClientMixin(String string) {
		super(string);
	}

	@Override
	public @Nullable AxoPlayer br$getPlayer() {
		return player;
	}

	@Override

	public AxoWorld br$getWorld() {
		return world;
	}

	@Override
	public AxoFont br$getFont() {
		return textRenderer;
	}

	@Override

	public AxoClientKeybinds br$getKeybinds() {
		return options;
	}

	@Override
	public CompletableFuture<Void> br$runTask(Runnable runnable) {
		return submit(runnable);
	}
}
