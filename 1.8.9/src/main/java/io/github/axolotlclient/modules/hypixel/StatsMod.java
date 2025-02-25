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

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.commands.ClientCommandInfo;
import io.github.axolotlclient.commands.ClientCommands;
import io.github.axolotlclient.commands.OfflinePlayerArgument;
import io.github.axolotlclient.modules.hypixel.bedwars.BedwarsPlayerStats;
import java.util.List;
import lombok.Getter;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;

import static io.github.axolotlclient.commands.ClientCommands.argument;
import static io.github.axolotlclient.commands.ClientCommands.literal;

public class StatsMod implements AbstractHypixelMod {
	private interface Handler {
		void accept(ClientCommandInfo ctx, String uuid, String username);
	}

	private record Entry(String name, Handler handler) {
	}

	private static final List<Entry> HANDLERS = List.of(
		new Entry("bedwars", (c, uuid, username) -> {
				final var stats = BedwarsPlayerStats.fromAPI(uuid);
				// TODO: i18n
				c.sendMessageAsync(
					// TODO: color with rank, prestige
					"[Bedwars Stats for %s (%s☆)]".formatted(username, stats.getStars()),
					// TODO: colorize this more
					"Kill Death Ratio: %s/%s (%s)".formatted(stats.getKills(), stats.getDeaths(), stats.getKDR()),
					"Final Kill Death Ratio: %s/%s (%s)".formatted(stats.getFinalKills(), stats.getFinalDeaths(), stats.getFKDR()),
					"Beads broken: %s".formatted(stats.getBedsBroken()),
					"Wins %s | WS %s | %s stars".formatted(stats.getWins(), stats.getLosses(), stats.getStars())
				);
		})
	);

	@Getter
	private static StatsMod instance = new StatsMod();
	@Getter
	private final OptionCategory playerstats = OptionCategory.create("playerstats");

	@Override
	public void init() {
		final var command = literal("playerstats");

		for (Entry handler : HANDLERS) {
			command.then(literal(handler.name()).then(argument("player", OfflinePlayerArgument.player()).executes(c -> {
				if (!API.getInstance().getApiOptions().enabled.get()) {
					c.getSource().sendMessage(Formatting.RED + "API is not enabled!");
					return -1;
				}

				final var res = OfflinePlayerArgument.get(c, "player");

				res.uuid().whenCompleteAsync((s, ex) -> {
					if (s.isEmpty()) {
						c.getSource().sendMessageAsync(new LiteralText(Formatting.RED + "Unknown player!"));
					} else {
						handler.handler().accept(c.getSource(), s.get(), res.playerName());
					}
				});

				return 0;
			})));
		}

		final var node = ClientCommands.getDISPATCHER().register(command);
		ClientCommands.getDISPATCHER().register(literal("pstats").redirect(node));
	}

	@Override
	public OptionCategory getCategory() {
		return playerstats;
	}
}
