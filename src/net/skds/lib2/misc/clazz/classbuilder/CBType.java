package net.skds.lib2.misc.clazz.classbuilder;

import java.util.Arrays;
import java.util.List;

public record CBType(String name, String pack, List<String> genericPart) implements Comparable<CBType> {

	public CBType(String name, String pack, int arrayDepth, List<String> genericPart) {
		this(name + arrayAppending(arrayDepth), pack, genericPart);
	}

	public CBType(Class<?> c) {
		this(c.getSimpleName(), c.getPackageName(), null);
	}

	public CBType(Class<?> c, String... genericPart) {
		this(c.getSimpleName(), c.getPackageName(), Arrays.asList(genericPart));
	}

	public CBType(String name, String pack) {
		this(name, pack, null);
	}

	public static CBType of(Class<?> c) {
		return new CBType(c.getSimpleName(), c.getPackageName(), null);
	}

	public static CBType of(Class<?> c, String... genericPart) {
		return new CBType(c.getSimpleName(), c.getPackageName(), Arrays.asList(genericPart));
	}

	public String canonicalName() {
		return pack + "." + name;
	}

	private static String arrayAppending(int depth) {
		if (depth == 0) return "";
		if (depth == 1) return "[]";
		return "[]".repeat(depth);
	}

	public boolean imports() {
		return pack != null && !pack.equals("java.lang");// Character.isUpperCase(name.charAt(0));
	}

	@Override
	public String toString() {
		String generic = "";
		if (genericPart != null) {
			StringBuilder sb = new StringBuilder("<");
			for (String g : genericPart) {
				sb.append(g).append(", ");
			}
			if (!genericPart.isEmpty()) {
				sb.setLength(sb.length() - 2);
			}
			generic = sb.append(">").toString();
		}
		return name + generic;
	}

	@Override
	public int compareTo(CBType o) {
		return canonicalName().compareTo(o.canonicalName());
	}
}
