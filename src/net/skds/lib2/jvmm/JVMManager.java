package net.skds.lib2.jvmm;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class JVMManager {

	public static final String JAVA_VERSION = "JAVA_VERSION";

	public static Map<String, String> getPropertyMap(String release) {
		int start = 0;
		HashMap<String, String> map = new HashMap<>();
		for (int end = release.indexOf('=', start); end != -1; end = release.indexOf('=', start)) {
			String key = release.substring(start, end);
			start = release.indexOf('"', end);
			if (start == -1) break;
			end = release.indexOf('"', start + 1);
			if (end == -1) break;
			String value = release.substring(start, end);
			map.put(key, value);
			start = release.indexOf('\n', end);
			if (start == -1) break;
		}
		return map;
	}

}
