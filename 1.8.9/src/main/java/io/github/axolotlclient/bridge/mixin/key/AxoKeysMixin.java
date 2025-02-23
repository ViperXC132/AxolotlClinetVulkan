package io.github.axolotlclient.bridge.mixin.key;

import io.github.axolotlclient.bridge.impl.AxoKeyImpl;
import io.github.axolotlclient.bridge.key.AxoKey;
import io.github.axolotlclient.bridge.key.AxoKeys;
import java.security.Key;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AxoKeys.class, remap = false)
public class AxoKeysMixin {

	@Mutable
	@Shadow
	@Final
	public static AxoKey KEY_I;

	@Mutable
	@Shadow
	@Final
	public static AxoKey KEY_K;

	@Mutable
	@Shadow
	@Final
	public static AxoKey MOUSE_LEFT;

	@Mutable
	@Shadow
	@Final
	public static AxoKey MOUSE_RIGHT;

	@Inject(method = "<clinit>", at = @At("HEAD"), cancellable = true)
	private static void setStaticValues(CallbackInfo ci) {
		KEY_I = AxoKeyImpl.get(Keyboard.KEY_I);
		KEY_K = AxoKeyImpl.get(Keyboard.KEY_K);
		MOUSE_LEFT = AxoKeyImpl.get(-100);
		MOUSE_RIGHT = AxoKeyImpl.get(-99);
		ci.cancel();
	}
}
