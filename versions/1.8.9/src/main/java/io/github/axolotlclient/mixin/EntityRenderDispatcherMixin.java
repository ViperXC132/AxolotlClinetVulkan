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

package io.github.axolotlclient.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.modules.freelook.Freelook;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

	@WrapOperation(method = "prepare", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;yaw:F", opcode = Opcodes.GETFIELD))
	public float axolotlclient$freelook$yaw(Entity instance, Operation<Float> original) {
		return Freelook.getInstance().yaw(original.call(instance));
	}

	@WrapOperation(method = "prepare", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;lastYaw:F", opcode = Opcodes.GETFIELD))
	public float axolotlclient$freelook$prevYaw(Entity instance, Operation<Float> original) {
		return Freelook.getInstance().yaw(original.call(instance));
	}

	@WrapOperation(method = "prepare", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;pitch:F", opcode = Opcodes.GETFIELD))
	public float axolotlclient$freelook$pitch(Entity instance, Operation<Float> original) {
		return Freelook.getInstance().pitch(original.call(instance));
	}

	@WrapOperation(method = "prepare", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;lastPitch:F", opcode = Opcodes.GETFIELD))
	public float axolotlclient$freelook$prevPitch(Entity instance, Operation<Float> original) {
		return Freelook.getInstance().pitch(original.call(instance));
	}
}
