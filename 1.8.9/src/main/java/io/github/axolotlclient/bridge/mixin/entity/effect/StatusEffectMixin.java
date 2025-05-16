package io.github.axolotlclient.bridge.mixin.entity.effect;

import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffect;
import io.github.axolotlclient.bridge.impl.AxoSpriteImpl;
import io.github.axolotlclient.bridge.render.AxoSprite;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.living.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StatusEffect.class)
public abstract class StatusEffectMixin implements AxoStatusEffect {
	@Shadow
	public abstract int getIconIndex();

	@Override
	public AxoSprite br$getSprite() {
		int iconIdx = getIconIndex();
		return new AxoSpriteImpl.Simple(Screen.BACKGROUND_LOCATION, iconIdx % 8 * 18, 198 + iconIdx / 8 * 18, 18, 18);
	}
}
