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

package io.github.axolotlclient.modules.hypixel;

import java.util.List;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.commands.PlayerArgument;
import io.github.axolotlclient.modules.hypixel.bedwars.BedwarsPlayerStats;
import lombok.Getter;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class StatsMod implements AbstractHypixelMod {
	private interface Handler {
		void accept(FabricClientCommandSource ctx, String uuid, String username);
	}

	private record Entry(String name, Handler handler) {
	}

	private static final List<Entry> HANDLERS = List.of(
		new Entry("bedwars", (c, uuid, username) -> {
			final var stats = BedwarsPlayerStats.fromAPI(uuid);
			c.sendFeedback(
				// TODO: color with rank, prestige
				Component.translatable("playerstats.bedwars.title", username, stats.getStars()).append("\n")
					.append(
						// TODO: colorize this more
						Component.translatable("playerstats.bedwars.kdr", stats.getKills(), stats.getDeaths(), stats.getKDR()))
					.append("\n")
					.append(Component.translatable("playerstats.bedwars.fkdr", stats.getFinalKills(), stats.getFinalDeaths(), stats.getFKDR()))
					.append("\n")
					.append(Component.translatable("playerstats.bedwars.beds", stats.getBedsBroken()))
					.append("\n")
					.append(Component.translatable("playerstats.bedwars.summary", stats.getWins(), stats.getWinstreak(), stats.getStars()))
			);
		})
	);

	@Getter
	private static StatsMod instance = new StatsMod();

	private final OptionCategory playerstats = OptionCategory.create("playerstats");

	@Override
	public void init() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, ctx) -> {
			final var command = literal("playerstats");

			for (Entry handler : HANDLERS) {
				command.then(literal(handler.name()).then(argument("player", PlayerArgument.player()).executes(c -> {
					if (!API.getInstance().getApiOptions().enabled.get()) {
						c.getSource().sendError(Component.translatable("playerstats.error.api_disabled").withStyle(ChatFormatting.RED));
						return -1;
					}
					if (!API.getInstance().isAuthenticated()) {
						c.getSource().sendError(Component.translatable("playerstats.error.api_unauthenticated").withStyle(ChatFormatting.RED));
						return -1;
					}

					final var res = PlayerArgument.get(c, "player");

					res.uuid().whenCompleteAsync((s, ex) -> {
						if (s.isEmpty()) {
							c.getSource().sendFeedback(Component.translatable("playerstats.error.unknown_player").withStyle(ChatFormatting.RED));
						} else {
							handler.handler().accept(c.getSource(), s.get(), res.playerName());
						}
					});

					return 0;
				})));
			}

			final var node = dispatcher.register(command);
			dispatcher.register(literal("pstats").redirect(node));
		});
	}

	@Override
	public OptionCategory getCategory() {
		return playerstats;
	}
}
