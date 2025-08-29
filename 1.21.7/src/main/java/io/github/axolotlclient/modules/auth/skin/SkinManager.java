package io.github.axolotlclient.modules.auth.skin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.api.util.UUIDHelper;
import io.github.axolotlclient.bridge.AxoMinecraftClient;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.auth.Account;
import io.github.axolotlclient.util.ClientColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SkinManager {

	private final Set<AxoIdentifier> loadedTextures = new ConcurrentSkipListSet<>(Comparator.comparing(Object::toString));

	public Skin read(Path p) {
		boolean slim;
		String sha256;
		try {
			var in = Files.readAllBytes(p);
			sha256 = Hashing.sha256().hashBytes(in).toString();
			try (var img = NativeImage.read(in)) {
				slim = (ClientColors.ARGB.alpha(img.getPixel(47, 63)) == 0);
			}
			return new Skin.Local(!slim, Hashing.sha512().hashUnencodedChars(p.toString()).toString(), p, sha256);
		} catch (IOException e) {
			AxolotlClientCommon.getInstance().getLogger().warn("Failed to probe skin: ", e);
		}
		return null;
	}


	public CompletableFuture<AxoIdentifier> loadSkin(Skin skin, Account owner) {
		var rl = getRl(skin);
		if (loadedTextures.contains(rl)) {
			return CompletableFuture.completedFuture(rl);
		}

		return skin.getImage().thenApplyAsync(bytes -> {
			try {
				var tex = new DynamicTexture(rl::toString, NativeImage.read(bytes));
				Minecraft.getInstance().getTextureManager().register((ResourceLocation) rl, tex);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			loadedTextures.add(rl);
			return rl;
		}, AxoMinecraftClient.getInstance()).handle((v, t) -> {
			if (t != null) {
				AxolotlClientCommon.getInstance().getLogger().warn("Failed to load skin!", t);
			}
			return v;
		});
	}

	public AxoIdentifier loadCape(Cape cape) {
		var rl = getRl(cape);
		if (loadedTextures.contains(rl)) {
			return rl;
		}

		return cape.getImage().thenApplyAsync(bytes -> {
			try {
				var tex = new DynamicTexture(rl::toString, NativeImage.read(bytes));
				Minecraft.getInstance().getTextureManager().register((ResourceLocation) rl, tex);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			loadedTextures.add(rl);
			return rl;
		}, AxoMinecraftClient.getInstance()).handle((id, t) -> {
			if (t != null) {
				AxolotlClientCommon.getInstance().getLogger().warn("Failed to load cape!", t);
			}
			return id;
		}).getNow(null);

	}

	public void releaseAll() {
		loadedTextures.forEach(id -> Minecraft.getInstance().getTextureManager().release((ResourceLocation) id));
		loadedTextures.clear();
	}

	private @NotNull AxoIdentifier getRl(Skin skin) {
		return AxoIdentifier.of(AxolotlClientCommon.MODID, "skins/" + Hashing.sha256().hashUnencodedChars(skin.id()));
	}

	private @NotNull AxoIdentifier getRl(Cape cape) {
		return AxoIdentifier.of(AxolotlClientCommon.MODID, "capes/" + Hashing.sha256().hashUnencodedChars(cape.id()));
	}

	public String getDefaultSkinHash(Account account) {
		var skin = DefaultPlayerSkin.get(UUIDHelper.fromUndashed(account.getUuid()));
		var mc = Minecraft.getInstance();
		var resourceManager = mc.getResourceManager();
		try {
			var res = resourceManager.getResourceOrThrow(skin.texture());
			try (
				var in = res.open()) {
				return Hashing.sha256().hashBytes(in.readAllBytes()).toString();
			}
		} catch (IOException ignored) {
		}
		return null;
	}
}
