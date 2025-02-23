package io.github.axolotlclient.bridge.mixin.item;

import io.github.axolotlclient.bridge.item.AxoEnchant;
import io.github.axolotlclient.bridge.item.AxoEnchants;
import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AxoEnchants.class, remap = false)
public class AxoEnchantsMixin {
	@Mutable
	@Shadow
	@Final
	public static AxoEnchant PROTECTION;

	@Inject(method = "<clinit>", at = @At("HEAD"), cancellable = true)
	private static void setStaticValues(CallbackInfo info) {
		PROTECTION = Enchantment.PROTECTION;
		info.cancel();
	}
}
