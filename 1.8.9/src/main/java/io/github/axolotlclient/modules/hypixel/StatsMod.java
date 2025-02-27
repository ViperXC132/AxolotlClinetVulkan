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
import io.github.axolotlclient.commands.ClientCommandInfo;
import io.github.axolotlclient.commands.ClientCommands;
import io.github.axolotlclient.commands.PlayerArgument;
import io.github.axolotlclient.modules.hypixel.bedwars.BedwarsPlayerStats;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Formatting;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import static io.github.axolotlclient.commands.ClientCommands.argument;
import static io.github.axolotlclient.commands.ClientCommands.literal;

public class StatsMod implements AbstractHypixelMod {
	private interface Handler {
		void accept(ClientCommandInfo ctx, String uuid, String username, PlayerData data);
	}

	private record Entry(String name, Handler handler) {
	}

	private static final List<Entry> HANDLERS = List.of(
		new Entry("bedwars", (c, uuid, username, data) -> {
			final var allStats = data.bedwars().all();
			c.sendMessage(
				I18n.translate("playerstats.bedwars.title", (data.rankFormatted() + " " + username + Formatting.RESET), data.bedwars().level()),
				I18n.translate("playerstats.bedwars.kdr", allStats.kills(), allStats.deaths(), allStats.kdr()),
				I18n.translate("playerstats.bedwars.fkdr", allStats.finalKills(), allStats.finalDeaths(), allStats.fkdr()),
				I18n.translate("playerstats.bedwars.beds", allStats.bedsBroken(), allStats.bedsLost(), allStats.bblr()),
				I18n.translate("playerstats.bedwars.summary", allStats.wins(), allStats.losses(), allStats.wlr(), allStats.winstreak())
			);
		})
	);

	@Getter
	private static StatsMod instance = new StatsMod();

	private final OptionCategory playerstats = OptionCategory.create("playerstats");

	@Override
	public void init() {
		final var command = literal("playerstats");

		for (Entry handler : HANDLERS) {
			command.then(literal(handler.name()).then(argument("player", PlayerArgument.player()).executes(c -> {
				if (!API.getInstance().getApiOptions().enabled.get()) {
					c.getSource().sendMessage(Formatting.RED + I18n.translate("playerstats.error.api_disabled"));
					return -1;
				}
				if (!API.getInstance().isAuthenticated()) {
					c.getSource().sendMessage(Formatting.RED + I18n.translate("playerstats.error.api_unauthenticated"));
					return -1;
				}

				final var res = PlayerArgument.get(c, "player");

				res.uuid().whenCompleteAsync((uuid, ex) -> {
					if (uuid.isEmpty()) {
						c.getSource().sendMessageAsync(new LiteralText(Formatting.RED + I18n.translate("playerstats.error.unknown_player")));
					} else {
						HypixelAbstractionLayer.getInstance().getPlayerDataApi().getAsync(uuid.get()).whenCompleteAsync((playerData, throwable) -> {
							if(playerData.isEmpty()) {
								c.getSource().sendMessage(Formatting.RED + I18n.translate("playerstats.error.failed_data"));
								return;
							}

							handler.handler().accept(c.getSource(), uuid.get(), res.playerName(), playerData.get());
						}, Minecraft.getInstance()::submit);
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
