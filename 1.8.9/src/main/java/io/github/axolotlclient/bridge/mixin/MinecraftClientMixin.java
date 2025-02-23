package io.github.axolotlclient.bridge.mixin;

import io.github.axolotlclient.bridge.AxoMinecraftClient;
import io.github.axolotlclient.bridge.entity.AxoPlayer;
import io.github.axolotlclient.bridge.key.AxoClientKeybinds;
import io.github.axolotlclient.bridge.render.AxoTextRenderer;
import io.github.axolotlclient.bridge.world.AxoWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Minecraft.class)
@Implements({
	@Interface(iface = AxoMinecraftClient.class, prefix = "bridge$")
})
public class MinecraftClientMixin {
	@Shadow
	public TextRenderer textRenderer;

	@Shadow
	public LocalClientPlayerEntity player;

	@Shadow
	public ClientWorld world;

	@Shadow
	public GameOptions options;

	@Nullable
	public AxoPlayer bridge$getPlayer() {
		return player;
	}

	public AxoWorld bridge$getWorld() {
		return world;
	}

	public AxoTextRenderer bridge$getTextRenderer() {
		return textRenderer;
	}

	public AxoClientKeybinds bridge$getKeybinds() {
		return options;
	}
}
