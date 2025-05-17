package io.github.axolotlclient.bridge.mixin.entity.effect;

import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffect;
import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffects;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AxoStatusEffects.class, remap = false)
public class AxoStatusEffectsMixin {
	@Mutable
	@Shadow
	@Final
	public static AxoStatusEffect JUMP_BOOST;

	@Mutable
	@Shadow
	@Final
	public static AxoStatusEffect SPEED;

	@Mutable
	@Shadow
	@Final
	public static AxoStatusEffect HASTE;

	@Inject(method = "<clinit>", at = @At("HEAD"), cancellable = true)
	private static void setStaticValues(CallbackInfo info) {
		JUMP_BOOST = StatusEffects.JUMP_BOOST;
		SPEED = StatusEffects.SPEED;
		HASTE = StatusEffects.HASTE;

		info.cancel();
	}
}
