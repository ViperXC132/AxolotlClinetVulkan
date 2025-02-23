package io.github.axolotlclient.bridge.impl;

import io.github.axolotlclient.bridge.item.AxoItem;
import io.github.axolotlclient.bridge.item.AxoItemClass;
import lombok.Getter;

public class AirItemImpl implements AxoItem {
	@Getter
	public static final AirItemImpl instance = new AirItemImpl();

	private AirItemImpl() {
	}

	@Override
	public boolean is(AxoItemClass itemClass) {
		return false;
	}
}
