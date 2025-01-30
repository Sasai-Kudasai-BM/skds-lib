package net.skds.lib2.utils;

import net.skds.lib2.io.json.JsonUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;

public interface AutoString {

	public static String build(Object object, String fields) {
		if (object == null) {
			return "null";
		}
		StringBuilder builder = new StringBuilder("{");

		builder.append("\"class\":\"");
		builder.append(object.getClass().getSimpleName());
		builder.append("\",");

		builder.append("\"hash\":");
		builder.append(object.hashCode());
		builder.append(",");

		builder.append("\"fields\":{");
		builder.append(fields);
		builder.append("}}");

		return builder.toString();
	}

	default String autoString() {
		return autoString(this);
	}

	static String autoString(Object object) {
		if (object == null) {
			return "null";
		}
		StringBuilder builder = new StringBuilder();
		Iterator<Field> iterator = Arrays.asList(object.getClass().getFields()).iterator();
		while (iterator.hasNext()) {
			Field field = iterator.next();

			builder.append("\"");
			builder.append(field.getName());
			builder.append("\":");
			Object value = null;
			try {
				value = field.get(object);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			if (value instanceof AutoString autoString) {
				builder.append(autoString.autoString());
			} else if (value == null) {
				builder.append("null");
			} else {
				builder.append(JsonUtils.toJsonCompact(value));
			}

			if (iterator.hasNext()) {
				builder.append(",");
			}
		}
		return build(object, builder.toString());
	}

	/*public static void main(String[] args) {
		System.out.println(new InnerAutoString().autoString());
	}

	public static class InnerAutoString implements AutoString {
		public String stringValue;
		public int intValue;
		public InnerAutoString_1 c = new InnerAutoString_1();
	}

	public static class InnerAutoString_1 implements AutoString {
		public boolean booleanValue = true;
		public InnerAutoString_2 test = new InnerAutoString_2();
	}

	public static class InnerAutoString_2 {
		public boolean c1 = true;
		public int c2 = 1;
		public String c3;
		public String[] array1;
		public String[] array2 = new String[]{"i","a"};
	}*/
}
