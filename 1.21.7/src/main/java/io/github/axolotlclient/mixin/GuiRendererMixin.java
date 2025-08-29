package io.github.axolotlclient.mixin;

import java.util.Map;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.axolotlclient.util.IdentifiablePiPRenderState;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {

	@WrapOperation(method = "preparePictureInPictureState", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object improvePiPRenderers(Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRenderer<?>> instance, Object o, Operation<PictureInPictureRenderer<?>> original, PictureInPictureRenderState state) {
		if (state instanceof IdentifiablePiPRenderState<?> idState) {
			return idState.renderer();
		}
		return original.call(instance, o);
	}
}
