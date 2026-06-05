package io.github.axolotlclient.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

	@Accessor
	GuiRenderState getGuiRenderState();

	@Accessor("mainCamera")
	Camera getCamera();
}
