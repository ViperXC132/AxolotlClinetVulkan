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

package io.github.axolotlclient.modules.hypixel.nickhider;

import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringOption;
import io.github.axolotlclient.bridge.AxoMinecraftClient;
import io.github.axolotlclient.bridge.entity.AxoPlayer;
import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.modules.hypixel.AbstractHypixelMod;
import lombok.Getter;

public class NickHider implements AbstractHypixelMod {

	@Getter
	private static final NickHider Instance = new NickHider();
	public final StringOption hiddenNameSelf = new StringOption("hiddenNameSelf", "You");
	public final StringOption hiddenNameOthers = new StringOption("hiddenNameOthers", "Player");
	public final BooleanOption hideOwnName = new BooleanOption("hideOwnName", false);
	public final BooleanOption hideOtherNames = new BooleanOption("hideOtherNames", false);
	public final BooleanOption hideOwnSkin = new BooleanOption("hideOwnSkin", false);
	public final BooleanOption hideOtherSkins = new BooleanOption("hideOtherSkins", false);
	private final OptionCategory category = OptionCategory.create("nickhider");

	@Override
	public void init() {
		category.add(hiddenNameSelf);
		category.add(hiddenNameOthers);
		category.add(hideOwnName);
		category.add(hideOtherNames);
		category.add(hideOwnSkin);
		category.add(hideOtherSkins);
	}

	@Override
	public OptionCategory getCategory() {
		return category;
	}

	public AxoText editMessage(AxoText message) {
		if (hideOwnName.get() || hideOtherNames.get()) {
			String msg = message.toString(); // TODO .getFormattedString();
			String playerName = AxoMinecraftClient.getInstance().br$getPlayer().br$getGameProfile().br$getName();
			if (hideOwnName.get() && msg.contains(playerName)) {
				msg = msg.replaceAll(playerName, hiddenNameSelf.get());
			}

			if (hideOtherNames.get()) {
				for (AxoPlayer player : AxoMinecraftClient.getInstance().br$getWorld().br$getPlayers()) {
					if (msg.contains(player.br$getGameProfile().br$getName())) {
						msg = msg.replaceAll(player.br$getGameProfile().br$getName(), hiddenNameOthers.get());
					}
				}
			}

			// TODO: style api
			return AxoText.literal(msg); //.setStyle(message.getStyle().deepCopy());
		}
		return message;
	}
}
