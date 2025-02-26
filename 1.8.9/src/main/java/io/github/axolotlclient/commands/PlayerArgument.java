/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.commands;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.axolotlclient.api.util.UUIDHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;

public class PlayerArgument implements ArgumentType<PlayerArgument.OfflinePlayerInfo> {
	public record OfflinePlayerInfo(String playerName, CompletableFuture<Optional<String>> uuid) {
	}

	private static final Pattern NAME_REGEX = Pattern.compile("[a-zA-Z0-9_]{2,16}");

	@Override
	public OfflinePlayerInfo parse(StringReader stringReader) {
		String playerName = stringReader.readUnquotedString();
		return new OfflinePlayerInfo(playerName, UUIDHelper.USERNAME_TO_UUID.getAsync(playerName));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		ClientPlayNetworkHandler handler = Minecraft.getInstance().getNetworkHandler();
		if (handler == null) {
			return builder.buildFuture();
		}

		handler.getOnlinePlayers()
			.stream()
			.map(playerInfo -> playerInfo.getProfile().getName())
			.filter(name -> NAME_REGEX.matcher(name).matches())
			.filter(name -> name.startsWith(builder.getRemaining()))
			.forEach(builder::suggest);

		return builder.buildFuture();
	}

	public static OfflinePlayerInfo get(CommandContext<?> context, String name) {
		return context.getArgument(name, OfflinePlayerInfo.class);
	}

	public static PlayerArgument player() {
		return new PlayerArgument();
	}
}
