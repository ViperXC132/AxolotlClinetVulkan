/*
 * Copyright © 2025 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

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
import io.github.axolotlclient.mixin.skins.SkinTextureDownloaderAccessor;
import io.github.axolotlclient.modules.auth.Account;
import io.github.axolotlclient.util.ClientColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

public class SkinManager {

	private final Set<AxoIdentifier> loadedTextures = new ConcurrentSkipListSet<>(Comparator.comparing(Object::toString));

	public Skin read(Path p) {
		return read(p, true);
	}

	public Skin read(Path p, boolean fix) {
		if (p.getFileName().toString().endsWith(Skin.Local.METADATA_SUFFIX)) return null;
		boolean slim;
		String sha256;
		try {
			var in = Files.readAllBytes(p);
			sha256 = Hashing.sha256().hashBytes(in).toString();
			try (var img = NativeImage.read(in)) {
				int width = img.getWidth();
				int height = img.getHeight();
				if (width != 64) return null;
				if (height == 32) {
					if (fix) {
						try (var img2 = SkinTextureDownloaderAccessor.invokeProcessLegacySkin(img, "local")) {
							img2.writeToFile(p);
						}
					}
					slim = false;
				} else if (height != 64) {
					return null;
				} else {
					slim = ClientColors.ARGB.alpha(img.getPixel(50, 16)) == 0;
				}
				var metadata = Skin.Local.readMetadata(p);
				if (metadata != null && metadata.containsKey(Skin.Local.CLASSIC_METADATA_KEY)) {
					slim = !(boolean) metadata.get(Skin.Local.CLASSIC_METADATA_KEY);
				}
			}
			return new Skin.Local(!slim, p, sha256);
		} catch (Exception e) {
			AxolotlClientCommon.getInstance().getLogger().warn("Failed to probe skin: ", e);
		}
		return null;
	}


	public CompletableFuture<AxoIdentifier> loadSkin(Skin skin) {
		var rl = AxoIdentifier.of(AxolotlClientCommon.MODID, "skins/" + skin.textureKey());
		if (loadedTextures.contains(rl)) {
			return CompletableFuture.completedFuture(rl);
		}

		return skin.image().thenApplyAsync(bytes -> {
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
		var rl = AxoIdentifier.of(AxolotlClientCommon.MODID, "capes/" + cape.textureKey());
		if (loadedTextures.contains(rl)) {
			return rl;
		}

		return cape.image().thenApplyAsync(bytes -> {
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
