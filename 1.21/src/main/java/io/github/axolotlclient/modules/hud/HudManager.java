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

package io.github.axolotlclient.modules.hud;

import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.modules.hud.gui.hud.KeystrokeHud;
import io.github.axolotlclient.modules.hud.gui.hud.PackDisplayHud;
import io.github.axolotlclient.modules.hud.gui.hud.PlayerHud;
import io.github.axolotlclient.modules.hud.gui.hud.vanilla.*;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.ComboHud;
import io.github.axolotlclient.modules.hud.gui0.hud.simple.ReachHud;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */
public class HudManager extends HudManagerCommon {
	@Getter
	private final static HudManager instance = new HudManager();

	@Override
	protected void openScreen() {
		MinecraftClient.getInstance().setScreen(new HudEditScreen());
	}

	@Override
	protected void addExtraHud() {
		add(new ActionBarHud());
		add(new BossBarHud());
		add(new CrosshairHud());
		add(new DebugCountersHud());
		add(new HotbarHUD());
		add(new ScoreboardHud());
		add(new KeystrokeHud());
		add(new PackDisplayHud());
		add(new PlayerHud());

		((ReachHud) get(ReachHud.ID)).getEnabled().setForceOff(true, "feature.broken");
		((ComboHud) get(ComboHud.ID)).getEnabled().setForceOff(true, "feature.broken");
	}

	@Override
	public void render(AxoRenderContext context, float delta) {
		final var mc = ((MinecraftClient) client);
		mc.getProfiler().push("Hud render");
		if(!(mc.currentScreen instanceof HudEditScreen)) {
			super.render(context, delta);
		}
		mc.getProfiler().pop();
	}
}
