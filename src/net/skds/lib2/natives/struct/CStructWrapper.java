package net.skds.lib2.natives.struct;

import net.skds.lib2.natives.MemoryAccess;
import net.skds.lib2.natives.struct.annotation.StructMember;
import net.skds.lib2.reflection.ReflectUtils;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class CStructWrapper<T extends WrappedCStruct> {

	private static final HashMap<Class<? extends WrappedCStruct>, CStructWrapper<? extends WrappedCStruct>> instances = new HashMap<>();

	private static final FieldLinker[] fle = {};
	private static final MemoryLayout[] mle = {};

	final MemoryLayout layout;
	private final FieldLinker[] fields;

	private final Supplier<T> constructor;

	public CStructWrapper(Class<T> c) {
		List<FieldLinker> linkers = new ArrayList<>();
		List<MemoryLayout> layouts = new LinkedList<>();
		collectFields(linkers, layouts, c);

		this.layout = MemoryLayout.structLayout(layouts.toArray(mle));
		this.fields = linkers.toArray(fle);
		var cst = ReflectUtils.getConstructor(c);
		if (cst == null) {
			throw new RuntimeException("Unable to get constructor for class \"" + c.getName() + "\"");
		}
		this.constructor = cst;
		synchronized (instances) {
			instances.put(c, this);
		}
	}

	public T alloc() {
		MemorySegment baseSegment = Arena.ofAuto().allocate(layout.byteSize(), layout.byteAlignment());
		return wrap(baseSegment, 0);
	}

	public T alloc(Arena arena) {
		MemorySegment baseSegment = arena.allocate(layout.byteSize(), layout.byteAlignment());
		return wrap(baseSegment, 0);
	}

	public void alloc(Arena arena, WrappedCStruct struct) {
		MemorySegment baseSegment = arena.allocate(layout.byteSize(), layout.byteAlignment());
		init(struct, baseSegment, 0);
	}

	public void alloc(WrappedCStruct struct) {
		MemorySegment baseSegment = Arena.ofAuto().allocate(layout.byteSize(), layout.byteAlignment());
		init(struct, baseSegment, 0);
	}

	public void init(WrappedCStruct str, MemorySegment baseSegment, long offset) {
		str.segment = baseSegment;
		str.offset = offset;
		try {
			for (int i = 0; i < fields.length; i++) {
				fields[i].init(str);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public T wrap(WrappedCStruct other) {
		T str = constructor.get();
		init(str, other.getSegment(), other.getOffset());
		return str;
	}

	public T wrap(MemorySegment baseSegment, long offset) {
		T str = constructor.get();
		init(str, baseSegment, offset);
		return str;
	}

	public void take(WrappedCStruct struct) {
		try {
			for (int i = 0; i < fields.length; i++) {
				fields[i].take(struct);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void put(WrappedCStruct struct) {
		try {
			for (int i = 0; i < fields.length; i++) {
				fields[i].put(struct);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static void collectFields(List<FieldLinker> linkers, List<MemoryLayout> layouts, Class<?> c) {
		if (!WrappedCStruct.class.isAssignableFrom(c)) {
			throw new RuntimeException(c + " is not an instance of WrappedCStruct");
		}
		var sc = c.getSuperclass();
		int offset = 0;
		if (sc != WrappedCStruct.class) {
			collectFields(linkers, layouts, sc);
			if (!linkers.isEmpty()) {
				FieldLinker last = linkers.getLast();
				offset = last.offset + last.getSize();
			}
		}
		for (Field f : c.getDeclaredFields()) {
			StructMember structMember = f.getAnnotation(StructMember.class);
			if (structMember != null) {
				offset = addLinker(linkers, layouts, f, structMember, offset);
			}
		}
	}

	private static ValueLayout getPrimitiveLayout(Class<?> fc, boolean isPointer) {
		if (isPointer) {
			if (fc == long.class) {
				//return ValueLayout.ADDRESS;
				return ValueLayout.JAVA_LONG;
			} else {
				throw new IllegalArgumentException("\"" + fc.getName() + "\" can not be pointer");
			}
		} else if (fc == int.class) {
			return ValueLayout.JAVA_INT;
		} else if (fc == byte.class) {
			return ValueLayout.JAVA_BYTE;
		} else if (fc == long.class) {
			return ValueLayout.JAVA_LONG;
		} else if (fc == float.class) {
			return ValueLayout.JAVA_FLOAT;
		} else if (fc == double.class) {
			return ValueLayout.JAVA_DOUBLE;
		} else if (fc == short.class) {
			return ValueLayout.JAVA_SHORT;
		} else if (fc == char.class) {
			return ValueLayout.JAVA_CHAR;
		}
		throw new IllegalArgumentException("WTF primitive \"" + fc.getName() + "\"");
	}

	@SuppressWarnings("unchecked")
	private static MemoryLayout getMemberElementLayout(Class<?> fc, StructMember sm) {
		if (WrappedCStruct.class.isAssignableFrom(fc)) {
			if (fc == WrappedCStruct.class) {
				throw new IllegalArgumentException("Struct member must be an inheritor of WrappedCStruct");
			}
			CStructWrapper<?> wrapper = forClass((Class<? extends WrappedCStruct>) fc);
			return wrapper.layout;
			//if (sm.size() == -1) {
			//	return wrapper.layout;
			//} else {
			//	return MemoryLayout.sequenceLayout(sm.size(), wrapper.layout);
			//}
		}
		if (fc.isArray() == (sm.size() == -1)) {
			throw new IllegalArgumentException("Struct member array definition conflict");
		}
		Class<?> pfc = fc;
		if (pfc.isArray()) {
			pfc = fc.getComponentType();
			if (pfc.isArray()) {
				throw new IllegalArgumentException("Struct member multi-dimensional arrays must be wrapped do 1-dimensional arrays");
			}
		}
		if (!pfc.isPrimitive()) {
			throw new IllegalArgumentException("Non-primitive struct members must be marked as structs");
		}
		return getPrimitiveLayout(pfc, sm.isPointer());
		//if (sm.size() == -1) {
		//	return vl;
		//} else {
		//	return MemoryLayout.sequenceLayout(sm.size(), vl);
		//}
	}

	private static int addLinker(List<FieldLinker> linkers, List<MemoryLayout> layouts, Field f, StructMember sm, int offset) {
		Class<?> fc = f.getType();
		FieldLinker linker = null;
		MemoryLayout layout = getMemberElementLayout(fc, sm);
		int padding = MemoryAccess.calcPadding(offset, layout.byteAlignment());
		if (padding > 0) {
			offset += padding;
			layouts.add(MemoryLayout.paddingLayout(padding));
		}
		if (fc.isPrimitive()) {
			ValueLayout vl = (ValueLayout) layout;
			if (fc == int.class) {
				linker = new PrimitiveFieldLinker(f, offset, vl) {
					@Override
					void take(WrappedCStruct struct) throws IllegalAccessException {
						int value = (int) handle.get(struct.getSegment(), offset + struct.getOffset());
						field.setInt(struct, value);
					}

					@Override
					void put(WrappedCStruct struct) throws IllegalAccessException {
						int value = field.getInt(struct);
						handle.set(struct.getSegment(), offset + struct.getOffset(), value);
					}
				};
			} else if (fc == long.class) {
				linker = new PrimitiveFieldLinker(f, offset, vl) {
					@Override
					void take(WrappedCStruct struct) throws IllegalAccessException {
						long value = (long) handle.get(struct.getSegment(), offset + struct.getOffset());
						field.setLong(struct, value);
					}

					@Override
					void put(WrappedCStruct struct) throws IllegalAccessException {
						long value = field.getLong(struct);
						handle.set(struct.getSegment(), offset + struct.getOffset(), value);
					}
				};
			} else if (fc == float.class) {
				linker = new PrimitiveFieldLinker(f, offset, vl) {
					@Override
					void take(WrappedCStruct struct) throws IllegalAccessException {
						float value = (float) handle.get(struct.getSegment(), offset + struct.getOffset());
						field.setFloat(struct, value);
					}

					@Override
					void put(WrappedCStruct struct) throws IllegalAccessException {
						float value = field.getFloat(struct);
						handle.set(struct.getSegment(), offset + struct.getOffset(), value);
					}
				};
			} else if (fc == double.class) {
				linker = new PrimitiveFieldLinker(f, offset, vl) {
					@Override
					void take(WrappedCStruct struct) throws IllegalAccessException {
						double value = (double) handle.get(struct.getSegment(), offset + struct.getOffset());
						field.setDouble(struct, value);
					}

					@Override
					void put(WrappedCStruct struct) throws IllegalAccessException {
						double value = field.getDouble(struct);
						handle.set(struct.getSegment(), offset + struct.getOffset(), value);
					}
				};
			} else if (fc == boolean.class) {
				linker = new PrimitiveFieldLinker(f, offset, vl) {
					@Override
					void take(WrappedCStruct struct) throws IllegalAccessException {
						boolean value = (boolean) handle.get(struct.getSegment(), offset + struct.getOffset());
						field.setBoolean(struct, value);
					}

					@Override
					void put(WrappedCStruct struct) throws IllegalAccessException {
						boolean value = field.getBoolean(struct);
						handle.set(struct.getSegment(), offset + struct.getOffset(), value);
					}
				};
			} else if (fc == byte.class) {
				linker = new PrimitiveFieldLinker(f, offset, vl) {
					@Override
					void take(WrappedCStruct struct) throws IllegalAccessException {
						byte value = (byte) handle.get(struct.getSegment(), offset + struct.getOffset());
						field.setByte(struct, value);
					}

					@Override
					void put(WrappedCStruct struct) throws IllegalAccessException {
						byte value = field.getByte(struct);
						handle.set(struct.getSegment(), offset + struct.getOffset(), value);
					}
				};
			} else if (fc == short.class) {
				linker = new PrimitiveFieldLinker(f, offset, vl) {
					@Override
					void take(WrappedCStruct struct) throws IllegalAccessException {
						short value = (short) handle.get(struct.getSegment(), offset + struct.getOffset());
						field.setShort(struct, value);
					}

					@Override
					void put(WrappedCStruct struct) throws IllegalAccessException {
						short value = field.getByte(struct);
						handle.set(struct.getSegment(), offset + struct.getOffset(), value);
					}
				};
			} else if (fc == char.class) {
				linker = new PrimitiveFieldLinker(f, offset, vl) {
					@Override
					void take(WrappedCStruct struct) throws IllegalAccessException {
						char value = (char) handle.get(struct.getSegment(), offset + struct.getOffset());
						field.setChar(struct, value);
					}

					@Override
					void put(WrappedCStruct struct) throws IllegalAccessException {
						char value = field.getChar(struct);
						handle.set(struct.getSegment(), offset + struct.getOffset(), value);
					}
				};
			}
		}
		if (fc.isArray()) {
			Class<?> comp = fc.componentType();
			if (WrappedCStruct.class.isAssignableFrom(comp)) {
				@SuppressWarnings("unchecked")
				CStructWrapper<?> wrapper = forClass((Class<? extends WrappedCStruct>) fc);
				linker = new StructArrayFieldLinker(f, sm.size(), offset, wrapper);
			} else {
				linker = new PrimitiveArrayFieldLinker(f, offset, sm.size(), fc.componentType(), (ValueLayout) layout);
			}
		}
		if (WrappedCStruct.class.isAssignableFrom(fc)) {
			@SuppressWarnings("unchecked")
			CStructWrapper<?> wrapper = forClass((Class<? extends WrappedCStruct>) fc);
			linker = new StructFieldLinker(f, offset, wrapper);
		}
		if (linker == null) {
			throw new IllegalArgumentException("Type is not supported \"" + fc.getName() + "\"");
		}
		linkers.add(linker);
		layouts.add(linker.layout);
		return (int) (offset + linker.layout.byteSize());
	}

	private abstract static class FieldLinker {

		final int offset;
		final MemoryLayout layout;
		final Field field;

		private FieldLinker(Field f, MemoryLayout layout, int offset) {
			this.field = f;
			this.layout = layout;
			this.offset = offset;
		}

		void init(WrappedCStruct struct) throws IllegalAccessException {
		}

		abstract int getSize();

		abstract int getAlignment();

		abstract void take(WrappedCStruct struct) throws IllegalAccessException;

		abstract void put(WrappedCStruct struct) throws IllegalAccessException;
	}

	private static class StructFieldLinker extends FieldLinker {

		final CStructWrapper<?> wrapper;

		private StructFieldLinker(Field f, int offset, CStructWrapper<?> wrapper) {
			super(f, wrapper.layout, offset);
			this.wrapper = wrapper;
		}

		@Override
		void init(WrappedCStruct struct) throws IllegalAccessException {
			WrappedCStruct str = wrapper.constructor.get();
			str.segment = struct.segment;
			str.offset = offset + struct.getOffset();
			field.set(struct, str);
		}

		@Override
		int getSize() {
			return (int) layout.byteSize();
		}

		@Override
		int getAlignment() {
			return (int) layout.byteAlignment();
		}

		@Override
		void take(WrappedCStruct struct) throws IllegalAccessException {
			WrappedCStruct str = (WrappedCStruct) field.get(struct);
			wrapper.take(str);
		}

		@Override
		void put(WrappedCStruct struct) throws IllegalAccessException {
			WrappedCStruct str = (WrappedCStruct) field.get(struct);
			wrapper.put(str);
		}
	}

	private abstract static class PrimitiveFieldLinker extends FieldLinker {

		final VarHandle handle;

		private PrimitiveFieldLinker(Field f, int offset, ValueLayout layout) {
			super(f, layout, offset);
			this.handle = layout.varHandle();
		}

		@Override
		int getSize() {
			return (int) layout.byteSize();
		}

		@Override
		int getAlignment() {
			return (int) layout.byteAlignment();
		}
	}

	private static class PrimitiveArrayFieldLinker extends FieldLinker {

		final ValueLayout valueLayout;
		//final VarHandle handle;
		final int length;
		final Class<?> aClass;

		private PrimitiveArrayFieldLinker(Field f, int offset, int length, Class<?> aClass, ValueLayout valueLayout) {
			super(f, MemoryLayout.sequenceLayout(length, valueLayout), offset);
			this.length = length;
			this.aClass = aClass;
			this.valueLayout = valueLayout;
			//this.handle = valueLayout.varHandle();
		}

		@Override
		void init(WrappedCStruct struct) throws IllegalAccessException {
			Object arr = Array.newInstance(aClass, length);
			field.set(struct, arr);
		}

		@Override
		int getSize() {
			return (int) layout.byteSize() * length;
		}

		@Override
		int getAlignment() {
			return (int) layout.byteAlignment();
		}

		@Override
		void take(WrappedCStruct struct) throws IllegalAccessException {
			Object array = field.get(struct);
			if (array == null) {
				array = Array.newInstance(aClass, length);
				field.set(struct, array);
			}
			MemorySegment.copy(struct.getSegment(), valueLayout, offset + struct.getOffset(), array, 0, length);
		}

		@Override
		void put(WrappedCStruct struct) throws IllegalAccessException {
			Object array = field.get(struct);
			if (array != null) {
				MemorySegment.copy(array, 0, struct.getSegment(), valueLayout, offset + struct.getOffset(), length);
			}
		}
	}

	private static class StructArrayFieldLinker extends FieldLinker {

		final long step;
		final CStructWrapper<?> wrapper;
		final int length;

		private StructArrayFieldLinker(Field f, int length, int offset, CStructWrapper<?> wrapper) {
			super(f, MemoryLayout.sequenceLayout(length, wrapper.layout), offset);
			this.length = length;
			this.wrapper = wrapper;
			this.step = wrapper.layout.byteSize() + MemoryAccess.calcPadding(wrapper.layout.byteSize(), wrapper.layout.byteAlignment());
		}

		@Override
		void init(WrappedCStruct struct) throws IllegalAccessException {
			Object arr = Array.newInstance(field.getType(), length);
			for (int i = 0; i < length; i++) {
				WrappedCStruct str = wrapper.constructor.get();
				str.segment = struct.segment;
				str.offset = offset + struct.getOffset() + i * step;
				Array.set(arr, i, str);
			}
			field.set(struct, arr);
		}

		@Override
		int getSize() {
			return (int) layout.byteSize() * length;
		}

		@Override
		int getAlignment() {
			return (int) layout.byteAlignment();
		}

		@Override
		void take(WrappedCStruct struct) throws IllegalAccessException {
			Object array = field.get(struct);
			for (int i = 0; i < length; i++) {
				WrappedCStruct struct2 = (WrappedCStruct) Array.get(array, i);
				wrapper.take(struct2);
			}
		}

		@Override
		void put(WrappedCStruct struct) throws IllegalAccessException {
			Object array = field.get(struct);
			for (int i = 0; i < length; i++) {
				WrappedCStruct struct2 = (WrappedCStruct) Array.get(array, i);
				wrapper.put(struct2);
			}
		}
	}


	@SuppressWarnings("unchecked")
	public static <T extends WrappedCStruct> CStructWrapper<T> forClass(Class<T> c) {
		synchronized (instances) {
			CStructWrapper<T> wrapper = (CStructWrapper<T>) instances.get(c);
			if (wrapper == null) {
				ReflectUtils.forceInitialize(c);
				wrapper = (CStructWrapper<T>) instances.get(c);
				if (wrapper == null) {
					throw new RuntimeException("WrappedCStruct classes must have initialized static field CStructWrapper. Or cyclic reference occur. Err in: " + c.getName());
				}
			}
			return wrapper;
		}
	}
}
