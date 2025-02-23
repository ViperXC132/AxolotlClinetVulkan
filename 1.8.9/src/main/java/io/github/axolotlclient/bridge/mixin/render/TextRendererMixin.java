package io.github.axolotlclient.bridge.mixin.render;

import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.render.AxoTextRenderer;
import io.github.axolotlclient.bridge.util.AxoText;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TextRenderer.class)
@Implements(
	@Interface(iface = AxoTextRenderer.class, prefix = "bridge$")
)
public abstract class TextRendererMixin {
	@Shadow
	public abstract int draw(String string, float f, float g, int i, boolean bl);

	@Shadow
	public abstract int getWidth(String string);

	@Shadow
	public int fontHeight;

	// don't overwrite the normal method, and don't displace since this method trivial.
	@Intrinsic
	public int bridge$getWidth(AxoText text) {
		return getWidth(((Text) text).getString());
	}

	// don't overwrite the normal method, and don't displace since this method trivial.
	@Intrinsic
	public int bridge$getWidth(String text) {
		return getWidth(text);
	}

	public int bridge$drawString(AxoRenderContext render, String value, int x, int y, int color, boolean shadow) {
		return draw(value, x, y, color, shadow);
	}

	public void bridge$drawCenteredString(AxoRenderContext context, String value, int x, int y, int color, boolean shadow) {
		draw(value, x - (float) getWidth(value) / 2, y, color, shadow);
	}

	public int bridge$getFontHeight() {
		return fontHeight;
	}
}
