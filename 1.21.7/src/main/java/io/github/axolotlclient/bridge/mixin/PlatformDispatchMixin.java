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

import com.mojang.blaze3d.platform.NativeImage;
import io.github.axolotlclient.bridge.PlatformDispatch;
import io.github.axolotlclient.bridge.impl.AxoSpriteImpl;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.util.ThreadExecuter;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.renderer.RenderPipelines;
import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = PlatformDispatch.class, remap = false)
public class PlatformDispatchMixin {
	@Unique
	private static final ServerStatusPinger axo$pinger = new ServerStatusPinger();

	@Unique
	private static void getRealTimeServerPing(ServerData server, MutableInt currentServerPing) {
		ThreadExecuter.scheduleTask(() -> {
			try {
				axo$pinger.pingServer(server, () -> {
				}, () -> currentServerPing.setValue(server.ping));
			} catch (Exception ignored) {
			}
		});
	}

	/**
	 * @author Flowey
	 * @reason Implement bridge.
	 */
	@Overwrite
	public static void pingHud$updatePing(MutableInt currentServerPing) {
		final var minecraft = Minecraft.getInstance();
		if (minecraft.getCurrentServer() != null) {
			getRealTimeServerPing(minecraft.getCurrentServer(), currentServerPing);
		} else if (minecraft.hasSingleplayerServer()) {
			currentServerPing.setValue(1);
		}
	}

	/**
	 * @author Flowey
	 * @reason Implement bridge.
	 */
	@Overwrite
	public static AxoSprite.Dynamic ipHud$getServerIcon() throws IOException {
		final var minecraft = Minecraft.getInstance();
		final var icon = FaviconTexture.forServer(minecraft.getTextureManager(), Objects.requireNonNull(minecraft.getCurrentServer()).ip);

		try {
			icon.upload(NativeImage.read(minecraft.getCurrentServer().getIconBytes()));
		} catch (Throwable e) {
			icon.close();
			throw e;
		}

		class Impl implements AxoSprite.Dynamic, AxoSpriteImpl {
			@Override
			public void draw(Minecraft client, GuiGraphics stack, int sX, int sY, int sW, int sH) {
				stack.blit(RenderPipelines.GUI_TEXTURED, icon.textureLocation(), sX, sY, 0, 0, sW, sH, 16, 16, -1);
			}

			@Override
			public void close() {
				icon.close();
			}
		}

		return new Impl();
	}
}
