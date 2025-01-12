package net.skds.lib2.reflection;

import lombok.experimental.UtilityClass;
import net.skds.lib2.utils.function.MultiSupplier;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@UtilityClass
public class ReflectUtils {

	@SuppressWarnings("unchecked")
	public static <T> Supplier<T> getConstructor(Class<T> tClass) {
		Constructor<?> c = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(tClass);
		if (c == null) {
			try {
				c = tClass.getDeclaredConstructor();
			} catch (NoSuchMethodException e) {
				return null;
			}
		}
		try {
			c.setAccessible(true);
		} catch (InaccessibleObjectException exception) {
			exception.printStackTrace(System.err);
			return null;
		}
		Constructor<?> finalC = c;
		return () -> {
			try {
				return (T) finalC.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	public static <T> MultiSupplier<T> getMultiConstructor(Class<T> tClass, Class<?>... args) {
		try {
			Constructor<T> c = tClass.getDeclaredConstructor(args);
			c.setAccessible(true);
			return (arg) -> {
				try {
					return (T) c.newInstance(arg);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			};
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

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

	public static void fillInstanceFields(Object instance, FillingFunction function) {
		fillInstanceFields(instance, instance.getClass(), function);
	}

	public static void fillInstanceFields(Object instance, Class<?> clazz, FillingFunction function) {
		for (Field f : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(f.getModifiers())) {
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

	public static final Field accessField(Class<?> cl, String key) {
		try {
			Field field = cl.getDeclaredField(key);
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
