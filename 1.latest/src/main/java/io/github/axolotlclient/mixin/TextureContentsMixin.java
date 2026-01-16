package io.github.axolotlclient.mixin;

import java.io.InputStream;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.util.AltIcons;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextureContents.class)
public abstract class TextureContentsMixin {

	@WrapOperation(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/Resource;open()Ljava/io/InputStream;"))
	private static InputStream getAltIcon(Resource instance, Operation<InputStream> original, @Local(argsOnly = true) Identifier id) {
		if (AxolotlClientCommon.BADGE_PATH.equals(id) && !AxolotlClientCommon.getInstance().getConfig().noAltIcons.get()) {
			return AltIcons.getAltIcon().orElseGet(() -> original.call(instance));
		}
		return original.call(instance);
	}
}
