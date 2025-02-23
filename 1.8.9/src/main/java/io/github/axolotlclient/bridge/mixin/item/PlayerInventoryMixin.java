package io.github.axolotlclient.bridge.mixin.item;

import io.github.axolotlclient.bridge.impl.Bridge;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.item.AxoPlayerInventory;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerInventory.class)
@Implements({
	@Interface(iface = AxoPlayerInventory.class, prefix = "bridge$")
})
public abstract class PlayerInventoryMixin {
	@Shadow
	public abstract ItemStack getMainHandStack();

	@Shadow
	public abstract int getSize();

	@Shadow
	public abstract ItemStack getStack(int slot);

	@Shadow
	public abstract ItemStack getArmor(int par1);

	public AxoItemStack bridge$getMainHand() {
		return Bridge.wrapStack(getMainHandStack());
	}

	public List<AxoItemStack> bridge$getItems() {
		return IntStream.range(0, getSize())
			.mapToObj(x -> Bridge.wrapStack(getStack(x)))
			.toList();
	}

	public List<AxoItemStack> bridge$getArmor() {
		return IntStream.range(0, 4)
			.mapToObj(x -> Bridge.wrapStack(getArmor(x)))
			.toList();
	}
}
