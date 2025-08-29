package io.github.axolotlclient.modules.auth.skin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import io.github.axolotlclient.modules.auth.Account;
import io.github.axolotlclient.modules.auth.MSApi;

public interface Skin extends Asset {
	boolean isClassicVariant();

	record Local(boolean classic, String id, Path file, String textureKey) implements Skin {

		@Override
		public boolean isClassicVariant() {
			return classic;
		}

		@Override
		public CompletableFuture<byte[]> getImage() {
			return CompletableFuture.supplyAsync(() -> {
				try {
					return Files.readAllBytes(file);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		}

		@Override
		public boolean isActive() {
			return false;
		}

		@Override
		public CompletableFuture<MSApi.MCProfile> equip(MSApi api, Account account) {
			return api.uploadAndSetSkin(account, this);
		}
	}
}
