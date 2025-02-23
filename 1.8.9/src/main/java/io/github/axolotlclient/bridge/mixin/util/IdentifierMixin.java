package io.github.axolotlclient.bridge.mixin.util;

import io.github.axolotlclient.bridge.util.AxoIdentifier;
import net.minecraft.resource.Identifier;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Identifier.class)
@Implements(
	@Interface(iface = AxoIdentifier.class, prefix = "bridge$")
)
public abstract class IdentifierMixin {
	@Shadow
	public abstract String getPath();

	// don't overwrite the normal method, and don't displace since this method trivial.
	@Intrinsic
	public String bridge$getPath() {
		return getPath();
	}
}
