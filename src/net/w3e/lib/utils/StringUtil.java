package net.w3e.lib.utils;

import net.sdteam.libmerge.Lib1Merge;

@Lib1Merge
public class StringUtil {

	public static final String quote(String string) {
		return '"' + string.replace("\"", "\\\"") + '"';
	}

	public static final String unquote(String string) {
		return string.substring(1, string.length() - 1).replace("\\\"", "\"");
	}

	/**
	 * "".repeat(count);
	 */
	@Deprecated
	public static final String stringOfNChar(int count, String c) {
		return c.repeat(count);
	}
}
