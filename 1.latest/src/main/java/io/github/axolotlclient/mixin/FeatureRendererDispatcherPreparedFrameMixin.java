package io.github.axolotlclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.util.duck.SubmitNodeCollectorExtension;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderDispatcher.PreparedFrame.class)
public abstract class FeatureRendererDispatcherPreparedFrameMixin {
	@Shadow
	protected abstract void executePhase(FeatureRenderPhase<?> par1, FeatureFrameContext par2);

	@Shadow
	private FeatureFrameContext context;

	@Inject(method = "executeTranslucent", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/SubmitNodeCollection;translucentCustomGeometry:Lnet/minecraft/client/renderer/feature/phase/SimpleFeatureRenderPhase;", opcode = Opcodes.GETFIELD))
	private void executeBadge(CallbackInfo ci, @Local SubmitNodeCollection collection) {
		executePhase(((SubmitNodeCollectorExtension)collection).axolotlclient$badgePhase(), context);
	}
}
