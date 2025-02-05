package net.skds.lib2.natives.windows;

import net.skds.lib2.natives.AbstractLinkedLibrary;
import net.skds.lib2.natives.SafeLinker;
import net.skds.lib2.natives.UpcallLink;

import java.lang.invoke.MethodHandle;

import static net.skds.lib2.natives.SafeLinker.*;

public class User32 extends AbstractLinkedLibrary {

	private static User32 instance;

	public final UpcallLink<LowLevelKeyboardProc> lowLevelKeyboardProcUL = SafeLinker.createUpcallLink(
			User32.LowLevelKeyboardProc.class,
			SafeLinker.G_INT,
			SafeLinker.G_INT,
			SafeLinker.G_INT,
			SafeLinker.G_LONG
	);

	private final MethodHandle waitMessage = createHandle(lib, "WaitMessage", BOOLEAN);
	private final MethodHandle getMessage = createHandle(lib, "GetMessageW", BOOLEAN, PTR, PTR, INT, INT);
	private final MethodHandle setWindowsHookExA = createHandle(lib, "SetWindowsHookExA", PTR, INT, PTR, PTR, INT);
	private final MethodHandle unhookWindowsHookEx = createHandle(lib, "UnhookWindowsHookEx", BOOLEAN, PTR);

	private User32() {
		super("user32");
	}

	public long setWindowsHookExA(int hookId, long hookPtr, long hMod, int thread) {
		try {
			return (long) setWindowsHookExA.invokeExact(hookId, hookPtr, hMod, thread);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public boolean unhookWindowsHookEx(long pHook) {
		try {
			return (boolean) unhookWindowsHookEx.invokeExact(pHook);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public boolean getMessageW(long lpMsg, long hWnd, int wMsgFilterMin, int wMsgFilterMax) {
		try {
			return (boolean) getMessage.invokeExact(lpMsg, hWnd, wMsgFilterMin, wMsgFilterMax);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public boolean waitMessage() {
		try {
			return (boolean) waitMessage.invokeExact();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@FunctionalInterface
	public interface LowLevelKeyboardProc {
		int call(int nCode, int wParam, long lParam);
	}

	public static User32 getInstance() {
		User32 inst = instance;
		if (inst == null) {
			try {
				inst = new User32();
				instance = inst;
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
		return inst;
	}
}
