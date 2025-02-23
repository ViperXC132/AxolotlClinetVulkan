package io.github.axolotlclient.bridge.mixin.internal;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.bridge.AxoMinecraftClient;
import io.github.axolotlclient.bridge.BridgeVersion;
import io.github.axolotlclient.AxolotlClientConfigCommon;
import io.github.axolotlclient.bridge.impl.AxoKeyImpl;
import io.github.axolotlclient.bridge.impl.Bridge;
import io.github.axolotlclient.bridge.internal.PlatformImplInternal;
import io.github.axolotlclient.bridge.item.AxoItem;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.item.AxoItems;
import io.github.axolotlclient.bridge.key.AxoKey;
import io.github.axolotlclient.bridge.key.AxoKeybinding;
import io.github.axolotlclient.bridge.render.AxoWindow;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.util.Util;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.locale.I18n;
import net.minecraft.resource.Identifier;
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
		return Util.getWindow();
	}

	/**
	 * @author Flowey
	 * @reason Implement bridge platform.
	 */
	@Overwrite
	public static AxoMinecraftClient getMinecraftClientInstance() {
		return Minecraft.getInstance();
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
		if (count == 0 || item == AxoItems.AIR) {
			return new ItemStack(Item.byBlock(Blocks.STONE), 0);
		}

		return new ItemStack((Item) item, count);
	}

	/**
	 * @author Flowey
	 * @reason Implement bridge platform.
	 */
	@Overwrite
	public static long getMeasuringTimeMs() {
		return Minecraft.getTime();
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
		return Minecraft.getCurrentFps();
	}

	/**
	 * @author Flowey
	 * @reason Implement bridge platform.
	 */
	@Overwrite
	public static AxoKeybinding createKeyBinding(AxoKey defaultKey, String name, String category) {
		final var binding = new KeyBinding(name, ((AxoKeyImpl) defaultKey).id(), category);
		Bridge.addKeybind(binding);
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
}
