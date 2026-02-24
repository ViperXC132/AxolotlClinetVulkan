package io.github.axolotlclient.mixin;

import java.util.function.Consumer;

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextFieldWidget.class)
public interface AxoConfigTextFieldWidgetAccessor {
	@Accessor("changedListener")
	Consumer<String> getResponder();
}
