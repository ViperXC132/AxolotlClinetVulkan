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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.github.axolotlclient.bridge.AxoMinecraftClient;
import io.github.axolotlclient.bridge.AxoPlayerListEntry;
import io.github.axolotlclient.bridge.AxoSession;
import io.github.axolotlclient.bridge.entity.AxoPlayer;
import io.github.axolotlclient.bridge.key.AxoClientKeybinds;
import io.github.axolotlclient.bridge.render.AxoFont;
import io.github.axolotlclient.bridge.resource.AxoResourceManager;
import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.bridge.world.AxoWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.resource.manager.ReloadableResourceManager;
import net.minecraft.client.resource.manager.ResourceManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin implements AxoMinecraftClient {
	@Shadow
	public TextRenderer textRenderer;

	@Shadow
	public LocalClientPlayerEntity player;

	@Shadow
	public ClientWorld world;

	@Shadow
	public GameOptions options;

	@Shadow
	@Final
	private Session session;

	@Shadow
	public abstract boolean isInSingleplayer();

	@Shadow
	public abstract ServerListEntry getCurrentServerEntry();

	@Shadow
	public GameGui gui;

	@Shadow
	public Screen screen;

	@Shadow
	private ReloadableResourceManager resourceManager;

	@Shadow
	public abstract ResourceManager getResourceManager();

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
	public AxoSession br$getSession() {
		return new AxoSession(session.getUsername(), session.getUuid(), session.getAccessToken());
	}

	@Override
	public boolean br$isLocalServer() {
		return isInSingleplayer();
	}

	@Override
	public String br$getServerAddress() {
		return Optional.ofNullable(getCurrentServerEntry()).map(x -> x.address).orElse(null);
	}

	@Override
	public Collection<? extends AxoPlayerListEntry> br$getOnlinePlayers() {
		return player == null ? List.of()
			: Collections.unmodifiableCollection(player.networkHandler.getOnlinePlayers());
	}

	@Override
	public void br$sendToClient(AxoText msg) {
		gui.getChat().addMessage((Text) msg);
	}

	@Override
	public void br$sendToServer(String msg) {
		player.sendChat(msg);
	}

	@Override
	public void br$reinitScreen() {
		if (screen != null) {
			screen.init((Minecraft) (Object) this, screen.width, screen.height);
		}
	}

	@Override
	public AxoResourceManager br$getResourceManager() {
		return getResourceManager();
	}
}
