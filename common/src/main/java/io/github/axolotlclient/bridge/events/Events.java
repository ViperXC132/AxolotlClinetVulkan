package io.github.axolotlclient.bridge.events;

import io.github.axolotlclient.bridge.entity.AxoEntity;
import io.github.axolotlclient.bridge.entity.AxoPlayer;
import io.github.axolotlclient.bridge.key.AxoKey;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

/**
 * Events...
 */
public class Events {
	public static final EventBus<BiConsumer<AxoPlayer, AxoEntity>> PLAYER_ATTACK = EventBus.broadcast2();
	public static final EventBus<BiConsumer<AxoPlayer, @Nullable AxoEntity>> PLAYER_HURT = EventBus.broadcast2();
	public static final EventBus<Consumer<Long>> UPDATE_TIME = EventBus.broadcast1();
	public static final EventBus<Consumer<AxoKey>> KEY_INPUT = EventBus.broadcast1();
	public static final EventBus<Runnable> CLIENT_START = EventBus.broadcast0();
	public static final EventBus<Runnable> CLIENT_STOP = EventBus.broadcast0();
	public static final EventBus<Runnable> TICK = EventBus.broadcast0();
}
