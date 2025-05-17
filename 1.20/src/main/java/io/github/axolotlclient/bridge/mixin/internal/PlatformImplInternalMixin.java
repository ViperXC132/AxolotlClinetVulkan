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

package io.github.axolotlclient.bridge.mixin.internal;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.AxolotlClientConfigCommon;
import io.github.axolotlclient.bridge.AxoMinecraftClient;
import io.github.axolotlclient.bridge.BridgeVersion;
import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffect;
import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffectInstance;
import io.github.axolotlclient.bridge.impl.AxoSpriteImpl;
import io.github.axolotlclient.bridge.internal.PlatformImplInternal;
import io.github.axolotlclient.bridge.item.AxoItem;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.key.AxoKey;
import io.github.axolotlclient.bridge.key.AxoKeybinding;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.bridge.render.AxoWindow;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.mixin.MinecraftClientAccessor;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = PlatformImplInternal.class, remap = false)
public class PlatformImplInternalMixin {
    /**
     * @author Flowey
     * @reason Implement bridge platform.
     */
    @Overwrite
    public static @Nullable AxoWindow getWindow() {
        return MinecraftClient.getInstance().getWindow();
    }

    /**
     * @author Flowey
     * @reason Implement bridge platform.
     */
    @Overwrite
    public static AxoMinecraftClient getMinecraftClientInstance() {
        return MinecraftClient.getInstance();
    }

    /**
     * @author Flowey
     * @reason Implement bridge platform.
     */
    @Overwrite
    public static String getTranslatedString(String nameKey, Object[] args) {
        return I18n.translate(nameKey, args);
    }

    /**
     * @author Flowey
     * @reason Implement bridge platform.
     */
    @Overwrite
    public static BridgeVersion getBridgeApiVersion() {
        return BridgeVersion.V1_8;
    }


    /**
     * @author Flowey
     * @reason Implement bridge platform.
     */
    @Overwrite
    public static AxoItemStack createItemStack(AxoItem item, int count) {
        return new ItemStack((Item) item, count);
    }

    /**
     * @author Flowey
     * @reason Implement bridge platform.
     */
    @Overwrite
    public static long getMeasuringTimeMs() {
        return Util.getMeasuringTimeMs();
    }

    /**
     * @author Flowey
     * @reason Implement bridge platform.
     */
    @Overwrite
    public static AxolotlClientConfigCommon getConfig() {
        return AxolotlClient.config();
    }

    /**
     * @author Flowey
     * @reason Implement bridge platform.
     */
    @Overwrite
    public static int getCurrentFps() {
        return MinecraftClientAccessor.axolotlclient$getCurrentFps();
    }

    /**
     * @author Flowey
     * @reason Implement bridge platform.
     */
    @Overwrite
    public static AxoKeybinding createKeyBinding(AxoKey defaultKey, String name, String category) {
		int code = defaultKey == null ? 0 : ((InputUtil.Key) defaultKey).getKeyCode();
        final var binding = new KeyBind(name, code, category);
        KeyBindingHelper.registerKeyBinding(binding);
        return binding;
    }

    /**
     * @author Flowey
     * @reason Implement bridge platform.
     */
    @Overwrite
    public static AxoIdentifier createIdentifier(String ns, String path) {
        return new Identifier(ns, path);
    }

	/**
	 * @author Flowey
	 * @reason Implement bridge platform.
	 */
	@Overwrite
	public static AxoStatusEffectInstance createStatusEffectInstance(AxoStatusEffect effect, int duration) {
		return new StatusEffectInstance((StatusEffect) effect, duration);
	}

	/**
	 * @author Flowey
	 * @reason Implement bridge platform.
	 */
	@Overwrite
	public static AxoSprite getTexture(GraphicsOption option) {
		return new AxoSpriteImpl.Config(option);
	}
}
