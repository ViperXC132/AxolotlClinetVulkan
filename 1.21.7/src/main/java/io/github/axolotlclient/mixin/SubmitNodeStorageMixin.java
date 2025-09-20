package io.github.axolotlclient.mixin;

import io.github.axolotlclient.util.duck.SubmitNodeCollectorExtension;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SubmitNodeStorage.class)
public abstract class SubmitNodeStorageMixin implements SubmitNodeCollectorExtension {
	@Shadow
	public abstract SubmitNodeCollection order(int i);

	@Override
	public void axolotlclient$lastNameTagSubmitHasBadge() {
		((SubmitNodeCollectorExtension)order(0)).axolotlclient$lastNameTagSubmitHasBadge();
	}
}
