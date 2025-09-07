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
import java.util.concurrent.CompletableFuture;

import io.github.axolotlclient.modules.auth.Account;
import io.github.axolotlclient.modules.auth.MSApi;

public interface Skin extends Asset {
	boolean classicVariant();
	void classicVariant(boolean classic);

	final class Local implements Skin {
		public static final String METADATA_SUFFIX = ".meta";
		private boolean classic;
		private final Path file;
		private final String textureKey;

		public Local(boolean classic, Path file, String textureKey) {
			this.classic = classic;
			this.file = file;
			this.textureKey = textureKey;
		}

		@Override
		public boolean classicVariant() {
			return classic;
		}

		@Override
		public void classicVariant(boolean classic) {
			if (classic != this.classic) {

			}
			this.classic = classic;
		}

		@Override
		public CompletableFuture<byte[]> image() {
			return CompletableFuture.supplyAsync(() -> {
				try {
					return Files.readAllBytes(file);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		}

		@Override
		public boolean active() {
			return false;
		}

		@Override
		public CompletableFuture<MSApi.MCProfile> equip(MSApi api, Account account) {
			return api.uploadAndSetSkin(account, this);
		}

		@Override
		public boolean isLocal() {
			return true;
		}

		@Override
		public Path file() {
			return file;
		}

		@Override
		public String textureKey() {
			return textureKey;
		}
	}

	record Shared(Local local, MSApi.MCProfile.OnlineSkin online) implements Skin {

		@Override
		public boolean classicVariant() {
			return local.classicVariant();
		}

		@Override
		public void classicVariant(boolean classic) {
			local.classicVariant(classic);
		}

		@Override
		public CompletableFuture<byte[]> image() {
			return local.image();
		}

		@Override
		public boolean active() {
			return online.active();
		}

		@Override
		public CompletableFuture<MSApi.MCProfile> equip(MSApi api, Account account) {
			return online.equip(api, account);
		}

		@Override
		public String textureKey() {
			return local.textureKey();
		}

		@Override
		public boolean isOnline() {
			return true;
		}

		@Override
		public boolean isLocal() {
			return true;
		}

		@Override
		public Path file() {
			return local.file();
		}

		@Override
		public String url() {
			return online.url();
		}

		@Override
		public boolean supportsDownload() {
			return true;
		}
	}
}
