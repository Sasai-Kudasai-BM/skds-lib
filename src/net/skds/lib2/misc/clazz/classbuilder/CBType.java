package net.skds.lib2.misc.clazz.classbuilder;

import java.util.Arrays;
import java.util.List;

public record CBType(String name, String pack, int arrayDepth, List<String> genericPart) implements Comparable<CBType> {
	public CBType(String name, String pack) {
		this(name, pack, 0, null);
	}

	public static CBType of(Class<?> c) {
		int depth = 0;
		for (Class<?> c2 = c; c2.isArray(); c2 = c2.getComponentType()) {
			depth++;
		}
		return new CBType(c.getSimpleName(), c.getPackageName(), depth, null);
	}

	public static CBType of(Class<?> c, String... genericPart) {
		int depth = 0;
		for (Class<?> c2 = c; c2.isArray(); c2 = c2.getComponentType()) {
			depth++;
		}
		return new CBType(c.getSimpleName(), c.getPackageName(), depth, Arrays.asList(genericPart));
	}

	public String canonicalName() {
		return pack + "." + name;
	}

	public String arrayAppending() {
		if (arrayDepth == 0) return "";
		if (arrayDepth == 1) return "[]";
		return "[]".repeat(arrayDepth);
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
		return name + arrayAppending() + generic;
	}

	@Override
	public int compareTo(CBType o) {
		return canonicalName().compareTo(o.canonicalName());
	}
}
