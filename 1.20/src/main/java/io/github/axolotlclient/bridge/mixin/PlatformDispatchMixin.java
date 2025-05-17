package io.github.axolotlclient.bridge.mixin;

import io.github.axolotlclient.bridge.PlatformDispatch;
import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.util.ThreadExecuter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.MetadataQueryC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.ServerMetadataS2CPacket;
import net.minecraft.text.Text;
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
					final ClientConnection clientConnection = ClientConnection.connect(optional.get(), false);
					ClientQueryPacketListener listener = new ClientQueryPacketListener() {

						private long currentSystemTime = 0L;

						@Override
						public void onServerMetadata(ServerMetadataS2CPacket packet) {
							this.currentSystemTime = net.minecraft.util.Util.getMeasuringTimeMs();
							clientConnection.send(new QueryPingC2SPacket(this.currentSystemTime));
						}

						@Override
						public void onPong(QueryPongS2CPacket packet) {
							var time = this.currentSystemTime;
							var latency = net.minecraft.util.Util.getMeasuringTimeMs();
							currentServerPing.setValue((int) (latency - time));
							clientConnection.disconnect(Text.translatable("multiplayer.status.finished"));
						}

						@Override
						public void onDisconnected(Text reason) {
						}

						@Override
						public boolean isConnected() {
							return clientConnection.isOpen();
						}
					};
					clientConnection.send(new HandshakeC2SPacket(address.getAddress(), address.getPort(), NetworkState.STATUS));
					clientConnection.send(new MetadataQueryC2SPacket());
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
	@Overwrite
	public static AxoSprite.Dynamic ipHud$getServerIcon() {
		throw BridgeUtil.noImpl();
	}

	/**
	 * @author Flowey
	 * @reason Implement bridge.
	 */
	@Overwrite
	public static void playerHud$renderPlayer(AxoRenderContext graphics, float i) {
		throw BridgeUtil.noImpl();
	}
}
