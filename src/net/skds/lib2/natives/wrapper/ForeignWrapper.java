package net.skds.lib2.natives.wrapper;

import net.skds.lib2.natives.LinkerUtils;
import net.skds.lib2.natives.wrapper.annotation.LibraryInfo;
import net.skds.lib2.natives.wrapper.annotation.LibraryName;
import net.skds.lib2.natives.wrapper.annotation.NativeName;
import net.skds.lib2.reflection.ReflectUtils;
import net.skds.lib2.utils.SKDSUtils;

import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.TypeKind;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ForeignWrapper {

	private static final String CLASS_SUFFIX = "_wrapped";
	private static final WeakHashMap<ClassLoader, WeakReference<WrapperClassLoader>> LOADERS = new WeakHashMap<>();
	private static final ConcurrentHashMap<Class<?>, Object> INSTANCES = new ConcurrentHashMap<>(8, .5f);

	public static <T> T wrap(Class<T> c) {
		return wrap(c, null, Arena.ofAuto());
	}

	@SuppressWarnings("unchecked")
	public static <T> T wrap(Class<T> c, Path libraryPath, Arena arena) {
		Object inst = INSTANCES.get(c);
		if (inst != null) {
			return (T) inst;
		}
		synchronized (c) {
			inst = INSTANCES.get(c);
			if (inst != null) {
				return (T) inst;
			}
			checkInterface(c);
			Class<T> wrapper = createWrapper(c);
			var constructor = Objects.requireNonNull(ReflectUtils.getConstructor(wrapper, SymbolLookup.class), "Invalid wrapper");
			SymbolLookup lookup = null;
			if (libraryPath != null) {
				lookup = SymbolLookup.libraryLookup(libraryPath, arena);
			} else {
				LibraryInfo libraryInfo = c.getAnnotation(LibraryInfo.class);
				SKDSUtils.OSType os = SKDSUtils.OS_TYPE;
				if (libraryInfo != null) for (LibraryName ln : libraryInfo.value()) {
					if (ln.os() == os) {
						lookup = SymbolLookup.libraryLookup(ln.value(), arena);
						break;
					}
				}
				if (lookup == null) {
					lookup = SymbolLookup.loaderLookup();
				}
			}
			T obj = constructor.apply(lookup);
			Object prev = INSTANCES.putIfAbsent(c, obj);
			if (prev != null) {
				return (T) prev;
			}
			return obj;
		}
	}

	@SuppressWarnings("unchecked")
	private static <T, W extends T> Class<W> createWrapper(Class<T> c) {
		String className = c.getName() + CLASS_SUFFIX;
		ClassDesc classDesc = ClassDesc.of(className);
		Builder builder = new Builder(c, classDesc);
		byte[] classBytes = ClassFile.of().build(classDesc, builder);
		return (Class<W>) getLoader(c).getClassFromBytes(className, classBytes);
	}

	private static WrapperClassLoader getLoader(Class<?> targetInterface) {
		ClassLoader parent = targetInterface.getClassLoader();
		if (parent == null) {
			parent = ClassLoader.getSystemClassLoader();
		}
		synchronized (LOADERS) {
			WeakReference<WrapperClassLoader> ref = LOADERS.get(parent);
			WrapperClassLoader loader = (ref != null) ? ref.get() : null;
			if (loader == null) {
				loader = new WrapperClassLoader(parent);
				LOADERS.put(parent, new WeakReference<>(loader));
			}
			return loader;
		}
	}

	private static final class Builder implements Consumer<ClassBuilder> {

		private static final ClassDesc LINKER_UTILS_CLASS = LinkerUtils.class.describeConstable().orElseThrow();
		private final ArrayList<NativeMethodSignature> methods = new ArrayList<>();
		private final Class<?> interfaceClass;
		private final ClassDesc classDesc;

		Builder(Class<?> cl, ClassDesc classDesc) {
			this.classDesc = classDesc;
			this.interfaceClass = cl;
			HashSet<Method> methods = new HashSet<>();
			HashSet<Class<?>> visited = new HashSet<>();
			visited.add(cl);
			collectMethods(cl, methods, visited);
			for (Method m : methods) {
				String jName = m.getName();
				String nName = jName;
				NativeName nn = m.getAnnotation(NativeName.class);
				if (nn != null) {
					nName = nn.value();
				}
				Class<?> rt = m.getReturnType();
				checkPrimitive(rt);
				Class<?>[] argsTypes = m.getParameterTypes();
				int pc = m.getParameterCount();
				for (int i = 0; i < pc; i++) {
					checkPrimitive(argsTypes[i]);
				}
				this.methods.add(new NativeMethodSignature(nName, jName, rt, argsTypes));
			}
		}

		static void collectMethods(Class<?> cl, Collection<Method> methods, Collection<Class<?>> visited) {
			final int modsMask = Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC;
			final int modsResult = Modifier.ABSTRACT | Modifier.PUBLIC;
			for (Class<?> c2 : cl.getInterfaces()) {
				if (visited.add(c2)) {
					collectMethods(c2, methods, visited);
				}
			}
			for (Method m : cl.getDeclaredMethods()) {
				if ((m.getModifiers() & modsMask) == modsResult) {
					methods.add(m);
				}
			}
		}

		private void getLayout(CodeBuilder code, Class<?> type) {
			var info = LinkerUtils.getLayoutInfo(type);
			code.getstatic(LINKER_UTILS_CLASS, info.name(), info.tClass().describeConstable().orElseThrow());
		}

		@Override
		public void accept(ClassBuilder builder) {
			final int fMods = Modifier.PRIVATE | Modifier.FINAL;
			ClassDesc mhc = MethodHandle.class.describeConstable().orElseThrow();
			ClassDesc mlc = MemoryLayout.class.describeConstable().orElseThrow();
			ClassDesc slc = SymbolLookup.class.describeConstable().orElseThrow();
			ClassDesc linkerUtilsCD = LinkerUtils.class.describeConstable().orElseThrow();

			MethodTypeDesc createHandleMTD = MethodTypeDesc.of(
					mhc,
					slc,
					String.class.describeConstable().orElseThrow(),
					mlc,
					mlc.arrayType()
			);

			builder.withFlags(Modifier.PUBLIC | Modifier.FINAL);
			builder.withInterfaceSymbols(interfaceClass.describeConstable().orElseThrow());
			builder.withVersion(ClassFile.JAVA_25_VERSION, 0);

			builder.withMethod("<init>",
					MethodTypeDesc.of(
							ConstantDescs.CD_void,
							slc
					),
					Modifier.PRIVATE,
					m -> m.withCode(code -> {
						// super
						code.aload(code.receiverSlot());
						code.invokespecial(
								Object.class.describeConstable().orElseThrow(),
								"<init>",
								ConstantDescs.MTD_void
						);
						// init fields
						for (NativeMethodSignature nms : this.methods) {
							code.aload(code.receiverSlot()); // this for later store
							code.aload(1); // lookup
							code.ldc(nms.nativeName); // name
							getLayout(code, nms.returnType); // return type
							// new array
							code.loadConstant(nms.arguments.length);
							code.anewarray(mlc);
							// fill array
							for (int i = 0; i < nms.arguments.length; i++) {
								code.dup();// dup array to keep using
								code.loadConstant(i);// load index
								getLayout(code, nms.arguments[i]);// load value
								code.aastore();// store value to array
							}
							code.invokestatic(linkerUtilsCD, "createHandle", createHandleMTD);
							code.putfield(classDesc, nms.handleFieldName(), mhc);
						}
						code.return_();
					})
			);

			ClassDesc rteDesc = RuntimeException.class.describeConstable().orElseThrow();
			for (NativeMethodSignature nms : this.methods) {
				builder.withField(nms.handleFieldName(), mhc, fMods);
				ClassDesc[] args = new ClassDesc[nms.arguments.length];
				for (int i = 0; i < nms.arguments.length; i++) {
					Class<?> argC = nms.arguments[i];
					args[i] = argC.describeConstable().orElseThrow();
				}
				ClassDesc rt = nms.returnType.describeConstable().orElseThrow();
				MethodTypeDesc mtd = MethodTypeDesc.of(
						rt,
						args
				);
				builder.withMethod(
						nms.javaName,
						mtd
						, Modifier.PUBLIC | Modifier.FINAL,
						m -> m.withCode(code -> code.trying(tr -> {
							// get handle
							tr.aload(code.receiverSlot());
							tr.getfield(classDesc, nms.handleFieldName(), mhc);
							// load args
							for (int i = 0, l = nms.arguments.length; i < l; i++) {
								Class<?> argC = nms.arguments[i];
								TypeKind tk = TypeKind.fromDescriptor(argC.descriptorString());
								int slot = code.parameterSlot(i);
								tr.loadLocal(tk, slot);
							}
							// invoke
							tr.invokevirtual(mhc, "invokeExact", mtd);
							// return
							tr.return_(TypeKind.fromDescriptor(rt.descriptorString()));
						}, cat -> cat.catchingAll(cCode -> {
									// save Throwable
									int store = cCode.allocateLocal(TypeKind.REFERENCE);
									cCode.astore(store);
									// new RuntimeException
									cCode.new_(rteDesc);
									cCode.dup();
									cCode.aload(store);
									cCode.invokespecial(rteDesc,
											"<init>",
											MethodTypeDesc.of(
													ConstantDescs.CD_void,
													Throwable.class.describeConstable().orElseThrow()
											)
									);
									cCode.athrow();
								}
						))));
			}
		}

	}

	private static void checkPrimitive(Class<?> c) {
		if (!c.isPrimitive()) {
			throw new IllegalArgumentException("Only primitive types supported but got " + c);
		}
	}

	private record NativeMethodSignature(
			String nativeName,
			String javaName,
			Class<?> returnType,
			Class<?>[] arguments
	) {
		String handleFieldName() {
			if (arguments.length == 0) {
				return javaName;
			}
			StringBuilder sb = new StringBuilder(javaName).append("$");
			for (Class<?> c : arguments) {
				sb.append(c.descriptorString());
			}
			return sb.toString();
		}
	}

	private static final class WrapperClassLoader extends ClassLoader {

		WrapperClassLoader(ClassLoader parent) {
			super("ForeignWrapperClassloader", parent);
		}

		public Class<?> getClassFromBytes(String className, byte[] classBytes) {
			return defineClass(className, classBytes, 0, classBytes.length);
		}
	}

	private static void checkInterface(Class<?> c) {
		if (!c.isInterface()) throw new IllegalArgumentException("Target class must be an interface " + c);
	}
}
