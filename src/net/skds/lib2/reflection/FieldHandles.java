package net.skds.lib2.reflection;

import lombok.experimental.UtilityClass;
import net.skds.lib2.utils.function.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;

@UtilityClass
public class FieldHandles {

	@SuppressWarnings("unchecked")
	public static <C, F> Function<C, F> objGetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return o -> (F) vh.get(o);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C, F> BiConsumer<C, F> objSetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return vh::set;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> Object2IntFunction<C> intGetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return o -> (int) vh.get(o);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> IntBiConsumer<C> intSetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return vh::set;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> Object2BooleanFunction<C> booleanGetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return o -> (boolean) vh.get(o);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> BooleanBiConsumer<C> booleanSetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return vh::set;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> Object2ByteFunction<C> byteGetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return o -> (byte) vh.get(o);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> ByteBiConsumer<C> byteSetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return vh::set;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> Object2ShortFunction<C> shortGetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return o -> (short) vh.get(o);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> ShortBiConsumer<C> shortSetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return vh::set;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> Object2CharFunction<C> charGetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return o -> (char) vh.get(o);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> CharBiConsumer<C> charSetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return vh::set;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> Object2LongFunction<C> longGetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return o -> (long) vh.get(o);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> LongBiConsumer<C> longSetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return vh::set;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> Object2FloatFunction<C> floatGetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return o -> (float) vh.get(o);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> FloatBiConsumer<C> floatSetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return vh::set;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> Object2DoubleFunction<C> doubleGetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return o -> (double) vh.get(o);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static <C> DoubleBiConsumer<C> doubleSetter(Field f) {
		try {
			f.setAccessible(true);
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup());
			VarHandle vh = lookup.unreflectVarHandle(f);
			return vh::set;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
