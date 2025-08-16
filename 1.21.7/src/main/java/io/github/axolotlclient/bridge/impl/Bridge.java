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

package io.github.axolotlclient.bridge.impl;

import com.mojang.brigadier.CommandDispatcher;
import io.github.axolotlclient.bridge.events.Events;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class Bridge {
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void init() {
		ClientLifecycleEvents.CLIENT_STARTED.register(minecraft -> Events.CLIENT_START.invoker().run());
		ClientLifecycleEvents.CLIENT_STOPPING.register(minecraft -> Events.CLIENT_STOP.invoker().run());
		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> Events.TICK.invoker().run());
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

			@Override
			public void onResourceManagerReload(ResourceManager resourceManager) {
				Events.END_RESOURCE_RELOAD.invoker().run();
			}

			@Override
			public ResourceLocation getFabricId() {
				return ResourceLocation.fromNamespaceAndPath("axolotlclient", "bridge/resource_listener");
			}
		});
		ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, minecraftClient) -> Events.CONNECTION_PLAY_READY.invoker().run());
		ClientPlayConnectionEvents.DISCONNECT.register((clientPlayNetworkHandler, minecraftClient) -> Events.DISCONNECT.invoker().run());

		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext) ->
			Events.COMMAND_REGISTER.invoker().accept(() -> (CommandDispatcher) commandDispatcher));
	}
}
