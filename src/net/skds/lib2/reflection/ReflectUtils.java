package net.skds.lib2.reflection;

import lombok.experimental.UtilityClass;
import net.skds.lib2.utils.function.MultiSupplier;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@UtilityClass
public class ReflectUtils {

	public static void forceInitialize(Class<?> cl) {
		try {
			Class.forName(cl.getName(), true, cl.getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Supplier<T> getConstructor(Class<T> tClass) {
		Constructor<?> c;
		try {
			c = tClass.getDeclaredConstructor();
			try {
				c.setAccessible(true);
			} catch (InaccessibleObjectException exception) {
				exception.printStackTrace(System.err);
				return null;
			}
		} catch (NoSuchMethodException e) {
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
					System.err.println("error with " + tClass + " " + c);
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
		fillInstanceFields(instance, instance.getClass(), function);
	}

	public static void fillInstanceFields(Object instance, Class<?> clazz, FillingFunction function) {
		for (Field f : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			try {
				function.accept(f, value -> {
					f.setAccessible(true);
					try {
						f.set(instance, value);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
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
		void accept(Field field, Consumer<Object> consumer) throws IllegalAccessException;
	}

	public static Field accessField(Class<?> cl, String key) {
		try {
			Field field = cl.getDeclaredField(key);
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static Class<?> getRawType(Type type) {
		if (type instanceof Class<?>) {
			// type is a normal class.
			return (Class<?>) type;

		} else if (type instanceof ParameterizedType parameterizedType) {
			// I'm not exactly sure why getRawType() returns Type instead of Class.
			// Neal isn't either but suspects some pathological case related
			// to nested classes exists.
			Type rawType = parameterizedType.getRawType();
			if (rawType instanceof Class cl) {
				return cl;
			} else {
				throw new IllegalArgumentException();
			}

		} else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			return Array.newInstance(getRawType(componentType), 0).getClass();

		} else if (type instanceof TypeVariable) {
			// we could use the variable's bounds, but that won't work if there are multiple.
			// having a raw type that's more general than necessary is okay
			return Object.class;

		} else if (type instanceof WildcardType) {
			Type[] bounds = ((WildcardType) type).getUpperBounds();
			// Currently the JLS only permits one bound for wildcards so using first bound is safe
			assert bounds.length == 1;
			return getRawType(bounds[0]);

		} else {
			String className = type == null ? "null" : type.getClass().getName();
			throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
					+ "GenericArrayType, but <" + type + "> is of type " + className);
		}
	}

	private static final MethodHandles.Lookup METHOD_LOOKUP = MethodHandles.lookup();

	public static MethodHandle getMethodHandle(Class<?> tClass, String methodName, Class<?>... args) {
		try {
			Method method = tClass.getDeclaredMethod(methodName, args);
			return METHOD_LOOKUP.unreflect(method);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static MethodHandle getMethodHandle(Method method) {
		try {
			return METHOD_LOOKUP.unreflect(method);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<ReflectSuperType> getReflectSuperTypes(Class<?> tClass) {
		return getReflectSuperTypes(tClass, Object.class);
	}

	public static List<ReflectSuperType> getReflectSuperTypes(Class<?> tClass, Class<?> typeTargetClass) {
		List<ReflectSuperType> classes = new ArrayList<>();

		while (true) {
			if (tClass != null) {
				AnnotatedType annotatedSuperclass = tClass.getAnnotatedSuperclass();
				if (annotatedSuperclass instanceof AnnotatedType annotatedParameterizedType) {
					Type type = annotatedParameterizedType.getType();
					if (type instanceof ParameterizedType parameterizedType) {
						try {
							if (typeTargetClass.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
								Type[] parameters = parameterizedType.getActualTypeArguments();
								if (parameters.length > 0) {
									classes.add(new ReflectSuperType(tClass, parameters));
								}
							}
						} catch (Exception ignored) {
						}
					}
					if (type instanceof Class<?> parameterizedType) {
						try {
							if (typeTargetClass.isAssignableFrom(parameterizedType)) {
								Type[] parameters = parameterizedType.getTypeParameters();
								if (parameters.length > 0) {
									classes.add(new ReflectSuperType(tClass, parameters));
								}
							}
						} catch (Exception ignored) {
						}
					}
				}
			}

			Class<?> s = tClass.getSuperclass();

			if (s == null) {
				if (!classes.isEmpty()) {
					break;
				}
				throw new IllegalArgumentException("Unable to create codec for non-canonical map declaration \"" + tClass + "\"");
			}

			tClass = s;
		}
		return classes;
	}

	public record ReflectSuperType(Class<?> cl, Type[] superParameters) {

		@Override
		public String toString() {
			return "ReflectSuperType{" +
					"cl=" + cl +
					", superParameters=" + Arrays.toString(superParameters) +
					'}';
		}
	}

}
