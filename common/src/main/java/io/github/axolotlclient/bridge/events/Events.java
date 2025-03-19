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
