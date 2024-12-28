package net.skds.lib2.io.json.codec;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Modifier;

@Getter
@Setter
public class JsonCodecOptions implements Cloneable {

	private DecorationType decorationType = DecorationType.FLAT;
	private JsonCapabilityVersion capabilityVersion = JsonCapabilityVersion.JSON;
	private int excludeFieldModifiers = Modifier.TRANSIENT | Modifier.STATIC;
	private boolean serializeNulls = true;
	private boolean deserializeNulls = false;
	private String tabulation = "\t";

	@Override
	public JsonCodecOptions clone() {
		JsonCodecOptions clone = new JsonCodecOptions();
		clone.decorationType = decorationType;
		clone.capabilityVersion = capabilityVersion;
		clone.excludeFieldModifiers = excludeFieldModifiers;
		clone.serializeNulls = serializeNulls;
		clone.deserializeNulls = deserializeNulls;
		clone.tabulation = tabulation;
		return clone;
	}

	public enum DecorationType {
		FLAT,
		FANCY;
	}
}
