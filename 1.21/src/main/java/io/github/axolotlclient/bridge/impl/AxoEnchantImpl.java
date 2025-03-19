package io.github.axolotlclient.bridge.impl;

import io.github.axolotlclient.bridge.item.AxoEnchant;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Holder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public record AxoEnchantImpl(RegistryKey<Enchantment> holder) implements AxoEnchant {
	public Optional<? extends Holder<Enchantment>> lookup() {
		final var world = MinecraftClient.getInstance().world;

		if (world == null) {
			return Optional.empty();
		}

		return world
			.getRegistryManager()
			.getLookupOrThrow(RegistryKeys.ENCHANTMENT).getHolder(holder);
	}
}
