package io.github.axolotlclient.util;

import com.google.gson.JsonElement;

public final class JsonBuilders {
	public static final class JsonArray {
		private final com.google.gson.JsonArray arr = new com.google.gson.JsonArray();

		public static JsonArray create() {
			return new JsonArray();
		}

		private JsonArray() {
		}

		public JsonArray field(JsonElement ele) {
			arr.add(ele);
			return this;
		}

		public JsonArray field(JsonArray builder) {
			return field(builder.build());
		}

		public JsonArray field(JsonObject builder) {
			return field(builder.build());
		}

		public JsonArray field(Number prop) {
			arr.add(prop);
			return this;
		}

		public JsonArray field(String prop) {
			arr.add(prop);
			return this;
		}

		public JsonArray field(Boolean prop) {
			arr.add(prop);
			return this;
		}

		public JsonArray field(Character prop) {
			arr.add(prop);
			return this;
		}

		public com.google.gson.JsonArray build() {
			return arr;
		}

		public String asString() {
			return arr.toString();
		}
	}

	public static final class JsonObject {

		private final com.google.gson.JsonObject obj = new com.google.gson.JsonObject();

		public static JsonObject create() {
			return new JsonObject();
		}

		private JsonObject() {
		}

		public JsonObject field(String name, JsonElement ele) {
			obj.add(name, ele);
			return this;
		}

		public JsonObject field(String name, JsonObject builder) {
			return field(name, builder.build());
		}

		public JsonObject field(String name, JsonArray builder) {
			return field(name, builder.build());
		}

		public JsonObject field(String name, Number prop) {
			obj.addProperty(name, prop);
			return this;
		}

		public JsonObject field(String name, String prop) {
			obj.addProperty(name, prop);
			return this;
		}

		public JsonObject field(String name, Boolean prop) {
			obj.addProperty(name, prop);
			return this;
		}

		public JsonObject field(String name, Character prop) {
			obj.addProperty(name, prop);
			return this;
		}

		public com.google.gson.JsonObject build() {
			return obj;
		}

		public String asString() {
			return obj.toString();
		}
	}
}
