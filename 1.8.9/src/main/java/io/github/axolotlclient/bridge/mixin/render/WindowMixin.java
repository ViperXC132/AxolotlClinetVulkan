package io.github.axolotlclient.bridge.mixin.render;

import io.github.axolotlclient.bridge.render.AxoWindow;
import net.minecraft.client.render.Window;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Window.class)
@Implements(
	@Interface(iface = AxoWindow.class, prefix = "bridge$")
)
public abstract class WindowMixin {
	@Shadow
	public abstract double getScaledWidth();

	@Shadow
	public abstract double getScaledHeight();

	// don't overwrite the normal method, and don't displace since this method trivial.
	@Intrinsic
	public double bridge$getScaledWidth() {
		return getScaledWidth();
	}

	// don't overwrite the normal method, and don't displace since this method trivial.
	@Intrinsic
	public double  bridge$getScaledHeight() {
		return getScaledHeight();
	}
}
