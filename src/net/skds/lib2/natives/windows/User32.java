package net.skds.lib2.natives.windows;

import net.skds.lib2.natives.AbstractLinkedLibrary;
import net.skds.lib2.natives.LinkerUtils;
import net.skds.lib2.natives.UpcallLink;

import java.lang.invoke.MethodHandle;

import static net.skds.lib2.natives.LinkerUtils.*;

@SuppressWarnings({"DataFlowIssue", "UnusedReturnValue", "unused"})
public class User32 extends AbstractLinkedLibrary {

	private static final class Holder {
		private static final User32 INSTANCE = new User32();
	}

	public final UpcallLink<LowLevelKeyboardProc> lowLevelKeyboardProcUL = LinkerUtils.createUpcallLink(User32.LowLevelKeyboardProc.class);
	public final UpcallLink<LowLevelMouseProc> lowLevelMouseProc = LinkerUtils.createUpcallLink(User32.LowLevelMouseProc.class);

	private final MethodHandle peekMessage = createHandle(lib, "PeekMessageA", BOOLEAN, PTR, PTR, INT, INT, INT);
	private final MethodHandle waitMessage = createHandle(lib, "WaitMessage", BOOLEAN);
	private final MethodHandle getMessage = createHandle(lib, "GetMessageW", BOOLEAN, PTR, PTR, INT, INT);
	private final MethodHandle setWindowsHookExA = createHandle(lib, "SetWindowsHookExA", PTR, INT, PTR, PTR, INT);
	private final MethodHandle unhookWindowsHookEx = createHandle(lib, "UnhookWindowsHookEx", BOOLEAN, PTR);
	//private final MethodHandle registerRawInputDevices = createHandle(lib, "RegisterRawInputDevices", BOOLEAN, PTR, INT, INT);
	//private final MethodHandle getRawInputData = createHandle(lib, "GetRawInputData", INT, PTR, INT, PTR, PTR, INT);

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

	/*
	public int getRawInputData(long hRawInput, int uiCommand, long pData, long pcbSize, int cbSizeHeader) {
		try {
			return (int) getRawInputData.invokeExact(hRawInput, uiCommand, pData, pcbSize, cbSizeHeader);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public boolean registerRawInputDevices(long pRawInputDevices, int uiNumDevices, int cbSize) {
		try {
			return (boolean) registerRawInputDevices.invokeExact(pRawInputDevices, uiNumDevices, cbSize);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	 */

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

	public boolean peekMessage(long lpMsg, long hWnd, int wMsgFilterMin, int wMsgFilterMax, int wRemoveMsg) {
		try {
			return (boolean) peekMessage.invokeExact(lpMsg, hWnd, wMsgFilterMin, wMsgFilterMax, wRemoveMsg);
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

	@FunctionalInterface
	public interface LowLevelMouseProc {
		int call(int nCode, int wParam, long lParam);
	}


	public static User32 getInstance() {
		return Holder.INSTANCE;
	}
}
