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
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

import com.google.common.hash.Hashing;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.api.util.UUIDHelper;
import io.github.axolotlclient.bridge.AxoMinecraftClient;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.auth.Account;
import io.github.axolotlclient.util.ClientColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryStack;

public class SkinManager {

	private final Set<AxoIdentifier> loadedTextures = new ConcurrentSkipListSet<>(Comparator.comparing(Object::toString));

	public Skin read(Path p) {
		boolean slim;
		String sha256;
		try {
			var in = Files.readAllBytes(p);
			sha256 = Hashing.sha256().hashBytes(in).toString();
			try (var img = NativeImage.read(ByteBuffer.wrap(in))) {
				slim = (ClientColors.ARGB.alpha(img.getPixelColor(47, 63)) == 0);
			}
			return new Skin.Local(!slim, Hashing.sha512().hashUnencodedChars(p.toString()).toString(), p, sha256);
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
			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				ByteBuffer byteBuffer = memoryStack.malloc(bytes.length);
				byteBuffer.put(bytes);
				byteBuffer.rewind();
				var tex = new NativeImageBackedTexture(NativeImage.read(byteBuffer));
				MinecraftClient.getInstance().getTextureManager().registerTexture((Identifier) rl, tex);
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
			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				ByteBuffer byteBuffer = memoryStack.malloc(bytes.length);
				byteBuffer.put(bytes);
				byteBuffer.rewind();
				var tex = new NativeImageBackedTexture(NativeImage.read(byteBuffer));
				MinecraftClient.getInstance().getTextureManager().registerTexture((Identifier) rl, tex);
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
		loadedTextures.forEach(id -> MinecraftClient.getInstance().getTextureManager().destroyTexture((Identifier) id));
		loadedTextures.clear();
	}

	public String getDefaultSkinHash(Account account) {
		var skin = DefaultSkinHelper.getTexture(UUIDHelper.fromUndashed(account.getUuid()));
		var mc = MinecraftClient.getInstance();
		var resourceManager = mc.getResourceManager();
		try {
			var res = resourceManager.getResource(skin);
			try (
				var in = res.br$asStream()) {
				return Hashing.sha256().hashBytes(in.readAllBytes()).toString();
			}
		} catch (IOException ignored) {
		}
		return null;
	}
}
