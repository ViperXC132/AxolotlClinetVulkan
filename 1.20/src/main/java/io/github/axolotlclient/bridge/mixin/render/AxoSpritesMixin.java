package io.github.axolotlclient.bridge.mixin.render;

import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.bridge.impl.AxoSpriteImpl;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.bridge.render.AxoSprites;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AxoSprites.class, remap = false)
public class AxoSpritesMixin {
	@Mutable
	@Shadow
	@Final
	public static AxoSprite BADGE;

	@Mutable
	@Shadow
	@Final
	public static AxoSprite BARRIER_ITEM_ICON;

	@Inject(method = "<clinit>", at = @At("HEAD"), cancellable = true)
	private static void setStaticValues(CallbackInfo info) {
		BADGE = new AxoSpriteImpl.Simple((Identifier) AxolotlClientCommon.BADGE_PATH, 0, 0, 16, 16);
		BARRIER_ITEM_ICON = new AxoSpriteImpl.Simple(new Identifier("textures/item/barrier.png"), 0, 0, 16, 16);

		info.cancel();
	}
}
