package io.github.axolotlclient.bridge.mixin.util;

import io.github.axolotlclient.bridge.util.AxoText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Text.class)
public interface TextMixin extends AxoText {
}
