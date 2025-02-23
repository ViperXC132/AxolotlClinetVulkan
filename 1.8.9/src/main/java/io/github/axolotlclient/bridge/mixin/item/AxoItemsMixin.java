package io.github.axolotlclient.bridge.mixin.item;

import io.github.axolotlclient.bridge.impl.AirItemImpl;
import io.github.axolotlclient.bridge.item.AxoItem;
import io.github.axolotlclient.bridge.item.AxoItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AxoItems.class, remap = false)
public class AxoItemsMixin {
	@Mutable
	@Shadow
	@Final
	public static AxoItem AIR;

	@Mutable
	@Shadow
	@Final
	public static AxoItem IRON_HELMET;

	@Mutable
	@Shadow
	@Final
	public static AxoItem IRON_LEGGINGS;

	@Mutable
	@Shadow
	@Final
	public static AxoItem IRON_CHESTPLATE;

	@Mutable
	@Shadow
	@Final
	public static AxoItem IRON_BOOTS;

	@Mutable
	@Shadow
	@Final
	public static AxoItem IRON_SWORD;

	@Mutable
	@Shadow
	@Final
	public static AxoItem ARROW;

	@Inject(method = "<clinit>", at = @At("HEAD"), cancellable = true)
	private static void setStaticValues(CallbackInfo info) {
		AIR = AirItemImpl.getInstance();
		IRON_HELMET = AxoItems.IRON_HELMET;
		IRON_CHESTPLATE = AxoItems.IRON_CHESTPLATE;
		IRON_LEGGINGS = AxoItems.IRON_LEGGINGS;
		IRON_BOOTS = AxoItems.IRON_BOOTS;
		IRON_SWORD = AxoItems.IRON_SWORD;
		ARROW = AxoItems.ARROW;
		info.cancel();
	}
}
