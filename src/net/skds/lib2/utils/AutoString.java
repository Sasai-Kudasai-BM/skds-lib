package net.skds.lib2.utils;

import net.sdteam.libmerge.Lib1Merge;
import net.skds.lib2.utils.json.JsonUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;

@Lib1Merge
public interface AutoString {

	public default String autoString() {
		StringBuilder builder = new StringBuilder("{");

		builder.append("\"class\":\"");
		builder.append(this.getClass().getSimpleName());
		builder.append("\",");

		builder.append("\"hash\":");
		builder.append(this.hashCode());
		builder.append(",");

		builder.append("\"fields\":{");

		Iterator<Field> iterator = Arrays.asList(getClass().getFields()).iterator();
		while (iterator.hasNext()) {
			Field field = iterator.next();

			builder.append("\"");
			builder.append(field.getName());
			builder.append("\":");
			Object value = null;
			try {
				value = field.get(this);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			if (value instanceof AutoString autoString) {
				builder.append(autoString.autoString());
			} else {
				builder.append(JsonUtils.toJsonCompactNull(value));
			}

			if (iterator.hasNext()) {
				builder.append(",");
			}
		}
		builder.append("}}");

		return builder.toString();
	}

	static String autoString(Object object) {
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
			} else {
				builder.append(JsonUtils.toJsonCompactNull(value));
			}

			if (iterator.hasNext()) {
				builder.append(",");
			}
		}
		builder.append("}}");

		return builder.toString();
	}

	/*public static void main(String[] args) {
		System.out.println(new InnerAutoString().autoString());
	}

	public static class InnerAutoString implements AutoString {
		public String objectValue;
		public int doubleValue;
		public InnerAutoString_1 c = new InnerAutoString_1();
	}

	public static class InnerAutoString_1 implements AutoString {
		public boolean value = true;
		public InnerAutoString_2 test = new InnerAutoString_2();
	}

	public static class InnerAutoString_2 {
		public boolean c1 = true;
		public int c2 = 1;
		public String c3;
		public String[] array1;
		public String[] array2 = new String[]{"i","objectValue"};
	}*/
}
