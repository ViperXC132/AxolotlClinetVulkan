package io.github.axolotlclient.bridge.mixin.key;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.bridge.impl.AxoKeyImpl;
import io.github.axolotlclient.bridge.key.AxoKey;
import io.github.axolotlclient.bridge.key.AxoKeybinding;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * An abstract representation of a keybind
 */
@Mixin(KeyBinding.class)
@Implements({
	@Interface(iface = AxoKeybinding.class, prefix = "bridge$")
})
public abstract class KeyBindingMixin {
	@Shadow
	public abstract boolean isPressed();

	@Shadow
	private int keyCode;
	@Unique
	private final List<Runnable> axolotlclient$bridge$onClicked = new ArrayList<>();

	@Unique
	private final List<Runnable> axolotlclient$bridge$onReleased = new ArrayList<>();

	@Inject(method = "set", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/KeyBinding;pressed:Z"))
	private static void dispatchHandlers(int i, boolean bl, CallbackInfo ci, @Local KeyBinding binding) {
		if (bl) {
			((KeyBindingMixin) (Object) binding).axolotlclient$bridge$onClicked.forEach(Runnable::run);
		} else {
			((KeyBindingMixin) (Object) binding).axolotlclient$bridge$onReleased.forEach(Runnable::run);
		}
	}

	public void bridge$registerOnClicked(Runnable runnable) {
		axolotlclient$bridge$onClicked.add(runnable);
	}

	public void bridge$registerOnReleased(Runnable runnable) {
		axolotlclient$bridge$onReleased.add(runnable);
	}

	@Intrinsic
	public boolean bridge$isPressed() {
		return isPressed();
	}

	public AxoKey bridge$getBoundKey() {
		return AxoKeyImpl.get(keyCode);
	}
}
