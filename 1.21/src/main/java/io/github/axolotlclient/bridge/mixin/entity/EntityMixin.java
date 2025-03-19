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

package io.github.axolotlclient.bridge.mixin.entity;

import io.github.axolotlclient.bridge.entity.AxoEntity;
import io.github.axolotlclient.bridge.math.Vec3;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin implements AxoEntity {
	@Shadow
	@Nullable
	private Entity vehicle;

	@Shadow
	private boolean onGround;

	@Shadow
	public abstract Vec3d getRotationVec(float tickDelta);

	@Shadow
	private float yaw;

	@Shadow
	public abstract UUID getUuid();

	@Shadow
	public abstract Vec3d getPos();

	@Shadow
	public abstract Vec3d getVelocity();

	@Override
	public @Nullable AxoEntity br$getVehicle() {
		return vehicle;
	}

	@Override
	public Vec3 br$getPos() {
		return new Vec3(getPos().x, getPos().y, getPos().z);
	}

	@Override
	public Vec3 br$getVelocity() {
		return new Vec3(getVelocity().x, getVelocity().y, getVelocity().z);
	}

	@Override
	public boolean br$isOnGround() {
		return onGround;
	}

	@Override
	public float br$getYaw() {
		return yaw;
	}

	@Override
	public Vec3 br$getRotation(float deltaTick) {
		final var vec = getRotationVec(deltaTick);
		return new Vec3(vec.x, vec.y, vec.z);
	}

	@Override
	public UUID br$getUuid() {
		return getUuid();
	}
}
