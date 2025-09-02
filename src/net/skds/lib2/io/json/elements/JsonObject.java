package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.codec.*;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.sosison.SosisonEntryType;
import net.skds.lib2.utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

public final class JsonObject extends HashMap<String, JsonElement> implements JsonElement {

	public JsonObject() {
	}

	public JsonObject(int initialSize) {
		super(initialSize);
	}

	@Override
	public JsonElementType type() {
		return JsonElementType.OBJECT;
	}

	@Override
	public JsonObject getAsJsonObject() {
		return this;
	}

	public JsonObject getAsJsonObjectOrNull(String key) {
		JsonElement element = this.get(key);
		if (element != null) {
			return element.getAsJsonObject();
		} else {
			return null;
		}
	}

	public JsonObject getAsJsonObjectOrNew(String key) {
		JsonElement element = this.get(key);
		if (element != null) {
			return element.getAsJsonObject();
		} else {
			return new JsonObject();
		}
	}

	public JsonObject getAsJsonObjectOr(String key, JsonObject or) {
		JsonElement element = this.get(key);
		if (element != null) {
			return element.getAsJsonObject();
		} else {
			return or;
		}
	}


	public JsonArray getAsJsonArrayOrNull(String key) {
		JsonElement element = this.get(key);
		if (element != null) {
			return element.getAsJsonArray();
		} else {
			return null;
		}
	}

	public JsonArray getAsJsonArrayOrNew(String key) {
		JsonElement element = this.get(key);
		if (element != null) {
			return element.getAsJsonArray();
		} else {
			return new JsonArray();
		}
	}

	public JsonArray getAsJsonArrayOr(String key, JsonArray or) {
		JsonElement element = this.get(key);
		if (element != null) {
			return element.getAsJsonArray();
		} else {
			return or;
		}
	}


	public String getAsJsonStringOrNull(String key) {
		JsonElement element = this.get(key);
		if (element != null) {
			return element.getAsString();
		} else {
			return null;
		}
	}

	public String getAsJsonStringOrNew(String key) {
		JsonElement element = this.get(key);
		if (element != null) {
			return element.getAsString();
		} else {
			return "";
		}
	}

	public String getAsJsonStringOr(String key, String or) {
		JsonElement element = this.get(key);
		if (element != null) {
			return element.getAsString();
		} else {
			return or;
		}
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		if (!isEmpty()) {
			forEach((k, v) -> sb.append(StringUtils.quote(k)).append(":").append(v).append(","));
			sb.setLength(sb.length() - 1);
		}
		sb.append('}');
		return sb.toString();
	}

	public JsonElement put(String key, Number value) {
		return super.put(key, new JsonNumber(value));
	}

	public JsonElement put(String key, boolean value) {
		return super.put(key, new JsonBoolean(value));
	}

	public JsonElement put(String key, CharSequence value) {
		return super.put(key, new JsonString(value.toString()));
	}

	public JsonElement putNull(String key) {
		return super.put(key, JsonElement.NULL);
	}

	@Override
	public JsonObject deepCopy() {
		JsonObject jsonObject = new JsonObject();
		for (Entry<String, JsonElement> entry : this.entrySet()) {
			jsonObject.put(entry.getKey(), entry.getValue().deepCopy());
		}
		return jsonObject;
	}

	public static class Codec extends AbstractCodec<JsonObject> {

		private final UniversalCodec<JsonElement> elementCodec;

		public Codec(Type type, UniversalCodecRegistry registry) {
			super(type, registry);
			this.elementCodec = registry.getCodecIndirect(JsonElement.class);
		}

		@Override
		public void write(JsonObject value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginObject();
			if (value.size() > 1) {
				writer.lineBreakEnable(true);
			}
			for (var me : value.entrySet()) {
				writer.writeName(me.getKey());
				elementCodec.write(me.getValue(), writer);
			}
			writer.endObject();
		}

		@Override
		public JsonObject read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					JsonObject jo = new JsonObject();
					while (reader.nextEntryType() != SosisonEntryType.END_OBJECT) {
						String name = reader.readName();
						JsonElement e;
						try {
							e = elementCodec.read(reader);
						} catch (Exception ex) {
							throw new ParseException("Exception while read " + name, ex);
						}
						jo.put(name, e);
					}
					reader.endObject();
					return jo;
				}
				default -> throw new ParseException("Unexpected token " + type);
			}
		}
	}
}
