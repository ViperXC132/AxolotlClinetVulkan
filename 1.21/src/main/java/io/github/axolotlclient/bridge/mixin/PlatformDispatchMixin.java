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

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.texture.NativeImage;
import io.github.axolotlclient.AxolotlClientConfig.impl.util.GraphicsImpl;
import io.github.axolotlclient.bridge.PlatformDispatch;
import io.github.axolotlclient.bridge.impl.AxoSpriteImpl;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.util.ThreadExecuter;
import net.minecraft.class_9191;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.query.MetadataQueryC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.ServerMetadataS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = PlatformDispatch.class, remap = false)
public class PlatformDispatchMixin {
	@Unique
	private static void getRealTimeServerPing(ServerInfo server, MutableInt currentServerPing) {
		ThreadExecuter.scheduleTask(() -> {
			try {
				var address = ServerAddress.parse(server.address);
				var optional = AllowedAddressResolver.DEFAULT.resolve(address).map(Address::getInetSocketAddress);

				if (optional.isPresent()) {
					final ClientConnection clientConnection = ClientConnection.connect(optional.get(), false, (class_9191) null);
					ClientQueryPacketListener listener = new ClientQueryPacketListener() {

						private long currentSystemTime = 0L;

						@Override
						public void onServerMetadata(ServerMetadataS2CPacket packet) {
							this.currentSystemTime = Util.getMeasuringTimeMs();
							clientConnection.send(new QueryPingC2SPacket(this.currentSystemTime));
						}

						@Override
						public void onQueryPong(QueryPongS2CPacket packet) {
							var time = this.currentSystemTime;
							var latency = Util.getMeasuringTimeMs();
							currentServerPing.setValue((int) (latency - time));
							clientConnection.disconnect(Text.translatable("multiplayer.status.finished"));
						}

						@Override
						public void onDisconnected(DisconnectionDetails reason) {
						}

						@Override
						public boolean isConnected() {
							return clientConnection.isOpen();
						}
					};
					clientConnection.connect(address.getAddress(), address.getPort(), listener);
					clientConnection.send(MetadataQueryC2SPacket.INSTANCE);
				}
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
		final var minecraft = MinecraftClient.getInstance();
		if (minecraft.getCurrentServerEntry() != null) {
			getRealTimeServerPing(minecraft.getCurrentServerEntry(), currentServerPing);
		} else if (minecraft.isIntegratedServerRunning()) {
			currentServerPing.setValue(1);
		}
	}

	/**
	 * @author Flowey
	 * @reason Implement bridge.
	 */
	@SuppressWarnings("deprecation")
	@Overwrite
	public static AxoSprite.Dynamic ipHud$getServerIcon() throws IOException {

		final var minecraft = MinecraftClient.getInstance();
		final var graphics = new GraphicsImpl(0, 0);
		final var serverEntry = minecraft.getCurrentServerEntry();
		Preconditions.checkState(serverEntry != null, "no server");

		graphics.setPixelData(Base64.getDecoder().decode(serverEntry.getFavicon()));
		final var img = NativeImage.read(Objects.requireNonNull(serverEntry.getFavicon()));
		final var icon = new NativeImageBackedTexture(img);
		final var iconId = Identifier.ofDefault(
			"servers/" + Hashing.sha1().hashUnencodedChars(minecraft.getCurrentServerEntry().address) + "/icon"
		);
		icon.upload();
		minecraft.getTextureManager().registerTexture(iconId, icon);

		class Impl implements AxoSprite.Dynamic, AxoSpriteImpl {
			@Override
			public void draw(MinecraftClient client, GuiGraphics stack, int sX, int sY, int sW, int sH) {
				client.getTextureManager().bindTexture(iconId);
				stack.drawTexture(iconId, sX, sY, 0, 0, sW, sH, 16, 16);
			}

			@Override
			public void close() {
				minecraft.getTextureManager().destroyTexture(iconId);
				icon.close();
			}
		}

		return new Impl();
	}
}
