package net.skds.lib2.utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.skds.lib.mat.Vec3;

import java.util.ArrayDeque;
import java.util.Deque;

public class JsonConveyor {

	private Deque<JsonElement> conv = new ArrayDeque<>();
	private JsonElement last;

	public JsonConveyor(byte[] text) {
		last = JsonParser.parseString(new String(text));
	}

	public JsonConveyor(JsonElement json) {
		last = json;
	}

	public void popPush(String key) {
		pop();
		push(key);
	}

	public void push(String key) {
		conv.add(last);
		last = last.getAsJsonObject().get(key);
	}

	public void pop() {
		if (conv.isEmpty()) {
			throw new RuntimeException("Stack underflow");
		}
		last = conv.pollLast();
	}

	public void pushIndex(int i) {
		conv.add(last);
		last = last.getAsJsonArray().get(i);
	}

	public int len() {
		return last.getAsJsonArray().size();
	}

	public boolean contains(String key) {
		return last.getAsJsonObject().has(key);
	}

	public boolean isCompound() {
		return last.isJsonObject();
	}

	public boolean isArray() {
		return last.isJsonArray();
	}

	public boolean isPrimitive() {
		return last.isJsonPrimitive();
	}

	// or
	public Vec3 getVec3or(String key, Vec3 def) {
		var ob = last.getAsJsonObject().get(key);
		if (ob == null) {
			return def;
		}
		var ar = ob.getAsJsonArray();
		return new Vec3(ar.get(0).getAsDouble(), ar.get(1).getAsDouble(), ar.get(2).getAsDouble());
	}

	public String getStringOr(String key, String def) {
		var ob = last.getAsJsonObject().get(key);
		if (ob == null) {
			return def;
		}
		return ob.getAsString();
	}

	public byte getByteOr(String key, byte def) {
		var ob = last.getAsJsonObject().get(key);
		if (ob == null) {
			return def;
		}
		return ob.getAsByte();
	}

	public short getShortOr(String key, short def) {
		var ob = last.getAsJsonObject().get(key);
		if (ob == null) {
			return def;
		}
		return ob.getAsShort();
	}

	public int getIntOr(String key, int def) {
		var ob = last.getAsJsonObject().get(key);
		if (ob == null) {
			return def;
		}
		return ob.getAsInt();
	}

	public long getLongOr(String key, long def) {
		var ob = last.getAsJsonObject().get(key);
		if (ob == null) {
			return def;
		}
		return ob.getAsLong();
	}

	public boolean getBooleanOr(String key, boolean def) {
		var ob = last.getAsJsonObject().get(key);
		if (ob == null) {
			return def;
		}
		return ob.getAsBoolean();
	}

	public float getFloatOr(String key, float def) {
		var ob = last.getAsJsonObject().get(key);
		if (ob == null) {
			return def;
		}
		return ob.getAsFloat();
	}

	public double getDoubleOr(String key, double def) {
		var ob = last.getAsJsonObject().get(key);
		if (ob == null) {
			return def;
		}
		return ob.getAsDouble();
	}

	// or
	public Vec3 getVec3(String key) {
		var ar = last.getAsJsonObject().get(key).getAsJsonArray();
		return new Vec3(ar.get(0).getAsDouble(), ar.get(1).getAsDouble(), ar.get(2).getAsDouble());
	}

	public String getString(String key) {
		return last.getAsJsonObject().get(key).getAsString();
	}

	public byte getByte(String key) {
		return last.getAsJsonObject().get(key).getAsByte();
	}

	public short getShort(String key) {
		return last.getAsJsonObject().get(key).getAsShort();
	}

	public int getInt(String key) {
		return last.getAsJsonObject().get(key).getAsInt();
	}

	public long getLong(String key) {
		return last.getAsJsonObject().get(key).getAsLong();
	}

	public boolean getBoolean(String key) {
		return last.getAsJsonObject().get(key).getAsBoolean();
	}

	public float getFloat(String key) {
		return last.getAsJsonObject().get(key).getAsFloat();
	}

	public double getDouble(String key) {
		return last.getAsJsonObject().get(key).getAsDouble();
	}

	public String[] getStringArray(String key) {
		JsonArray arr = last.getAsJsonObject().get(key).getAsJsonArray();
		String[] arr2 = new String[arr.size()];
		for (int i = 0; i < arr2.length; i++) {
			arr2[i] = arr.get(i).getAsString();
		}
		return arr2;
	}

	public byte[] getByteArray(String key) {
		JsonArray arr = last.getAsJsonObject().get(key).getAsJsonArray();
		byte[] arr2 = new byte[arr.size()];
		for (int i = 0; i < arr2.length; i++) {
			arr2[i] = arr.get(i).getAsByte();
		}
		return arr2;
	}

	public short[] getShortArray(String key) {
		JsonArray arr = last.getAsJsonObject().get(key).getAsJsonArray();
		short[] arr2 = new short[arr.size()];
		for (int i = 0; i < arr2.length; i++) {
			arr2[i] = arr.get(i).getAsShort();
		}
		return arr2;
	}

	public int[] getIntArray(String key) {
		JsonArray arr = last.getAsJsonObject().get(key).getAsJsonArray();
		int[] arr2 = new int[arr.size()];
		for (int i = 0; i < arr2.length; i++) {
			arr2[i] = arr.get(i).getAsInt();
		}
		return arr2;
	}

	public long[] getLongArray(String key) {
		JsonArray arr = last.getAsJsonObject().get(key).getAsJsonArray();
		long[] arr2 = new long[arr.size()];
		for (int i = 0; i < arr2.length; i++) {
			arr2[i] = arr.get(i).getAsLong();
		}
		return arr2;
	}

	public boolean[] getBooleanArray(String key) {
		JsonArray arr = last.getAsJsonObject().get(key).getAsJsonArray();
		boolean[] arr2 = new boolean[arr.size()];
		for (int i = 0; i < arr2.length; i++) {
			arr2[i] = arr.get(i).getAsBoolean();
		}
		return arr2;
	}

	public float[] getFloatArray(String key) {
		JsonArray arr = last.getAsJsonObject().get(key).getAsJsonArray();
		float[] arr2 = new float[arr.size()];
		for (int i = 0; i < arr2.length; i++) {
			arr2[i] = arr.get(i).getAsFloat();
		}
		return arr2;
	}

	public double[] getDoubleArray(String key) {
		JsonArray arr = last.getAsJsonObject().get(key).getAsJsonArray();
		double[] arr2 = new double[arr.size()];
		for (int i = 0; i < arr2.length; i++) {
			arr2[i] = arr.get(i).getAsDouble();
		}
		return arr2;
	}

	public JsonElement last() {
		return last;
	}

	public JsonObject asJsonObject() {
		return last.getAsJsonObject();
	}
}
