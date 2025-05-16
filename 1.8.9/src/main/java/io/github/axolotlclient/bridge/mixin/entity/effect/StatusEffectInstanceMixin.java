package io.github.axolotlclient.bridge.mixin.entity.effect;

import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffect;
import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffectInstance;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectInstanceMixin implements AxoStatusEffectInstance {
	@Shadow
	public abstract int getId();

	@Override
	public AxoStatusEffect br$getType() {
		return StatusEffect.BY_ID[getId()];
	}
}
