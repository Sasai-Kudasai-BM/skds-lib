package net.skds.lib2.natives.windows;

import net.skds.lib2.natives.AbstractLinkedLibrary;

import java.lang.invoke.MethodHandle;

import static net.skds.lib2.natives.SafeLinker.*;

public class Kernel32 extends AbstractLinkedLibrary {

	private static Kernel32 instance;

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
		Kernel32 inst = instance;
		if (inst == null) {
			try {
				inst = new Kernel32();
				instance = inst;
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
		return inst;
	}
}
