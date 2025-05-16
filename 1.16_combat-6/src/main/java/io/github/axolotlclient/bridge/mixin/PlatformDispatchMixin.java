package io.github.axolotlclient.bridge.mixin;

import io.github.axolotlclient.bridge.PlatformDispatch;
import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.util.ThreadExecuter;
import java.net.InetAddress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
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
				ServerAddress address = ServerAddress.parse(server.address);

				final ClientConnection manager = ClientConnection.connect(InetAddress.getByName(address.getAddress()), address.getPort(), false);
				manager.setPacketListener(new ClientQueryPacketListener() {
					private long currentSystemTime = 0L;

					@Override
					public void onResponse(QueryResponseS2CPacket packet) {
						this.currentSystemTime = net.minecraft.util.Util.getMeasuringTimeMs();
						manager.send(new QueryPingC2SPacket(this.currentSystemTime));
					}

					@Override
					public void onPong(QueryPongS2CPacket packet) {
						long time = this.currentSystemTime;
						long latency = net.minecraft.util.Util.getMeasuringTimeMs();
						currentServerPing.setValue((int) (latency - time));
						manager.disconnect(Text.of(""));
					}

					@Override
					public void onDisconnected(Text reason) {
					}

					@Override
					public ClientConnection getConnection() {
						return manager;
					}
				});
				manager.send(new HandshakeC2SPacket(address.getAddress(), address.getPort(), NetworkState.STATUS));
				manager.send(new QueryRequestC2SPacket());

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
