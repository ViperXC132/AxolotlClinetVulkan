package io.github.axolotlclient.bridge.mixin.key;

import io.github.axolotlclient.bridge.key.AxoClientKeybinds;
import io.github.axolotlclient.bridge.key.AxoKeybinding;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameOptions.class)
@Implements({
	@Interface(iface = AxoClientKeybinds.class, prefix = "bridge$")
})
public class GameOptionsMixin {
	@Shadow
	public KeyBinding sprintKey;

	@Shadow
	public KeyBinding sneakKey;

	@Shadow
	public KeyBinding attackKey;

	@Shadow
	public KeyBinding useKey;

	public AxoKeybinding bridge$getSprintKeybind() {
		return sprintKey;
	}

	public AxoKeybinding bridge$getSneakKeybind() {
		return sneakKey;
	}

	public AxoKeybinding bridge$getAttackKey() {
		return attackKey;
	}

	public AxoKeybinding bridge$getUseKey() {
		return useKey;
	}
}
