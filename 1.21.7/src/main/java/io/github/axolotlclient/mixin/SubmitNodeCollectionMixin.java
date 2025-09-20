package io.github.axolotlclient.mixin;

import io.github.axolotlclient.util.duck.NameTagFeatureRendererStorageExtension;
import io.github.axolotlclient.util.duck.SubmitNodeCollectorExtension;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SubmitNodeCollection.class)
public abstract class SubmitNodeCollectionMixin implements SubmitNodeCollectorExtension {

	@Shadow
	@Final
	private NameTagFeatureRenderer.Storage nameTagSubmits;

	@Override
	public void axolotlclient$lastNameTagSubmitHasBadge() {
		((NameTagFeatureRendererStorageExtension) nameTagSubmits).axolotlclient$lastNameTagSubmitHasBadge();
	}

	@Override
	public void axolotlclient$lastNameTagSubmitIsLevelHead() {
		((NameTagFeatureRendererStorageExtension) nameTagSubmits).axolotlclient$lastNameTagSubmitIsLevelHead();
	}
}
