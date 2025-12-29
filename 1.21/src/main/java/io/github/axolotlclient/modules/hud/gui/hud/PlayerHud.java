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

package io.github.axolotlclient.modules.hud.gui.hud;

import io.github.axolotlclient.bridge.events.Events;
import io.github.axolotlclient.bridge.events.types.PlayerDirectionChangeEvent;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Axis;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * <p>License: GPL-3.0</p>
 */

public class PlayerHud extends PlayerHudCommon {
	@Getter
	private static boolean currentlyRendering;

	public PlayerHud() {
		super();
		Events.PLAYER_DIRECTION_CHANGE.register(this::onPlayerDirectionChange);
	}

	public void onPlayerDirectionChange(PlayerDirectionChangeEvent event) {
		yawOffset += (event.yaw() - event.prevYaw()) / 2;
	}

	@Override
	public void tick() {
		super.tick();
		var client = MinecraftClient.getInstance();
		if (client.player != null && client.player.isInSwimmingPose()) {
			float rawPitch = client.player.isTouchingWater() ? -90.0F - client.player.getPitch() : -90.0F;
			float pitch = MathHelper.lerp(client.player.getLeaningPitch(1), 0.0F, rawPitch);
			float height = client.player.getHeight();
			// sin = opposite / hypotenuse
			float offset = (float) (Math.sin(Math.toRadians(pitch)) * height);
			yOffset = Math.abs(offset) - 30;
		} else if (client.player != null && client.player.isFallFlying()) {
			// Elytra!

			float j = (float) client.player.getRoll() + 1;
			float k = MathHelper.clamp(j * j / 100.0F, 0.0F, 1.0F);

			float pitch = k * (-90.0F - client.player.getPitch()) + 90;
			float height = client.player.getHeight();
			// sin = opposite / hypotenuse
			yOffset = (float) (Math.sin(Math.toRadians(pitch)) * height) - getContentHeight()/4f;
			if (pitch < 0) {
				yOffset -= (float) (((1 / (1 + Math.exp(-pitch / 4))) - .5) * 20);
			}
		} else {
			yOffset *= .8f;
		}
	}

	@Override
	protected void renderPlayer(AxoRenderContext ctx, boolean placeholder, double x, double y, float delta) {
		var client = MinecraftClient.getInstance();
		var graphics = (GuiGraphics) ctx;
		if (client.player == null) {
			return;
		}

		if (!placeholder && autoHide.get()) {
			if (isPerformingAction()) {
				hide = -1;
			} else if (hide == -1) {
				hide = System.currentTimeMillis();
			}

			if (hide != -1 && System.currentTimeMillis() - hide > 500) {
				return;
			}
		}

		float lerpY = (lastYOffset + ((yOffset - lastYOffset) * delta));

		Quaternionf quaternion = Axis.Z_POSITIVE.rotationDegrees(180.0F);

		// Rotate to whatever is wanted. Also make sure to offset the yaw
		float deltaYaw = client.player.getYaw(delta);
		if (dynamicRotation.get()) {
			deltaYaw -= (lastYawOffset + ((yawOffset - lastYawOffset) * delta));
		}
		Quaternionf quaternionf2 = new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 1, 0), deltaYaw - 180 + rotation.get().floatValue());
		quaternion.mul(quaternionf2);

		// Save these to set them back later
		float pastYaw = client.player.getYaw();
		float pastPrevYaw = client.player.prevYaw;
		currentlyRendering = true;
		InventoryScreen.drawEntity(graphics,
			((float) (x/getScale() + getContentWidth() / 2f)),
			((float) (y/getScale() + getContentHeight() * client.player.getHeight() / 2f - lerpY)),
			40, new Vector3f(), quaternion, quaternionf2, client.player);
		currentlyRendering = false;

		client.player.setYaw(pastYaw);
		client.player.prevYaw = pastPrevYaw;
	}

	private boolean isPerformingAction() {
		// inspired by tr7zw's mod
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		//noinspection DataFlowIssue
		return player.isSneaking() || player.isSprinting() || player.isFallFlying() || player.getAbilities().flying
			|| player.isSubmergedInWater() || player.isInSwimmingPose() || player.hasVehicle()
			|| player.isUsingItem() || player.handSwinging || player.hurtTime > 0 || player.isOnFire();
	}
}
