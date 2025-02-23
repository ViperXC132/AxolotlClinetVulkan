package io.github.axolotlclient.bridge.impl;

import io.github.axolotlclient.bridge.key.AxoKey;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public record AxoKeyImpl(int id) implements AxoKey {
	private static final Int2ObjectMap<AxoKeyImpl> MAP = new Int2ObjectOpenHashMap<>();

	public static AxoKeyImpl get(int id) {
		return MAP.computeIfAbsent(id, AxoKeyImpl::new);
	}
}
