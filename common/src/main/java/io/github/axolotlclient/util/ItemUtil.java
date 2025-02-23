package io.github.axolotlclient.util;

import io.github.axolotlclient.bridge.AxoMinecraftClient;
import io.github.axolotlclient.bridge.entity.AxoPlayer;
import io.github.axolotlclient.bridge.item.AxoPlayerInventory;
import io.github.axolotlclient.bridge.item.AxoItem;
import io.github.axolotlclient.bridge.item.AxoItemStack;

public class ItemUtil {
	public static int getTotal(AxoPlayerInventory inventory, AxoItem item) {
		return inventory.getItems().stream()
			.filter(x -> x.getItem() == item)
			.mapToInt(AxoItemStack::getCount)
			.sum();
	}

	public static int getTotal(AxoMinecraftClient inventory, AxoItem item) {
		AxoPlayer player = inventory.getPlayer();

		if (player == null) {
			return 0;
		}

		return getTotal(player.getInventory(), item);
	}
}
