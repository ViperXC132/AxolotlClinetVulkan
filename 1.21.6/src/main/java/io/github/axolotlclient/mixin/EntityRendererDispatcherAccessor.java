package io.github.axolotlclient.mixin;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderDispatcher.class)
public interface EntityRendererDispatcherAccessor {

	@Accessor("shouldRenderShadow")
	boolean getShouldRenderShadow();
}
