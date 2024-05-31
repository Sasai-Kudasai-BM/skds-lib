package net.skds.lib.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ReflectUtils {

	public static <T> HiddenField<T> getField(Class<?> clazz, FindOptions options) {

		final Field[] fields = clazz.getDeclaredFields();
		int currentOrdinal = 0;
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			if (options.test(f)) {
				if (currentOrdinal >= options.getOrdinal()) {
					f.setAccessible(true);
					return new HiddenField<>(f);
				} else {
					currentOrdinal++;
				}
			}
		}
		throw new RuntimeException("No field found");
	}

	public static <T> Map<String, HiddenField<T>> getAllFields(Class<?> clazz, FindOptions options) {

		Map<String, HiddenField<T>> map = new HashMap<>();

		final Field[] fields = clazz.getDeclaredFields();
		int currentOrdinal = 0;
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			if (options.test(f)) {
				if (currentOrdinal >= options.getOrdinal() || options.getOrdinal() == -1) {
					f.setAccessible(true);
					map.put(f.getName(), new HiddenField<>(f));
				} else {
					currentOrdinal++;
				}
			}
		}
		return map;
	}

	public static void fillInstanceFields(Object instance, FillingFunction function) {

		for (Field f : instance.getClass().getDeclaredFields()) {
			if ((f.getModifiers() & Modifier.STATIC) != 0) {
				continue;
			}
			function.accept(f, value -> {
				f.setAccessible(true);
				try {
					f.set(instance, value);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}
	}

	public static Type extractFieldType(Class<?> clazz, String fieldName) {
		try {
			return clazz.getDeclaredField(fieldName).getGenericType();
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	@FunctionalInterface
	public interface FillingFunction {
		public void accept(Field field, Consumer<Object> consumer);
	}
}
