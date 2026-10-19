package net.skds.lib2.io.codec;

import net.skds.lib2.io.json.JsonCodecOptions;
import java.lang.reflect.Modifier;

public class UniversalCodecOptionsImpl implements UniversalCodecOptions {
	// common
	private int excludeFieldModifiers = Modifier.TRANSIENT | Modifier.STATIC;
	// json
	private DecorationType decorationType = DecorationType.FLAT;
	private JsonCapabilityVersion capabilityVersion = JsonCapabilityVersion.JSON;
	private String tabulation = "\t";
	// sosison
	private boolean sosisonComments = false;

	@Override
	public UniversalCodecOptions clone() {
		UniversalCodecOptionsImpl clone = new UniversalCodecOptionsImpl();
		clone.decorationType = decorationType;
		clone.capabilityVersion = capabilityVersion;
		clone.excludeFieldModifiers = excludeFieldModifiers;
		clone.tabulation = tabulation;
		clone.sosisonComments = sosisonComments;
		return clone;
	}

	@Override
	public UniversalCodecOptionsImpl setDecorationType(DecorationType decorationType) {
		this.decorationType = decorationType;
		return this;
	}

	@Override
	public UniversalCodecOptionsImpl setCapabilityVersion(JsonCapabilityVersion capabilityVersion) {
		this.capabilityVersion = capabilityVersion;
		return this;
	}

	@Override
	public UniversalCodecOptionsImpl setExcludeFieldModifiers(int excludeFieldModifiers) {
		this.excludeFieldModifiers = excludeFieldModifiers;
		return this;
	}

	@Override
	public UniversalCodecOptionsImpl setTabulation(String tabulation) {
		this.tabulation = tabulation;
		return this;
	}

	@Override
	public int getExcludeFieldModifiers() {
		return this.excludeFieldModifiers;
	}

	@Override
	public DecorationType getDecorationType() {
		return this.decorationType;
	}

	@Override
	public JsonCapabilityVersion getCapabilityVersion() {
		return this.capabilityVersion;
	}

	@Override
	public String getTabulation() {
		return this.tabulation;
	}
	//@Override
	//public boolean sosisonComments() {
	//	return sosisonComments;
	//}
	//@Override
	//public SosisonCodecOptions setSosisonComments(boolean enabled) {
	//	this.sosisonComments = enabled;
	//	return this;
	//}
}
