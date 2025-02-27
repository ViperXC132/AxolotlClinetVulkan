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

package io.github.axolotlclient.modules.hypixel;

import java.util.Map;
import java.util.Optional;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.axolotlclient.api.Response;

public record PlayerData(Bedwars bedwars, Skywars skywars, DuelsData duels, String rank, String rankFormatted,
						 double level, int karma) {

	private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	public record Bedwars(int level, GameData all, CombinedGameData core,
						  GameData solo, GameData doubles, GameData trios,
						  GameData fours,
						  GameData fourVFour, CombinedGameData dreams, GameData castle,
						  GameData doublesLucky,
						  GameData foursLucky, GameData doublesUltimate,
						  GameData foursUltimate,
						  GameData doublesArmed, GameData foursArmed, GameData doublesRush,
						  GameData foursRush,
						  GameData doublesSwap, GameData foursSwap) {
		public record GameData(int kills, int deaths, int wins, int losses, int winstreak, int finalKills,
							   int finalDeaths, int bedsBroken, int bedsLost) implements KDR, BedwarsGameData {
		}

		public record CombinedGameData(int kills, int deaths, int wins, int losses, int finalKills,
									   int finalDeaths, int bedsBroken, int bedsLost) implements KDR, BedwarsGameData {
		}

		public interface BedwarsGameData {

			int wins();

			int losses();

			int bedsBroken();

			int finalDeaths();

			int finalKills();

			default float fkdr() {
				return (float) finalKills() / finalDeaths();
			}

			default float bblr() {
				return (float) bedsBroken() / losses();
			}

			default float wlr() {
				return (float) wins() / losses();
			}
		}
	}

	public record Skywars(String level, int exp, GameData all, GameData core, ModeData solo,
						  ModeData team, MegaModeData mega, GameData ranked, int winstreak) {

		public record ModeData(GameData normal, GameData insane) {
		}

		public record MegaModeData(GameData normal, GameData doubles) {
		}

		public record GameData(int kills, int deaths, int wins, int losses) implements KDR {

		}
	}

	public record DuelsData(Map<String, DuelsGameData> modes) {
		public record DuelsGameData(int kills, int deaths, int wins, int losses, int winstreak) implements KDR {
		}
	}

	public static Optional<PlayerData> of(Response response) {
		if (response.isError()) {
			return Optional.empty();
		}
		PlayerData playerData = GSON.fromJson(response.getPlainBody(), PlayerData.class);
		return Optional.of(playerData);
	}

	public interface KDR {
		int kills();

		int deaths();

		default float kdr() {
			return (float) kills() / deaths();
		}
	}
}
