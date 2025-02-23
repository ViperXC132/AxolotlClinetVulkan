package io.github.axolotlclient.bridge.item;

import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.internal.PlatformImplInternal;

public interface AxoItemStack {
	static AxoItemStack of(AxoItem item, int count) {
		return PlatformImplInternal.createItemStack(item, count);
	}

	static AxoItemStack of(AxoItem item) {
		return PlatformImplInternal.createItemStack(item, 1);
	}

	default AxoItem getItem() {
		throw BridgeUtil.noImpl();
	}

	default AxoItemStack copy() {
		throw BridgeUtil.noImpl();
	}

	default void setCount(int count) {
		throw BridgeUtil.noImpl();
	}

	default int getCount() {
		throw BridgeUtil.noImpl();
	}

	default int getDamage() {
		throw BridgeUtil.noImpl();
	}

	default int getMaxDamage() {
		throw BridgeUtil.noImpl();
	}

	default boolean isEmpty() {
		return getCount() == 0 || getItem() == AxoItems.AIR;
	}

	default int getEnchantment(AxoEnchant enchant) {
		throw BridgeUtil.noImpl();
	}

	default void setEnchantment(AxoEnchant enchant, int level) {
		throw BridgeUtil.noImpl();
	}

	default void removeEnchantment(AxoEnchant enchant) {
		throw BridgeUtil.noImpl();
	}

	default boolean hasEnchantment(AxoEnchant enchant) {
		return getEnchantment(enchant) != 0;
	}
}
