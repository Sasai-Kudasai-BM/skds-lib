package net.skds.lib2.natives;

import lombok.CustomLog;
import lombok.experimental.UtilityClass;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/*
--enable-native-access=ALL-UNNAMED
 */
@CustomLog
@UtilityClass
@SuppressWarnings("unused")
public final class LinkerUtils {

	public static final boolean NATIVE_ORDER = ByteOrder.BIG_ENDIAN == ByteOrder.nativeOrder();
	public static final Arena ARENA = Arena.global();
	public static final Linker LINKER = Linker.nativeLinker();
	public static final SymbolLookup SYMBOL_LOOKUP = SymbolLookup.loaderLookup();
	public static final MethodHandles.Lookup METHOD_LOOKUP = MethodHandles.lookup();

	private static final Map<Class<?>, ValueLayout> primitiveLayouts = new HashMap<>();

	public static final ValueLayout.OfBoolean BOOLEAN = ValueLayout.JAVA_BOOLEAN;
	public static final ValueLayout.OfByte BYTE = ValueLayout.JAVA_BYTE;
	public static final ValueLayout.OfShort SHORT = ValueLayout.JAVA_SHORT;
	public static final ValueLayout.OfInt INT = ValueLayout.JAVA_INT;
	public static final ValueLayout.OfLong LONG = ValueLayout.JAVA_LONG;
	public static final ValueLayout.OfLong PTR = ValueLayout.JAVA_LONG;
	public static final ValueLayout.OfFloat FLOAT = ValueLayout.JAVA_FLOAT;
	public static final ValueLayout.OfDouble DOUBLE = ValueLayout.JAVA_DOUBLE;
	public static final ValueLayout VOID = null;

	public static final VarHandle BOOLEAN_HANDLE = BOOLEAN.varHandle();
	public static final VarHandle BYTE_HANDLE = BYTE.varHandle();
	public static final VarHandle SHORT_HANDLE = SHORT.varHandle();
	public static final VarHandle INT_HANDLE = INT.varHandle();
	public static final VarHandle LONG_HANDLE = LONG.varHandle();
	public static final VarHandle FLOAT_HANDLE = FLOAT.varHandle();
	public static final VarHandle DOUBLE_HANDLE = DOUBLE.varHandle();


	public static final TypeGlue G_BOOLEAN = new TypeGlue(ValueLayout.JAVA_BOOLEAN, boolean.class);
	public static final TypeGlue G_BYTE = new TypeGlue(ValueLayout.JAVA_BYTE, byte.class);
	public static final TypeGlue G_SHORT = new TypeGlue(ValueLayout.JAVA_SHORT, short.class);
	public static final TypeGlue G_INT = new TypeGlue(ValueLayout.JAVA_INT, int.class);
	public static final TypeGlue G_LONG = new TypeGlue(ValueLayout.JAVA_LONG, long.class);
	public static final TypeGlue G_PTR = new TypeGlue(ValueLayout.JAVA_LONG, long.class);
	public static final TypeGlue G_FLOAT = new TypeGlue(ValueLayout.JAVA_FLOAT, float.class);
	public static final TypeGlue G_DOUBLE = new TypeGlue(ValueLayout.JAVA_DOUBLE, double.class);
	public static final TypeGlue G_VOID = new TypeGlue(null, void.class);


	private static FunctionDescriptor fd(Class<?> returnType, Class<?>[] argTypes) {
		if (!returnType.isPrimitive()) throw new IllegalArgumentException("non-primitive return type " + returnType);
		ValueLayout[] args = new ValueLayout[argTypes.length];
		for (int i = 0; i < args.length; i++) {
			Class<?> a = argTypes[i];
			if (!a.isPrimitive()) throw new IllegalArgumentException("non-primitive argument type " + a);
			args[i] = primitiveLayouts.get(a);
		}
		if (returnType == void.class) {
			return FunctionDescriptor.ofVoid(args);
		} else {
			ValueLayout rt = primitiveLayouts.get(returnType);
			return FunctionDescriptor.of(rt, args);
		}
	}

	private static FunctionDescriptor fd(MemoryLayout returnType, MemoryLayout[] argTypes) {
		return returnType == null ? FunctionDescriptor.ofVoid(argTypes) : FunctionDescriptor.of(returnType, argTypes);
	}

	private static FunctionDescriptor fd(TypeGlue returnType, TypeGlue[] argTypes) {
		return returnType.nType == null ? FunctionDescriptor.ofVoid(nArray(argTypes)) : FunctionDescriptor.of(returnType.nType, nArray(argTypes));
	}

	public static SymbolLookup library(String libName) {
		return SymbolLookup.libraryLookup(libName, ARENA);
	}

	public static SymbolLookup library(Path libPath) {
		return SymbolLookup.libraryLookup(libPath, ARENA);
	}

	public static SymbolLookup library(String libName, Arena arena) {
		return SymbolLookup.libraryLookup(libName, arena);
	}

	public static SymbolLookup library(Path libPath, Arena arena) {
		return SymbolLookup.libraryLookup(libPath, arena);
	}

	public static MethodHandle createHandle(SymbolLookup library, String name, MemoryLayout returnType, MemoryLayout... arguments) {
		Optional<MemorySegment> op = library.find(name);
		if (op.isEmpty()) {
			log.warn("Can not find method " + name + fd(returnType, arguments));
			return null;
		}
		return LINKER.downcallHandle(
				op.get(),
				fd(returnType, arguments)
		);
	}

	public static MethodHandle createHandleCritical(SymbolLookup library, String name, boolean allowHeapAccess, MemoryLayout returnType, MemoryLayout... arguments) {
		Optional<MemorySegment> op = library.find(name);
		if (op.isEmpty()) {
			log.warn("Can not find method " + name + fd(returnType, arguments));
			return null;
		}
		return LINKER.downcallHandle(
				op.get(),
				fd(returnType, arguments),
				Linker.Option.critical(allowHeapAccess)
		);
	}

	/**
	 * @noinspection OptionalIsPresent
	 */
	public static MethodHandle createHandle(String name, MemoryLayout returnType, MemoryLayout... arguments) {
		Optional<MemorySegment> op = SYMBOL_LOOKUP.find(name);
		if (op.isEmpty()) {
			return null;
		}
		return LINKER.downcallHandle(
				op.get(),
				fd(returnType, arguments)
		);
	}


	public static <T> UpcallLink<T> createUpcallLink(Class<T> clazz, TypeGlue returnType, TypeGlue... argTypes) {
		return createUpcallLink(clazz, "call", returnType, argTypes);
	}

	public static <T> UpcallLink<T> createUpcallLink(Class<T> clazz, String name, TypeGlue returnType, TypeGlue... argTypes) {
		MethodHandle handle;
		try {
			Method method = clazz.getDeclaredMethod(name, jArray(argTypes));
			handle = METHOD_LOOKUP.unreflect(method);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return new UpcallLink<>(fd(returnType, argTypes), handle);
	}

	public static <T> UpcallLink<T> createUpcallLink(Class<T> clazz) {
		Method[] methods = clazz.getDeclaredMethods();
		if (methods.length == 0) throw new IllegalArgumentException(clazz + " is not a functional interface");
		for (Method method : methods) {
			if (!Modifier.isAbstract(method.getModifiers())) continue;
			FunctionDescriptor fd = fd(method.getReturnType(), method.getParameterTypes());
			try {
				MethodHandle handle = METHOD_LOOKUP.unreflect(method);
				return new UpcallLink<>(fd, handle);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		throw new IllegalArgumentException(clazz + " is not a functional interface");
	}

	private static Class<?>[] jArray(TypeGlue[] gt) {
		final Class<?>[] arr = new Class<?>[gt.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = gt[i].jType;
		}
		return arr;
	}

	private static ValueLayout[] nArray(TypeGlue[] gt) {
		final ValueLayout[] arr = new ValueLayout[gt.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = gt[i].nType;
		}
		return arr;
	}

	public record TypeGlue(ValueLayout nType, Class<?> jType) {
	}

	static {
		primitiveLayouts.put(byte.class, ValueLayout.JAVA_BYTE);
		primitiveLayouts.put(boolean.class, ValueLayout.JAVA_BOOLEAN);
		primitiveLayouts.put(short.class, ValueLayout.JAVA_SHORT);
		primitiveLayouts.put(char.class, ValueLayout.JAVA_CHAR);
		primitiveLayouts.put(int.class, ValueLayout.JAVA_INT);
		primitiveLayouts.put(float.class, ValueLayout.JAVA_FLOAT);
		primitiveLayouts.put(long.class, ValueLayout.JAVA_LONG);
		primitiveLayouts.put(double.class, ValueLayout.JAVA_DOUBLE);
	}

}
