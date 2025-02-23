package io.github.axolotlclient.bridge.mixin.item;

import com.google.common.base.Preconditions;
import io.github.axolotlclient.bridge.item.AxoEnchant;
import io.github.axolotlclient.bridge.item.AxoItem;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.item.AxoItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
@Implements({
	@Interface(iface = AxoItemStack.class, prefix = "bridge$")
})
public abstract class ItemStackMixin {
	@Shadow
	public abstract Item getItem();

	@Shadow
	public int size;

	@Shadow
	public abstract ItemStack copy();

	@Shadow
	public abstract void setItem(Item par1);

	@Shadow
	public abstract int getDamage();

	@Shadow
	public abstract int getMaxDamage();

	@Shadow
	public abstract NbtList getEnchantments();

	@Shadow
	public abstract void addEnchantment(Enchantment enchantment, int level);

	public AxoItem bridge$getItem() {
		if (size == 0) {
			return AxoItems.AIR;
		}

		return getItem();
	}

	public AxoItemStack bridge$copy() {
		return copy();
	}

	public void bridge$setCount(int count) {
		size = count;
	}

	public int bridge$getCount() {
		return size;
	}

	@Intrinsic
	public int bridge$getDamage() {
		return getDamage();
	}

	@Intrinsic
	public int bridge$getMaxDamage() {
		return getMaxDamage();
	}

	@Unique
	@Nullable
	private NbtCompound axolotlclient$getEnchantment(int id) {
		final var enchants = getEnchantments();
		if (enchants == null) {
			return null;
		}

		for (int i = 0; i < enchants.size(); i++) {
			if (enchants.getCompound(i).getShort("id") == id) {
				return enchants.getCompound(i);
			}
		}

		return null;
	}

	public int bridge$getEnchantment(AxoEnchant enchant) {
		Preconditions.checkArgument(enchant != null, "enchant != null");
		final var data = axolotlclient$getEnchantment(((Enchantment) enchant).id);
		return data == null ? 0 : data.getShort("lvl");
	}

	public void bridge$setEnchantment(AxoEnchant enchant, int level) {
		Preconditions.checkArgument(enchant != null, "enchant != null");
		Preconditions.checkArgument(level > 1, "level > 1");

		int id = ((Enchantment) enchant).id;
		final var data = axolotlclient$getEnchantment(id);

		if (data == null) {
			addEnchantment(Enchantment.byId(id), level);
		} else {
			data.putShort("lvl", (short) level);
		}
	}

	public void bridge$removeEnchantment(AxoEnchant enchant) {
		final var enchants = getEnchantments();
		if (enchants == null) {
			return;
		}

		int id = ((Enchantment) enchant).id;

		for (int i = 0; i < enchants.size(); i++) {
			if (enchants.getCompound(i).getShort("id") == id) {
				enchants.remove(i);
				break;
			}
		}
	}
}
