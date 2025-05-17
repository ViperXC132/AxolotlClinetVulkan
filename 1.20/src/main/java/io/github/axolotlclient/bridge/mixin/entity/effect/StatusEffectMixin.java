package io.github.axolotlclient.bridge.mixin.entity.effect;

import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffect;
import io.github.axolotlclient.bridge.impl.AxoSpriteImpl;
import io.github.axolotlclient.bridge.render.AxoSprite;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StatusEffect.class)
public abstract class StatusEffectMixin implements AxoStatusEffect {
	@Override
	public AxoSprite br$getSprite() {
		return new AxoSpriteImpl.Vanilla(
			MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite((StatusEffect) (Object) this)
		);
	}
}
