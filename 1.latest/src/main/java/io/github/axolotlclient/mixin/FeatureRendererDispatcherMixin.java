package io.github.axolotlclient.mixin;

import io.github.axolotlclient.util.BadgeFeatureRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRendererMap;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderDispatcher.class)
public abstract class FeatureRendererDispatcherMixin {
	@Shadow
	@Final
	private FeatureRendererMap featureRenderers;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void addFeatureRenderers(RenderBuffers renderBuffers, ModelManager modelManager, AtlasManager atlasManager, Font font, GameRenderState gameRenderState, CallbackInfo ci) {
		featureRenderers.put(BadgeFeatureRenderer.TYPE, new BadgeFeatureRenderer());
	}
}
