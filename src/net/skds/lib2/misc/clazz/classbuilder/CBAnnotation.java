package net.skds.lib2.misc.clazz.classbuilder;

import java.util.Map;

public record CBAnnotation(CBType type, Object value, Map<String, Object> values) {

	public CBAnnotation(Class<?> t) {
		this(CBType.of(t), null, null);
	}

	public CBAnnotation(Class<?> t, Object value) {
		this(CBType.of(t), value, null);
	}

	public CBAnnotation(CBType t) {
		this(t, null, null);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("@").append(type.name());
		if (value != null) {
			sb.append("(").append(value).append(")");
		} else if (values != null && !values.isEmpty()) {
			sb.append("(");
			values.forEach((k, v) -> sb.append(k).append(" = ").append(v).append(", "));
			sb.setLength(sb.length() - 2);
			sb.append(")");
		}
		return sb.toString();
	}
}
