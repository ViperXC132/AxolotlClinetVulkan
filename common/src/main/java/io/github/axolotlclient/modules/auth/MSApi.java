/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.modules.auth;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.github.mizosoft.methanol.FormBodyPublisher;
import com.github.mizosoft.methanol.MediaType;
import com.github.mizosoft.methanol.MultipartBodyPublisher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.modules.auth.skin.Cape;
import io.github.axolotlclient.modules.auth.skin.Skin;
import io.github.axolotlclient.util.GsonHelper;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.NetworkUtil;

// Partly oriented on In-Game-Account-Switcher by The-Fireplace, VidTu
public class MSApi {

	private static final String CLIENT_ID = "938592fc-8e01-4c6d-b56d-428c7d9cf5ea"; // AxolotlClient MSA ClientID
	private static final String SCOPES = "XboxLive.signin offline_access";
	private static final String XBL_AUTH_URL = "https://user.auth.xboxlive.com/user/authenticate";
	private static final String MS_DEVICE_CODE_LOGIN_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode?mkt=";
	private static final String MS_TOKEN_LOGIN_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token";
	private static final String XBL_XSTS_AUTH_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
	private static final String MC_LOGIN_WITH_XBOX_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";

	private final Supplier<String> languageSupplier;
	private final Logger logger;
	private final Accounts accounts;
	private final HttpClient client;
	private final Gson gson = new GsonBuilder().create();

	public static MSApi INSTANCE;

	public MSApi(Accounts accounts, Supplier<String> languageSupplier) {
		this.logger = AxolotlClientCommon.getInstance().getLogger();
		this.client = NetworkUtil.createHttpClient();
		this.accounts = accounts;
		this.languageSupplier = languageSupplier;
		INSTANCE = this;
	}

	public CompletableFuture<?> startDeviceAuth() {

		String[] lang = languageSupplier.get().replace("_", "-").split("-");
		logger.debug("starting ms device auth flow");
		// https://learn.microsoft.com/en-us/entra/identity-platform/v2-oauth2-device-code#device-authorization-response
		HttpRequest.Builder builder = HttpRequest.newBuilder()
			.POST(FormBodyPublisher.newBuilder()
				.query("client_id", CLIENT_ID)
				.query("scope", SCOPES).build())
			.header("content-type", "application/x-www-form-urlencoded")
			.uri(URI.create(MS_DEVICE_CODE_LOGIN_URL + lang[0] + "-" + lang[1].toUpperCase(Locale.ROOT)));
		return requestJson(builder.build())
			.thenApply(object -> {
				int expiresIn = object.get("expires_in").getAsInt();
				String deviceCode = object.get("device_code").getAsString();
				String userCode = object.get("user_code").getAsString();
				String verificationUri = object.get("verification_uri").getAsString();
				int interval = object.get("interval").getAsInt();
				String message = object.get("message").getAsString();
				logger.debug("displaying device code to user");
				DeviceFlowData data = new DeviceFlowData(message, verificationUri, deviceCode, userCode, expiresIn, interval);
				accounts.displayDeviceCode(data);
				return data;
			})
			.thenApply(data -> {
				logger.debug("waiting for user authorization...");
				long start = System.currentTimeMillis();
				while (System.currentTimeMillis() - start < data.getExpiresIn() * 1000L && !data.isCancelled()) {
					if ((System.currentTimeMillis() - start) % data.getInterval() == 0) {
						HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().POST(
								FormBodyPublisher.newBuilder().query("client_id", CLIENT_ID)
									.query("device_code", data.getDeviceCode())
									.query("grant_type", "urn:ietf:params:oauth:grant-type:device_code")
									.build()
							)
							.uri(URI.create(MS_TOKEN_LOGIN_URL));
						JsonObject response = requestJson(requestBuilder.build()).join();

						if (response.has("refresh_token") && response.has("access_token")) {
							data.setStatus("auth.working");
							return authenticateFromMSTokens(response.get("access_token").getAsString(),
								response.get("refresh_token").getAsString())
								.thenAccept(o -> {
									o.ifPresent(a -> {
										int index = accounts.getAccounts().indexOf(a);
										if (index == -1) {
											accounts.getAccounts().add(a);
										} else {
											accounts.getAccounts().set(index, a);
										}
										accounts.login(a);
										accounts.save();
										data.setStatus("auth.finished");
									});
								}).join();
						}

						if (response.has("error")) {
							String error = response.get("error").getAsString();
							switch (error) {
								case "authorization_pending":
									continue;
								case "bad_verification_code":
									throw new IllegalStateException("Bad verification code! " + response);
								case "authorization_declined":
								case "expired_token":
								default:
									break;
							}
						}
					}
				}
				return null;
			});
	}

	private CompletableFuture<Optional<Account>> authenticateFromMSTokens(String accessToken, String refreshToken) {
		return CompletableFuture.supplyAsync(() -> {
			logger.debug("getting xbl token... ");
			XblData xbl = authXbl(accessToken).join();
			logger.debug("getting xsts token...");
			XblData xsts = authXstsMC(xbl.token()).join();
			logger.debug("getting mc auth token...");
			MCXblData mc = authMC(xsts.displayClaims().uhs(), xsts.token()).join();

			JsonObject profileJson = getMCProfile(mc.accessToken()).join();
			if (profileJson.has("error") && "NOT_FOUND".equals(profileJson.get("error").getAsString())) {
				AxolotlClientCommon.getInstance().getNotificationProvider().addStatus("auth.notif.login.failed", "auth.notif.login.failed.no_profile");
				return Optional.empty();
			}
			logger.debug("retrieving entitlements...");
			if (!checkOwnership(mc.accessToken()).join()) {
				AxolotlClientCommon.getInstance().getNotificationProvider().addStatus("auth.notif.login.failed", "auth.notif.login.failed.no_entitlement");
				logger.warn("Failed to check for game ownership!");
				return Optional.empty();
			}
			logger.debug("getting profile...");
			MCProfile profile = MCProfile.get(profileJson);
			return Optional.of(new Account(profile.name(), profile.id(), mc.accessToken(), mc.expiration(), refreshToken, accessToken));
		});
	}

	public record MCProfile(String id, String name, List<OnlineSkin> skins, List<OnlineCape> capes) {
		public static MCProfile get(JsonObject json) {
			return new MCProfile(json.get("id").getAsString(), json.get("name").getAsString(),
				GsonHelper.jsonArrayToStream(json.getAsJsonArray("skins"))
					.map(s -> OnlineSkin.get(s.getAsJsonObject()))
					.toList(), GsonHelper.jsonArrayToStream(json.getAsJsonArray("capes"))
				.map(s -> OnlineCape.get(s.getAsJsonObject()))
				.toList());
		}

		public record OnlineSkin(String id, String state, String url, String variant, String textureKey) implements Skin {
			public static final String VARIANT_CLASSIC = "CLASSIC";
			public static final String VARIANT_SLIM = "SLIM";
			public static final String STATE_ACTIVE = "ACTIVE";

			public static OnlineSkin get(JsonObject object) {
				String url = object.get("url").getAsString();
				return new OnlineSkin(object.get("id").getAsString(),
					object.get("state").getAsString(),
					url,
					object.get("variant").getAsString(),
					url.substring(url.lastIndexOf("/")+1));
			}

			@Override
			public boolean isOnline() {
				return true;
			}

			public CompletableFuture<byte[]> image() {
				return INSTANCE.client.sendAsync(HttpRequest.newBuilder(URI.create(url())).GET().build(), HttpResponse.BodyHandlers.ofByteArray())
					.thenApplyAsync(res -> {
						if (res.statusCode() == 200) {
							return res.body();
						}
						throw new IllegalArgumentException("abnormal status: " + res.statusCode());
					});
			}

			public boolean isClassicVariant() {
				return VARIANT_CLASSIC.equals(variant());
			}

			public boolean isSlimVariant() {
				return VARIANT_SLIM.equals(variant());
			}

			public boolean active() {
				return STATE_ACTIVE.equals(state());
			}

			@Override
			public CompletableFuture<MCProfile> equip(MSApi api, Account account) {
				return api.setSkin(account, this);
			}

			@Override
			public boolean supportsDownload() {
				return true;
			}
		}

		public record OnlineCape(String id, String state, String url, String alias, String textureKey) implements Cape {
			public static final String STATE_ACTIVE = "ACTIVE";

			public static OnlineCape get(JsonObject object) {
				String url = object.get("url").getAsString();
				return new OnlineCape(object.get("id").getAsString(), object.get("state").getAsString(),
					url, object.get("alias").getAsString(), url.substring(url.lastIndexOf("/")+1));
			}

			public CompletableFuture<byte[]> image() {
				return INSTANCE.client.sendAsync(HttpRequest.newBuilder(URI.create(url())).GET().build(), HttpResponse.BodyHandlers.ofByteArray())
					.thenApplyAsync(res -> {
						if (res.statusCode() == 200) {
							return res.body();
						}
						throw new IllegalArgumentException("abnormal status: " + res.statusCode());
					});
			}

			@Override
			public boolean isOnline() {
				return true;
			}

			public boolean active() {
				return STATE_ACTIVE.equals(state());
			}

			@Override
			public CompletableFuture<MCProfile> equip(MSApi api, Account account) {
				return api.showCape(account, this);
			}
		}
	}

	private CompletableFuture<XblData> authXbl(String code) {
		JsonObject object = new JsonObject();
		JsonObject properties = new JsonObject();
		properties.addProperty("AuthMethod", "RPS");
		properties.addProperty("SiteName", "user.auth.xboxlive.com");
		properties.addProperty("RpsTicket", "d=" + code);
		object.add("Properties", properties);
		object.addProperty("RelyingParty", "http://auth.xboxlive.com");
		object.addProperty("TokenType", "JWT");
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
			.uri(URI.create(XBL_AUTH_URL))
			.POST(HttpRequest.BodyPublishers.ofString(object.toString()))
			.header("content-type", "application/json")
			.header("accept", "application/json");

		return requestJson(requestBuilder.build()).thenApply(response -> new XblData(Instant.parse(response.get("IssueInstant").getAsString()), Instant.parse(response.get("NotAfter").getAsString()),
			response.get("Token").getAsString(), new XblData.DisplayClaims(response.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString())));
	}

	private record XblData(Instant issueInstant, Instant notAfter, String token, DisplayClaims displayClaims) {
		private record DisplayClaims(String uhs) {
		}
	}

	private CompletableFuture<XblData> authXstsMC(String xblToken) {
		String body = "{" +
			"    \"Properties\": {" +
			"        \"SandboxId\": \"RETAIL\"," +
			"        \"UserTokens\": [" +
			"            \"" + xblToken + "\"" +
			"        ]" +
			"    }," +
			"    \"RelyingParty\": \"rp://api.minecraftservices.com/\"," +
			"    \"TokenType\": \"JWT\"" +
			" }";
		return requestJson(HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body)).uri(URI.create(XBL_XSTS_AUTH_URL)).build())
			.thenApply(response -> new XblData(Instant.parse(response.get("IssueInstant").getAsString()), Instant.parse(response.get("NotAfter").getAsString()),
				response.get("Token").getAsString(), new XblData.DisplayClaims(response.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString())));
	}

	private CompletableFuture<MCXblData> authMC(String userhash, String xsts) {
		String body = "{\"identityToken\": \"XBL3.0 x=" + userhash + ";" + xsts + "\"\n}";
		return requestJson(HttpRequest.newBuilder(URI.create(MC_LOGIN_WITH_XBOX_URL)).POST(HttpRequest.BodyPublishers.ofString(body)).build())
			.thenApply(response -> new MCXblData(response.get("username").getAsString(),
				response.get("access_token").getAsString(),
				Instant.now().plus(response.get("expires_in").getAsLong(), ChronoUnit.SECONDS)));
	}

	private record MCXblData(String username, String accessToken, Instant expiration) {
	}

	private CompletableFuture<Boolean> checkOwnership(String accessToken) {
		return requestJson(HttpRequest
			.newBuilder(URI.create("https://api.minecraftservices.com/entitlements/mcstore"))
			.header("Authorization", "Bearer " + accessToken).build())
			.thenApply(res -> GsonHelper.jsonArrayToStream(res.get("items").getAsJsonArray())
				.anyMatch(e -> e.isJsonObject() && e.getAsJsonObject().has("name")
					&& "game_minecraft".equals(e.getAsJsonObject().get("name").getAsString())));
	}

	private CompletableFuture<JsonObject> getMCProfile(String accessToken) {
		return requestJson(HttpRequest.newBuilder().GET()
			.uri(URI.create("https://api.minecraftservices.com/minecraft/profile"))
			.header("Authorization", "Bearer " + accessToken).build());
	}

	public CompletableFuture<Optional<Account>> refreshToken(String token, Account account) {
		return CompletableFuture.supplyAsync(() -> {
			logger.debug("refreshing auth code... ");
			HttpRequest.Builder requestBuilder = HttpRequest
				.newBuilder(URI.create(MS_TOKEN_LOGIN_URL))
				.POST(FormBodyPublisher.newBuilder()
					.query("client_id", CLIENT_ID)
					.query("refresh_token", token)
					.query("scope", SCOPES)
					.query("grant_type", "refresh_token").build())
				.header("Accept", "application/json");

			JsonObject response = requestJson(requestBuilder.build()).join();

			if (response.has("error_codes")) {
				int errorCode = response.get("error_codes").getAsJsonArray().get(0).getAsInt();
				if (errorCode == 70000 || errorCode == 70012) {
					accounts.showAccountsExpiredScreen(account);
				} else {
					logger.warn("Login error, unexpected response: " + response);
					AxolotlClientCommon.getInstance().getNotificationProvider().addStatus("auth.notif.refresh.error", "auth.notif.refresh.error.unexpected_response");
				}
				return Optional.empty();
			}

			logger.debug("authenticating...");
			Optional<Account> opt = authenticateFromMSTokens(response.get("access_token").getAsString(),
				response.get("refresh_token").getAsString()).join();
			opt.ifPresent(refreshed -> {
				account.setRefreshToken(refreshed.getRefreshToken());
				account.setAuthToken(refreshed.getAuthToken());
				account.setName(refreshed.getName());
				account.setMsaToken(refreshed.getMsaToken());
				account.setExpiration(refreshed.getExpiration());
				accounts.save();
			});
			return opt;
		});
	}

	private CompletableFuture<JsonObject> requestJson(HttpRequest request) {
		return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
			.thenApply(res -> GsonHelper.fromJson(res.body()));
	}

	public CompletableFuture<MCProfile> getProfile(Account account) {
		return getMCProfile(account.getAuthToken()).thenApply(this::extractProfile);
	}

	private MCProfile extractProfile(JsonObject profileJson) {
		if (profileJson.has("error") && "NOT_FOUND".equals(profileJson.get("error").getAsString())) {
			throw new IllegalStateException("profile not found");
		}
		if (!profileJson.has("id")) {
			logger.warn("Unexpected profile response: {}", profileJson);
			throw new IllegalStateException("unexpected error");
		}
		return MCProfile.get(profileJson);
	}

	public CompletableFuture<MCProfile> setSkin(Account account, MCProfile.OnlineSkin skin) {
		record Body(String variant, String url) {
		}
		return requestJson(HttpRequest.newBuilder()
			.uri(URI.create("https://api.minecraftservices.com/minecraft/profile/skins"))
			.header("Authorization", "Bearer " + account.getAuthToken())
			.POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new Body(skin.variant(), skin.url())))).build())
			.thenApply(this::extractProfile);
	}

	public CompletableFuture<MCProfile> uploadAndSetSkin(Account account, Skin.Local skin) {
		try {
			return requestJson(HttpRequest.newBuilder()
				.uri(URI.create("https://api.minecraftservices.com/minecraft/profile/skins"))
				.header("Authorization", "Bearer " + account.getAuthToken())
				.POST(MultipartBodyPublisher.newBuilder()
					.textPart("variant", skin.isClassicVariant() ? "classic" : "slim")
					.filePart("file", skin.file(), MediaType.IMAGE_PNG).build()).build())
				.thenApply(this::extractProfile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public CompletableFuture<MCProfile> resetSkin(Account account) {
		return requestJson(HttpRequest.newBuilder().DELETE()
			.uri(URI.create("https://api.minecraftservices.com/minecraft/profile/skins/active"))
			.header("Authorization", "Bearer " + account.getAuthToken())
			.build())
			.thenApply(this::extractProfile);
	}

	public CompletableFuture<MCProfile> hideCape(Account account) {
		return requestJson(HttpRequest.newBuilder().DELETE()
			.uri(URI.create("https://api.minecraftservices.com/minecraft/profile/capes/active"))
			.header("Authorization", "Bearer " + account.getAuthToken())
			.build())
			.thenApply(this::extractProfile);
	}

	public CompletableFuture<MCProfile> showCape(Account account, MCProfile.OnlineCape cape) {
		record Body(String capeId) {
		}
		return requestJson(HttpRequest.newBuilder()
			.uri(URI.create("https://api.minecraftservices.com/minecraft/profile/capes/active"))
			.header("Authorization", "Bearer " + account.getAuthToken())
			.PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(new Body(cape.id())))).build())
			.thenApply(this::extractProfile);
	}
}
