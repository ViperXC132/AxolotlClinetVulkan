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

package io.github.axolotlclient.mixin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.hypixel.LevelHead;
import io.github.axolotlclient.util.BadgeFeatureRenderer;
import io.github.axolotlclient.util.duck.SubmitNodeCollectorExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import net.minecraft.client.renderer.feature.phase.SimpleFeatureRenderPhase;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubmitNodeCollection.class)
public abstract class SubmitNodeCollectionMixin implements SubmitNodeCollectorExtension {

	@Shadow
	@Final
	public SimpleFeatureRenderPhase nameTags;

	@Shadow
	public abstract void submitCustomGeometry(PoseStack poseStack, RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer);

	@Unique
	private boolean lastNametagSubmitHasBadge, lastNameTagSubmitIsLevelHead;
	@Unique
	private final SimpleFeatureRenderPhase badges = new SimpleFeatureRenderPhase();

	@SuppressWarnings("unchecked")
	@WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;of([Ljava/lang/Object;)Ljava/util/List;"))
	private <E> List<E> addPhase(E[] elements, Operation<List<E>> original) {
		var list = new ArrayList<>(original.call((Object) elements));
		list.add((E) badges);
		return Collections.unmodifiableList(list);
	}

	@Override
	public SimpleFeatureRenderPhase axolotlclient$badgePhase() {
		return badges;
	}

	@Override
	public void axolotlclient$lastNameTagSubmitHasBadge() {
		lastNametagSubmitHasBadge = true;
	}

	@Override
	public void axolotlclient$lastNameTagSubmitIsLevelHead() {
		lastNameTagSubmitIsLevelHead = true;
	}

	@ModifyArg(method = "submitNameTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/NameTagFeatureRenderer$Submit;<init>(Lorg/joml/Matrix4fc;FFLnet/minecraft/network/chat/Component;IIILnet/minecraft/client/gui/Font$DisplayMode;)V"), index = 6)
	private int bgColor(int color) {
		if (AxolotlClient.config().nametagBackground.get()) {
			if (lastNameTagSubmitIsLevelHead && !LevelHead.getInstance().background.get()) {
				return 0;
			}
			return color;
		} else {
			return 0;
		}
	}

	@Inject(method = "submitNameTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/phase/TranslucentFeatureRenderPhase;submit(Lnet/minecraft/client/renderer/feature/submit/TranslucentSubmit;)V"))
	private void submitExtraNametags(PoseStack poseStack, Vec3 nameTagAttachment, int offset, Component name, boolean seeThrough, int lightCoords, CameraRenderState camera, CallbackInfo ci, @Local(name = "x") float nameStartX, @Local(name = "minecraft") Minecraft mc, @Local(name = "pose") Matrix4f pose) {
		if (lastNametagSubmitHasBadge) {
			if (AxolotlClient.config().customBadge.get()) {

				var badgeText = Component.literal(AxolotlClient.config().badgeText.get());
				var x = nameStartX - (mc.font.width(badgeText) + 4);
				nameTags.submit(new NameTagFeatureRenderer.Submit(pose, x, offset, badgeText, LightCoordsUtil.lightCoordsWithEmission(lightCoords, 2), -1, 0, Font.DisplayMode.NORMAL));
			} else {
				var x = nameStartX - 10;
				badges.submit(new BadgeFeatureRenderer.Submit(pose, x, offset));
			}
		}
	}

	@ModifyArg(method = "submitNameTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/NameTagFeatureRenderer$Submit;<init>(Lorg/joml/Matrix4fc;FFLnet/minecraft/network/chat/Component;IIILnet/minecraft/client/gui/Font$DisplayMode;)V"), index = 5)
	private int applyLevelHeadOptions(int color) {
		if (lastNameTagSubmitIsLevelHead) {
			color = ARGB.color(ARGB.alpha(color), LevelHead.getInstance().textColor.get().toInt());
		}
		return color;
	}
}
