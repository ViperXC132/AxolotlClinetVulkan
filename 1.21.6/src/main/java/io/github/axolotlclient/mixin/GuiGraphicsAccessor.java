package io.github.axolotlclient.mixin;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {

	@Accessor("scissorStack")
	GuiGraphics.ScissorStack getScissorStack();
}
