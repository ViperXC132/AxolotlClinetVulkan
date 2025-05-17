package io.github.axolotlclient.bridge.mixin.entity;

import io.github.axolotlclient.bridge.entity.AxoEntity;
import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffectInstance;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements AxoEntity {
	@Shadow
	@Final
	private Map<StatusEffect, StatusEffectInstance> activeStatusEffects;

	@Override
	public List<AxoStatusEffectInstance> br$getStatusEffects() {
		return List.copyOf(activeStatusEffects.values());
	}
}
