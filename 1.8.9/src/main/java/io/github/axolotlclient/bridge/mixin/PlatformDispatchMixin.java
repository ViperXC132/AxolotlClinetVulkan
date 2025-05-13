package io.github.axolotlclient.bridge.mixin;

import io.github.axolotlclient.bridge.PlatformDispatch;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.HudManagerCommon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = PlatformDispatch.class, remap = false)
public class PlatformDispatchMixin {
	/**
	 * @author Flowey
	 * @reason Implement bridge.
	 */
	@Overwrite
	public static HudManagerCommon hudManager$getInstance() {
		return HudManager.getInstance();
	}
}
