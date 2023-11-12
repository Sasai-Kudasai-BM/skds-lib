package net.skds.lib.utils;

import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

	public static Map<String, String> queryToMap(String query) {
		if (query == null || query.isEmpty()) {
			return Map.of();
		}
		Map<String, String> map = new HashMap<>();
		String[] arr = query.split("&");
		for (int i = 0; i < arr.length; i++) {
			String val = arr[i];
			int pos = val.indexOf('=');
			if (pos != -1 && pos < val.length() - 1) {
				map.put(val.substring(0, pos), val.substring(pos + 1));
			}
		}
		return map;
	}
}
