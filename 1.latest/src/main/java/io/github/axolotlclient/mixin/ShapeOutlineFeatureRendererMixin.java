package io.github.axolotlclient.mixin;

import java.util.List;

import io.github.axolotlclient.util.DrawUtil;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.feature.ShapeOutlineFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShapeOutlineFeatureRenderer.class)
public abstract class ShapeOutlineFeatureRendererMixin extends RenderTypeFeatureRenderer<ShapeOutlineFeatureRenderer.Submit> {

	@Inject(method = "buildGroup", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
	private void drawFill(FeatureFrameContext context, List<ShapeOutlineFeatureRenderer.Submit> submits, CallbackInfo ci) {
		if (!submits.isEmpty()) {
			var submit = submits.getFirst();
			var pose = submit.pose();
			DrawUtil.drawOutlines(this::getVertexBuilder, pose, submit.shape());
		}
	}
}
