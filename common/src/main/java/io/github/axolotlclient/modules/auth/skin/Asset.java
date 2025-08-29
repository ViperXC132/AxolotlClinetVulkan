package io.github.axolotlclient.modules.auth.skin;

import java.util.concurrent.CompletableFuture;

import io.github.axolotlclient.modules.auth.Account;
import io.github.axolotlclient.modules.auth.MSApi;

public interface Asset {
	String id();
	CompletableFuture<byte[]> getImage();

	boolean isActive();

	CompletableFuture<MSApi.MCProfile> equip(MSApi api, Account account);

	String textureKey();
}
