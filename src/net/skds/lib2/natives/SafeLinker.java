package net.skds.lib2.natives;

import lombok.experimental.UtilityClass;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.nio.ByteOrder;
import java.nio.file.Path;

/*
--enable-native-access=ALL-UNNAMED
 */
@UtilityClass
@SuppressWarnings("unused")
public final class SafeLinker {


	public static final boolean NATIVE_ORDER = ByteOrder.BIG_ENDIAN == ByteOrder.nativeOrder();
	public static final Arena ARENA = Arena.global();
	public static final Linker LINKER = Linker.nativeLinker();
	public static final SymbolLookup SYMBOL_LOOKUP = SymbolLookup.loaderLookup();
	public static final MethodHandles.Lookup METHOD_LOOKUP = MethodHandles.lookup();

	public static final MemoryLayout BOOLEAN = ValueLayout.JAVA_BOOLEAN;
	public static final MemoryLayout BYTE = ValueLayout.JAVA_BYTE;
	public static final MemoryLayout SHORT = ValueLayout.JAVA_SHORT;
	public static final MemoryLayout INT = ValueLayout.JAVA_INT;
	public static final MemoryLayout LONG = ValueLayout.JAVA_LONG;
	public static final MemoryLayout PTR = ValueLayout.JAVA_LONG;
	public static final MemoryLayout FLOAT = ValueLayout.JAVA_FLOAT;
	public static final MemoryLayout DOUBLE = ValueLayout.JAVA_DOUBLE;
	public static final MemoryLayout VOID = null;

	public static final TypeGlue G_BOOLEAN = new TypeGlue(ValueLayout.JAVA_BOOLEAN, boolean.class);
	public static final TypeGlue G_BYTE = new TypeGlue(ValueLayout.JAVA_BYTE, byte.class);
	public static final TypeGlue G_SHORT = new TypeGlue(ValueLayout.JAVA_SHORT, short.class);
	public static final TypeGlue G_INT = new TypeGlue(ValueLayout.JAVA_INT, int.class);
	public static final TypeGlue G_LONG = new TypeGlue(ValueLayout.JAVA_LONG, long.class);
	public static final TypeGlue G_PTR = new TypeGlue(ValueLayout.JAVA_LONG, long.class);
	public static final TypeGlue G_FLOAT = new TypeGlue(ValueLayout.JAVA_FLOAT, float.class);
	public static final TypeGlue G_DOUBLE = new TypeGlue(ValueLayout.JAVA_DOUBLE, double.class);
	public static final TypeGlue G_VOID = new TypeGlue(null, void.class);

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

	public static MethodHandle createHandle(SymbolLookup library, String name, MemoryLayout returnType, MemoryLayout... arguments) {
		return LINKER.downcallHandle(
				library.find(name).orElseThrow(),
				fd(returnType, arguments)
		);
	}

	public static MethodHandle createHandle(String name, MemoryLayout returnType, MemoryLayout... arguments) {

		return LINKER.downcallHandle(
				SYMBOL_LOOKUP.find(name).orElseThrow(),
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

}
