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

import java.util.Collections;
import java.util.List;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.api.requests.UserRequest;
import io.github.axolotlclient.api.util.UUIDHelper;
import io.github.axolotlclient.modules.hypixel.LevelHead;
import io.github.axolotlclient.modules.hypixel.NickHider;
import io.github.axolotlclient.modules.hypixel.bedwars.BedwarsGame;
import io.github.axolotlclient.modules.hypixel.bedwars.BedwarsMod;
import io.github.axolotlclient.modules.hypixel.bedwars.BedwarsPlayer;
import io.github.axolotlclient.modules.tablist.Tablist;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

	@Shadow
	private Text header;
	@Shadow
	private Text footer;

	@Shadow
	@Final
	private MinecraftClient client;

	@WrapMethod(method = "getPlayerName")
	private Text nickHider(PlayerListEntry entry, Operation<Text> original) {
		var orig = original.call(entry);
		if (client.player == null) {
			return orig;
		}
		if (entry.getProfile().equals(client.player.getGameProfile()) && NickHider.getInstance().hideOwnName.get()) {
			return (Text) NickHider.getInstance().editComponent(orig, entry.getProfile().getName(), NickHider.getInstance().hiddenNameSelf.get());
		} else if (!entry.getProfile().equals(client.player.getGameProfile()) &&
			NickHider.getInstance().hideOtherNames.get()) {
			return (Text) NickHider.getInstance().editComponent(orig, entry.getProfile().getName(), NickHider.getInstance().hiddenNameOthers.get());
		}
		return orig;
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I", ordinal = 0))
	private int axolotlclient$moveName(TextRenderer instance, StringVisitable text, Operation<Integer> original, @Local PlayerListEntry entry) {
		var width = original.call(instance, text);
		if (AxolotlClient.config().showBadges.get() && UserRequest.getOnline(entry.getProfile().getId().toString())) {
			width += 10;
		}
		if (Tablist.getInstance().numericalPing.get())
			width += (instance.getWidth(String.valueOf(entry.getLatency())) - 10);
		if (BedwarsMod.getInstance().isEnabled() && BedwarsMod.getInstance().isWaiting()) {
			if (!entry.getProfile().getName().contains(Formatting.OBFUSCATED.toString())) {
				try {
					String uuid = UUIDHelper.toUndashed(entry.getProfile().getId());
					String render = LevelHead.getDisplayString(LevelHead.Mode.BEDWARS, uuid);
					width += instance.getWidth(render) + 2;
				} catch (Exception ignored) {
				}
			}
		}
		return width;
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
	public int axolotlclient$moveName2(TextRenderer instance, MatrixStack matrices, Text text, float x, float y, int color, @Local PlayerListEntry entry) {
		if (AxolotlClient.config().showBadges.get() && UserRequest.getOnline(entry.getProfile().getId().toString())) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(AxolotlClient.badgeIcon);
			RenderSystem.color4f(1, 1, 1, 1);
			DrawableHelper.drawTexture(matrices, (int) x, (int) y, 8, 8, 0, 0, 8, 8, 8, 8);
			x += 9;
		}
		return instance.drawWithShadow(matrices, text, x, y, color);
	}

	@Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
	private void axolotlclient$numericalPing(MatrixStack matrices, int width, int x, int y, PlayerListEntry entry, CallbackInfo ci) {
		if (BedwarsMod.getInstance().isEnabled() && BedwarsMod.getInstance().blockLatencyIcon() && (BedwarsMod.getInstance().isWaiting() || BedwarsMod.getInstance().inGame())) {
			ci.cancel();
		} else if (Tablist.getInstance().renderNumericPing(matrices, width, x, y, entry)) {
			ci.cancel();
		}
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isInSingleplayer()Z"))
	private boolean showPlayerHeads$1(MinecraftClient instance) {
		if (Tablist.getInstance().showPlayerHeads.get()) {
			return instance.isInSingleplayer();
		}
		return false;
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;isEncrypted()Z"))
	private boolean axolotlclient$showPlayerHeads$1(ClientConnection instance) {
		if (Tablist.getInstance().showPlayerHeads.get()) {
			return instance.isEncrypted();
		}
		return false;
	}

	@Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;header:Lnet/minecraft/text/Text;"))
	private void axolotlclient$setRenderHeaderFooter(MatrixStack matrices, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
		if (!Tablist.getInstance().showHeader.get()) {
			header = null;
		}
		if (!Tablist.getInstance().showFooter.get()) {
			footer = null;
		}
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isPartVisible(Lnet/minecraft/client/render/entity/PlayerModelPart;)Z", ordinal = 1))
	private boolean axolotlclient$alwaysShowHeadLayer(PlayerEntity instance, PlayerModelPart modelPart) {
		return Tablist.getInstance().alwaysShowHeadLayer.get() || instance.isPartVisible(modelPart);
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;renderLatencyIcon(Lnet/minecraft/client/util/math/MatrixStack;IIILnet/minecraft/client/network/PlayerListEntry;)V"))
	private void axolotlclient$renderWithoutObjective(PlayerListHud instance, MatrixStack matrixStack, int width, int x, int y, PlayerListEntry playerListEntry, Operation<Void> original) {
		if (!BedwarsMod.getInstance().isEnabled() || !BedwarsMod.getInstance().isWaiting()) {
			original.call(instance, matrixStack, width, x, y, playerListEntry);
			return;
		}
		int endX = x + width - 1;
		String render;
		try {
			if (playerListEntry.getProfile().getName().contains(Formatting.OBFUSCATED.toString())) {
				original.call(instance, matrixStack, width, x, y, playerListEntry);
				return;
			}

			String uuid = UUIDHelper.toUndashed(playerListEntry.getProfile().getId());
			render = LevelHead.getDisplayString(LevelHead.Mode.BEDWARS, uuid);
		} catch (Exception e) {
			original.call(instance, matrixStack, width, x, y, playerListEntry);
			return;
		}
		DrawableHelper.drawStringWithShadow(matrixStack,
			client.textRenderer,
			render,
			(endX - this.client.textRenderer.getWidth(render)) + 20,
			y,
			-1
		);
	}

	@Inject(
		method = "renderScoreboardObjective",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I",
			ordinal = 1
		),
		cancellable = true
	)
	private void axolotlclient$renderCustomScoreboardObjective(
		ScoreboardObjective objective, int y, String player, int startX, int endX,
		PlayerListEntry playerEntry, MatrixStack matrices, CallbackInfo ci
	) {
		if (!BedwarsMod.getInstance().isEnabled()) {
			return;
		}

		BedwarsGame game = BedwarsMod.getInstance().getGame().orElse(null);
		if (game == null) {
			return;
		}

		game.renderCustomScoreboardObjective(matrices, playerEntry.getProfile().getName(), objective.getScoreboard().getPlayerScore(player, objective).getScore(), y, endX);

		ci.cancel();
	}

	@ModifyVariable(
		method = "render",
		at = @At(
			value = "STORE"
		),
		ordinal = 7
	)
	public int axolotlclient$changeWidth(int value) {
		if (BedwarsMod.getInstance().isEnabled() && BedwarsMod.getInstance().blockLatencyIcon() && (BedwarsMod.getInstance().isWaiting() || BedwarsMod.getInstance().inGame())) {
			value -= 9;
		}
		if (BedwarsMod.getInstance().isEnabled() && BedwarsMod.getInstance().isWaiting()) {
			value += 20;
		}
		return value;
	}

	@Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
	public void axolotlclient$getPlayerName(PlayerListEntry playerEntry, CallbackInfoReturnable<Text> cir) {
		if (!BedwarsMod.getInstance().isEnabled()) {
			return;
		}
		BedwarsGame game = BedwarsMod.getInstance().getGame().orElse(null);
		if (game == null || !game.isStarted()) {
			return;
		}
		BedwarsPlayer player = game.getPlayer(playerEntry.getProfile().getName()).orElse(null);
		if (player == null) {
			return;
		}
		cir.setReturnValue(Text.of(player.getTabListDisplay()));
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@ModifyVariable(method = "render", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/Ordering;sortedCopy(Ljava/lang/Iterable;)Ljava/util/List;", remap = false))
	public List<PlayerListEntry> axolotlclient$overrideSortedPlayers(List<PlayerListEntry> original) {
		if (!BedwarsMod.getInstance().inGame()) {
			return original;
		}
		List<?> players = BedwarsMod.getInstance().getGame().orElseThrow().getTabPlayerList(Collections.unmodifiableList(original));
		if (players == null) {
			return original;
		}
		return (List) players;
	}

	@Inject(method = "setHeader", at = @At("HEAD"), cancellable = true)
	public void axolotlclient$changeHeader(Text header, CallbackInfo ci) {
		if (!BedwarsMod.getInstance().inGame()) {
			return;
		}
		this.header = (Text) BedwarsMod.getInstance().getGame().orElseThrow().getTopBarText();
		ci.cancel();
	}

	@Inject(method = "setFooter", at = @At("HEAD"), cancellable = true)
	public void axolotlclient$changeFooter(Text footer, CallbackInfo ci) {
		if (!BedwarsMod.getInstance().inGame()) {
			return;
		}
		this.footer = (Text) BedwarsMod.getInstance().getGame().orElseThrow().getBottomBarText();
		ci.cancel();
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/GameOptions;getTextBackgroundColor(I)I")))
	private void modifyBackground(MatrixStack stack, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
		var tablist = Tablist.getInstance();
		if (!tablist.backgroundEnabled.get()) {
			return;
		}
		if (tablist.customBackgroundColor.get()) {
			original.call(stack, x1, y1, x2, y2, tablist.backgroundColor.get().toInt());
			return;
		}
		original.call(stack, x1, y1, x2, y2, color);
	}

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;renderLatencyIcon(Lnet/minecraft/client/util/math/MatrixStack;IIILnet/minecraft/client/network/PlayerListEntry;)V")))
	private void modifyBackground$2(MatrixStack stack, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
		modifyBackground(stack, x1, y1, x2, y2, color, original);
	}
}
