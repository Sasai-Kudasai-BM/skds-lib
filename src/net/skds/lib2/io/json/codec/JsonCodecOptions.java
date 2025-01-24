package net.skds.lib2.io.json.codec;

import lombok.Getter;

import java.lang.reflect.Modifier;

@Getter
public class JsonCodecOptions implements Cloneable {

	private DecorationType decorationType = DecorationType.FLAT;
	private JsonCapabilityVersion capabilityVersion = JsonCapabilityVersion.JSON;
	private int excludeFieldModifiers = Modifier.TRANSIENT | Modifier.STATIC;
	//private boolean serializeNulls = true; //TODO
	//private boolean deserializeNulls = false;
	private String tabulation = "\t";

	@Override
	public JsonCodecOptions clone() {
		JsonCodecOptions clone = new JsonCodecOptions();
		clone.decorationType = decorationType;
		clone.capabilityVersion = capabilityVersion;
		clone.excludeFieldModifiers = excludeFieldModifiers;
		//clone.serializeNulls = serializeNulls;
		//clone.deserializeNulls = deserializeNulls;
		clone.tabulation = tabulation;
		return clone;
	}

	public JsonCodecOptions setDecorationType(DecorationType decorationType) {
		this.decorationType = decorationType;
		return this;
	}

	public JsonCodecOptions setCapabilityVersion(JsonCapabilityVersion capabilityVersion) {
		this.capabilityVersion = capabilityVersion;
		return this;
	}

	public JsonCodecOptions setExcludeFieldModifiers(int excludeFieldModifiers) {
		this.excludeFieldModifiers = excludeFieldModifiers;
		return this;
	}

	//public JsonCodecOptions setSerializeNulls(boolean serializeNulls) {
	//	this.serializeNulls = serializeNulls;
	//	return this;
	//}

	//public JsonCodecOptions setDeserializeNulls(boolean deserializeNulls) {
	//	this.deserializeNulls = deserializeNulls;
	//	return this;
	//}

	public JsonCodecOptions setTabulation(String tabulation) {
		this.tabulation = tabulation;
		return this;
	}

	public enum DecorationType {
		FLAT,
		FANCY;
	}
}
