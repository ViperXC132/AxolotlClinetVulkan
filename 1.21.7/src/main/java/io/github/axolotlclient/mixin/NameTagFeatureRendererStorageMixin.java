package io.github.axolotlclient.mixin;

import java.util.List;

import io.github.axolotlclient.util.duck.NameTagFeatureRendererStorageExtension;
import io.github.axolotlclient.util.duck.NameTagSubmitExtension;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.NameTagFeatureRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NameTagFeatureRenderer.Storage.class)
public abstract class NameTagFeatureRendererStorageMixin implements NameTagFeatureRendererStorageExtension {

	@Shadow
	@Final
	List<SubmitNodeStorage.NameTagSubmit> nameTagSubmitsNormal;

	@Override
	public void axolotlclient$lastNameTagSubmitHasBadge() {
		((NameTagSubmitExtension) (Object) nameTagSubmitsNormal.getLast()).axolotlclient$hasBadge(true);
	}

	@Override
	public void axolotlclient$lastNameTagSubmitIsLevelHead() {
		((NameTagSubmitExtension) (Object) nameTagSubmitsNormal.getLast()).axolotlclient$isForLevelHead(true);
	}
}
