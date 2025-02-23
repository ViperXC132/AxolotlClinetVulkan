package io.github.axolotlclient.bridge.mixin.item;

import io.github.axolotlclient.bridge.item.AxoEnchant;
import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Enchantment.class)
@Implements({
	@Interface(iface = AxoEnchant.class, prefix = "bridge$")
})
public class EnchantmentMixin {
}
