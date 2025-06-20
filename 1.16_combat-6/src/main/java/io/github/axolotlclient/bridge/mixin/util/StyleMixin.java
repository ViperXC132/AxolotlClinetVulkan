package io.github.axolotlclient.bridge.mixin.util;

import io.github.axolotlclient.bridge.util.AxoText;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Style.class)
public abstract class StyleMixin implements AxoText.Style {
	@Shadow
	public abstract Style withColor(TextColor par1);

	@Shadow
	public abstract Style withFormatting(Formatting par1);


	@Shadow
	public abstract Style withHoverEvent(HoverEvent par1);

	@Override
	public AxoText.Style br$color(AxoText.Color color) {
		return withFormatting(switch (color) {
			case BLACK -> Formatting.BLACK;
			case DARK_BLUE -> Formatting.DARK_BLUE;
			case DARK_GREEN -> Formatting.DARK_GREEN;
			case DARK_AQUA -> Formatting.DARK_AQUA;
			case DARK_RED -> Formatting.DARK_RED;
			case DARK_PURPLE -> Formatting.DARK_PURPLE;
			case GOLD -> Formatting.GOLD;
			case GRAY -> Formatting.GRAY;
			case DARK_GRAY -> Formatting.DARK_GRAY;
			case BLUE -> Formatting.BLUE;
			case GREEN -> Formatting.GREEN;
			case AQUA -> Formatting.AQUA;
			case RED -> Formatting.RED;
			case LIGHT_PURPLE -> Formatting.LIGHT_PURPLE;
			case YELLOW -> Formatting.YELLOW;
			case WHITE -> Formatting.WHITE;
		});
	}

	@Override
	public AxoText.Style br$color(int color) {
		return withColor(TextColor.fromRgb(color));
	}

	@Override
	public AxoText.Style br$tooltip(AxoText text) {
		return withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (Text) text));
	}
}
