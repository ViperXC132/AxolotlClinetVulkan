package io.github.axolotlclient.bridge.impl;

import com.google.common.base.Preconditions;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.Blocks;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.ornithemc.osl.keybinds.api.KeyBindingEvents;
import org.jetbrains.annotations.Nullable;

public class Bridge {
	@Nullable
	private static List<KeyBinding> keyBindings = new ArrayList<>();

	public static AxoItemStack wrapStack(@Nullable ItemStack stack) {
		// TODO: this is a bit of a janky workaround, but whatever...
		return Objects.requireNonNullElseGet(stack, () -> new ItemStack(Item.byBlock(Blocks.STONE), 0));
	}

	public static ItemStack unwrapStack(AxoItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}

		Preconditions.checkArgument(stack instanceof ItemStack, "stack instanceof ItemStack");
		return (ItemStack) stack;
	}

	public static void addKeybind(KeyBinding keyBinding) {
		Preconditions.checkState(keyBindings != null, "keybind registered too late!");
		keyBindings.add(keyBinding);
	}

	public static void init() {
		KeyBindingEvents.REGISTER_KEYBINDS.register(keyBindingRegistry -> {
			Preconditions.checkState(keyBindings != null, "double keybind register");
			keyBindings.forEach(keyBindingRegistry::register);
			keyBindings = null;
		});
	}
}
