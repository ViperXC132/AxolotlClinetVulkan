package io.github.axolotlclient.bridge.mixin.item;

import io.github.axolotlclient.bridge.item.AxoItem;
import io.github.axolotlclient.bridge.item.AxoItemClass;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
@Implements({
	@Interface(iface = AxoItem.class, prefix = "bridge$")
})
public class ItemMixin {
	public boolean bridge$is(AxoItemClass itemClass) {
		return switch (itemClass) {
			case BOW -> ((Item) (Object) this) instanceof BowItem;
		};
	}
}
