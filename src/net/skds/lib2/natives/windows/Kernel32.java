package net.skds.lib2.natives.windows;

import net.skds.lib2.natives.AbstractLinkedLibrary;

import java.lang.invoke.MethodHandle;

import static net.skds.lib2.natives.LinkerUtils.*;

@SuppressWarnings("DataFlowIssue")
public class Kernel32 extends AbstractLinkedLibrary {
	
	private static final class Holder {
		private static final Kernel32 INSTANCE = new Kernel32();
	}

	private final MethodHandle getModuleHandle = createHandle(lib, "GetModuleHandleW", PTR, PTR);
	private final MethodHandle getLastError = createHandle(lib, "GetLastError", INT);

	private Kernel32() {
		super("kernel32");
	}

	public int getLastError() {
		try {
			return (int) getLastError.invokeExact();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public long getModuleHandle(long ptr) {
		try {
			return (long) getModuleHandle.invokeExact(ptr);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static Kernel32 getInstance() {
		return Holder.INSTANCE;
	}
}
