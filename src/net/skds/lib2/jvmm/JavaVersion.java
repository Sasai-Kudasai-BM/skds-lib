package net.skds.lib2.jvmm;

import java.util.Map;
import java.util.Objects;

public sealed interface JavaVersion {

	String versionName();

	JavaVersion JDK_25 = new JavaVersionImpl("25");

	static JavaVersion of(Map<String, String> properties) {
		return new JavaVersionImpl(Objects.requireNonNull(properties.get(JVMManager.JAVA_VERSION), "Invalid release properties"));
	}

	record JavaVersionImpl(String versionName) implements JavaVersion {

	}

}
