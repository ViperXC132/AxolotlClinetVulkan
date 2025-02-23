package io.github.axolotlclient.bridge.mixin.entity;

import io.github.axolotlclient.bridge.entity.AxoPlayer;
import io.github.axolotlclient.bridge.item.AxoPlayerInventory;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntity.class)
@Implements({
	@Interface(iface = AxoPlayer.class, prefix = "bridge$")
})
public class PlayerEntityMixin {
	@Shadow
	public PlayerInventory inventory;

	public AxoPlayerInventory bridge$getInventory() {
		return this.inventory;
	}
}
