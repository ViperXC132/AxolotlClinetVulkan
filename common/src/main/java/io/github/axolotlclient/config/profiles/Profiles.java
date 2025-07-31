package io.github.axolotlclient.config.profiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.hash.Hashing;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.bridge.util.AxoI18n;
import io.github.axolotlclient.util.GsonHelper;

public class Profiles {
	private static final Path PROFILES_CONFIG = AxolotlClientCommon.resolveConfigFile("profiles").resolve("profiles.json");

	public static Profiles getInstance() {
		return INSTANCE;
	}

	private static final Profiles INSTANCE = new Profiles();

	private ProfileStorage storage;

	public void loadProfiles() {
		if (Files.exists(PROFILES_CONFIG)) {
			try (var stream = Files.newBufferedReader(PROFILES_CONFIG)) {
				storage = GsonHelper.GSON.fromJson(stream, ProfileStorage.class);
			} catch (IOException e) {
				AxolotlClientCommon.getInstance().getLogger().warn("Failed to load profiles!", e);
			}
		} else {
			storage = new ProfileStorage();
			storage.current = newProfile("Default");
			saveProfiles();
			for (String name : new String[]{"axolotlclient.json", "custom_hud.json", "keystrokes.json"}) {
				var oldPath = AxolotlClientCommon.resolveConfigFile(name);
				var newPath = resolveProfileFile(name);
				try {
					Files.createDirectories(newPath.getParent());
					Files.move(oldPath, newPath);
				} catch (IOException e) {
					AxolotlClientCommon.getInstance().getLogger().warn("Failed to move {} to profile-based config path at {}", oldPath, newPath, e);
				}
			}
		}
	}

	public void saveProfiles() {
		try {
			Files.createDirectories(PROFILES_CONFIG.getParent());
			try (var stream = Files.newBufferedWriter(PROFILES_CONFIG)) {
				GsonHelper.GSON.toJson(storage, stream);
			}
		} catch (IOException e) {
			AxolotlClientCommon.getInstance().getLogger().warn("Failed to save profiles, falling back to 'default'", e);
		}
	}

	public void iterateAvailable(Consumer<Profile> action) {
		storage.available().forEach(action);
	}

	public void remove(Profile profile) {
		storage.available().remove(profile);
	}

	public void switchTo(Profile profile) {
		if (!storage.available().contains(profile)) {
			throw new IllegalArgumentException("Unknown profile!");
		}
		storage.current = profile;
		AxolotlClientCommon.getInstance().reloadConfig();
	}

	public Profile getCurrent() {
		return storage.current();
	}

	public Path resolveProfileFile(String path) {
		return getCurrent().getPath().resolve(path);
	}

	@SuppressWarnings("UnstableApiUsage")
	public Profile newProfile(String name) {
		var p = new Profile(name, Hashing.sha512().hashUnencodedChars(UUID.randomUUID().toString()).toString());
		storage.available().add(p);
		return p;
	}

	public Profile duplicate(Profile profile) {
		var duplicate = newProfile(AxoI18n.translate("profiles.duplicated", profile.name()));
		try {
			Files.copy(profile.getPath(), duplicate.getPath());
		} catch (IOException e) {
			AxolotlClientCommon.getInstance().getLogger().warn("Failed to duplicate profile!");
		}
		return duplicate;
	}

	public record Profile(String name, String id) {
		public Path getPath() {
			return AxolotlClientCommon.resolveConfigFile("profiles").resolve(id());
		}
	}

	@JsonAdapter(ProfileStorageLoader.class)
	public static final class ProfileStorage {
		private Profile current;
		private final List<Profile> available = new ArrayList<>();

		private ProfileStorage(Profile current, Collection<Profile> available) {
			this.current = current;
			this.available.addAll(available);
		}

		private ProfileStorage() {

		}

		public Profile current() {
			return current;
		}

		public List<Profile> available() {
			return available;
		}
	}

	public static class ProfileStorageLoader extends TypeAdapter<ProfileStorage> {

		@Override
		public void write(JsonWriter out, ProfileStorage value) throws IOException {
			if (value == null) {
				out.nullValue();
				return;
			}

			out.beginObject();
			out.name("current").value(value.current().id());
			out.name("available").beginArray();
			for (Profile entry : value.available()) {
				out.beginObject();
				out.name("name").value(entry.name());
				out.name("id").value(entry.id());
				out.endObject();
			}
			out.endArray();
			out.endObject();
		}

		@SuppressWarnings("unchecked")
		@Override
		public ProfileStorage read(JsonReader in) throws IOException {
			if (in.peek() != JsonToken.BEGIN_OBJECT) {
				return null;
			}

			Map<String, Object> obj = (Map<String, Object>) GsonHelper.read(in);
			if (obj == null) return null;
			var available = ((List<Map<String, String>>) obj.get("available")).stream()
				.map(e -> new Profile(e.get("name"), e.get("id"))).toList();
			Map<String, Profile> profiles = available.stream().collect(Collectors.toMap(Profile::id, Function.identity()));
			return new ProfileStorage(profiles.get((String) obj.get("current")), available);
		}
	}
}
