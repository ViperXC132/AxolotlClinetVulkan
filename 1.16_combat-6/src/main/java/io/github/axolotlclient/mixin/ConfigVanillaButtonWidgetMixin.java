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

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.VanillaButtonWidget;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.util.ButtonWidgetTextures;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VanillaButtonWidget.class)
public abstract class ConfigVanillaButtonWidgetMixin extends ButtonWidget {
	@Shadow(remap = false)
	public abstract int getX();

	@Shadow(remap = false)
	public abstract int getY();

	private ConfigVanillaButtonWidgetMixin(int x, int y, int width, int height, Text message, PressAction action) {
		super(x, y, width, height, message, action);
	}

	@Redirect(method = "renderButton", at = @At(value = "INVOKE", target = "Lio/github/axolotlclient/AxolotlClientConfig/impl/ui/vanilla/widgets/VanillaButtonWidget;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0))
	private void drawTexture$1(VanillaButtonWidget instance, MatrixStack stack, int x, int y, int u, int v, int width, int height) {

	}

	@Redirect(method = "renderButton", at = @At(value = "INVOKE", target = "Lio/github/axolotlclient/AxolotlClientConfig/impl/ui/vanilla/widgets/VanillaButtonWidget;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 1))
	private void drawTexture$2$replaceWithNineSlice(VanillaButtonWidget instance, MatrixStack stack, int x, int y, int u, int v, int width, int height) {
		Identifier tex = ButtonWidgetTextures.get(getYImage(hovered));
		DrawUtil.blitSprite(tex, getX(), getY(), getWidth(), getHeight(), new DrawUtil.NineSlice(200, 20, 3));
		MinecraftClient.getInstance().getTextureManager().bindTexture(WIDGETS_LOCATION);
	}
}
