package io.github.axolotlclient.bridge.mixin.util;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.bridge.util.AxoText;
import net.minecraft.text.Formatting;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Style.class)
public abstract class StyleMixin implements AxoText.Style {
	@Unique
	@Nullable
	private Integer axolotlclient$color;

	@Shadow
	public abstract Style copy();

	@Shadow
	public abstract Style setHoverEvent(HoverEvent par1);

	@Shadow
	public abstract Style setColor(Formatting par1);

	@Shadow
	private Style parent;

	@Override
	public AxoText.Style br$color(AxoText.Color color) {
		return setColor(switch (color) {
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
		axolotlclient$color = color;
		return this;
	}

	@Override
	public AxoText.Style br$tooltip(AxoText text) {
		return setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (Text) text));
	}

	@Inject(method = "asString", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Style;isBold()Z"))
	private void formatColorCode(CallbackInfoReturnable<String> cir, @Local StringBuilder sb) {
		Integer color = null;
		StyleMixin s = this;

		while (s != null && color == null) {
			color = s.axolotlclient$color;
			s = (StyleMixin) (Object) s.parent;
		}

		if (color != null) {
			sb.append("§#").append(StringUtils.leftPad(color.toString(), 6, "0"));
		}
	}
}
