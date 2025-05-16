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

package io.github.axolotlclient.bridge.entity;

import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffectInstance;
import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.math.Vec3;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public interface AxoEntity {
	@Nullable
	default AxoEntity br$getVehicle() {
		throw BridgeUtil.noImpl();
	}

	default Vec3 br$getPos() {
		throw BridgeUtil.noImpl();
	}

	default Vec3 br$getVelocity() {
		throw BridgeUtil.noImpl();
	}

	default boolean br$isOnGround() {
		throw BridgeUtil.noImpl();
	}

	default float br$getYaw() {
		throw BridgeUtil.noImpl();
	}

	default float br$getPitch() {
		throw BridgeUtil.noImpl();
	}

	default int br$getNetId() {
		throw BridgeUtil.noImpl();
	}

	default Vec3 br$getRotation(float deltaTick) {
		throw BridgeUtil.noImpl();
	}

	default UUID br$getUuid() {
		throw BridgeUtil.noImpl();
	}

	default List<AxoStatusEffectInstance> br$getStatusEffects() {
		throw BridgeUtil.noImpl();
	}
}
