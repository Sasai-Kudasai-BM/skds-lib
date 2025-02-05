package net.skds.lib2.utils.platforms;

import lombok.CustomLog;
import net.skds.lib2.natives.windows.Kernel32;
import net.skds.lib2.natives.windows.User32;
import net.skds.lib2.utils.ThreadUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.LinkedList;

import static java.awt.event.KeyEvent.*;
import static net.skds.lib2.natives.SafeLinker.INT;
import static net.skds.lib2.natives.SafeLinker.LONG;

@CustomLog
final class WindowsPlatform extends PlatformFeatures {

	//private static final int WM_KEYDOWN = 0x0100;
	//private static final int WM_KEYUP = 0x0101;
	//private static final int WM_SYSKEYDOWN = 0x0104;
	//private static final int WM_SYSKEYUP = 0x0105;

	private static final int WH_KEYBOARD_LL = 13;

	private static final int WIN_L_SHIFT = 0xA0;
	private static final int WIN_R_SHIFT = 0xA1;
	private static final int WIN_L_CONTROL = 0xA2;
	private static final int WIN_R_CONTROL = 0xA3;
	private static final int WIN_L_MENU = 0xA4;
	private static final int WIN_R_MENU = 0xA5;

	private static final GroupLayout KBDLLHOOK_STRUCT = MemoryLayout.structLayout(
			INT.withName("vkCode"),
			INT.withName("scanCode"),
			INT.withName("flags"),
			INT.withName("time"),
			LONG.withName("dwExtraInfo")
	);


	private final Component blankComponent = Box.createGlue();

	private final User32 user32 = User32.getInstance();
	private final Kernel32 kernel32 = Kernel32.getInstance();
	//private final Toolkit toolkit = Toolkit.getDefaultToolkit();

	private KeyListenerThread keyListenerThread;

	private final long moduleHandle = kernel32.getModuleHandle(0);

	public WindowsPlatform() {
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		synchronized (this) {
			KeyListenerThread kt = keyListenerThread;
			if (kt == null) {
				kt = new KeyListenerThread();
				keyListenerThread = kt;
				kt.start();
			}
			kt.listeners.add(listener);
		}
	}

	@Override
	public void removeKeyListener(KeyListener listener) {
		synchronized (this) {
			KeyListenerThread kt = keyListenerThread;
			if (kt != null) {
				kt.listeners.remove(listener);
			}
		}
	}


	private KeyEvent processKeyEvent(long ptr, long callTime, KeyListenerThread thread) {

		MemorySegment ms = MemorySegment.ofAddress(ptr).reinterpret(KBDLLHOOK_STRUCT.byteSize());

		int vkCode = ms.get(ValueLayout.JAVA_INT, 0L);
		int flags = ms.get(ValueLayout.JAVA_INT, 8L);

		//boolean alt = (flags & 32) != 0;
		boolean up = (flags & 128) != 0;
		int id = up ? KEY_RELEASED : KEY_PRESSED;
		//boolean caps = toolkit.getLockingKeyState(VK_CAPS_LOCK);
		//char capsChar = (char) vkCode;
		//char c = caps ? capsChar : Character.toLowerCase(capsChar);

		int location = KEY_LOCATION_UNKNOWN;

		switch (vkCode) {
			case VK_SHIFT -> thread.applyMask(up, SHIFT_DOWN_MASK);
			case WIN_L_SHIFT -> {
				thread.applyMask(up, SHIFT_DOWN_MASK);
				location = KEY_LOCATION_LEFT;
				vkCode = VK_SHIFT;
			}
			case WIN_R_SHIFT -> {
				thread.applyMask(up, SHIFT_DOWN_MASK);
				location = KEY_LOCATION_RIGHT;
				vkCode = VK_SHIFT;
			}

			case VK_CONTROL -> thread.applyMask(up, CTRL_DOWN_MASK);
			case WIN_L_CONTROL -> {
				thread.applyMask(up, CTRL_DOWN_MASK);
				location = KEY_LOCATION_LEFT;
				vkCode = VK_CONTROL;
			}
			case WIN_R_CONTROL -> {
				thread.applyMask(up, CTRL_DOWN_MASK);
				location = KEY_LOCATION_RIGHT;
				vkCode = VK_CONTROL;
			}

			case VK_ALT -> thread.applyMask(up, ALT_DOWN_MASK);
			case WIN_L_MENU -> {
				thread.applyMask(up, ALT_DOWN_MASK);
				location = KEY_LOCATION_LEFT;
				vkCode = VK_ALT;
			}
			case WIN_R_MENU -> {
				thread.applyMask(up, ALT_DOWN_MASK);
				location = KEY_LOCATION_RIGHT;
				vkCode = VK_ALT;
			}
		}

		return new KeyEvent(blankComponent, id, callTime, thread.keyFlags, vkCode, CHAR_UNDEFINED, location);
	}

	private class KeyListenerThread extends ThreadUtils.SKDSThread {

		private final LinkedList<KeyListener> listeners = new LinkedList<>();
		private int keyFlags = 0;
		public boolean running = true;
		private long hookHandle;

		public KeyListenerThread() {
			super(ThreadUtils.MAIN_GROUP, "Windows-Platform-KeyListener");
			setDaemon(true);
		}

		private void applyMask(boolean up, int mask) {
			if (up) {
				keyFlags &= ~mask;
			} else {
				keyFlags |= mask;
			}
		}

		@Override
		public void run() {
			User32.LowLevelKeyboardProc caller = (n, w, ptr) -> {
				if (n < 0) {
					log.warn("User32.LowLevelKeyboardProc: n is negative " + n);
					return 0;
				}
				long callTime = System.currentTimeMillis();
				KeyEvent event = processKeyEvent(ptr, callTime, this);
				synchronized (WindowsPlatform.this) {
					if (!listeners.isEmpty()) {
						switch (event.getID()) {
							case KEY_PRESSED -> {
								for (KeyListener kl : listeners) {
									kl.keyPressed(event);
								}
							}
							case KEY_RELEASED -> {
								for (KeyListener kl : listeners) {
									kl.keyReleased(event);
								}
							}
							case KEY_TYPED -> {
								for (KeyListener kl : listeners) {
									kl.keyTyped(event);
								}
							}
						}
					}
				}
				return 0;
			};

			try {
				MemorySegment segment = user32.lowLevelKeyboardProcUL.bind(caller);
				hookHandle = user32.setWindowsHookExA(WH_KEYBOARD_LL, segment.address(), moduleHandle, 0);
				while (running) {
					user32.getMessageW(0, -1, 0, 0);
				}
			} finally {
				user32.unhookWindowsHookEx(hookHandle);
			}
		}
	}


	/*
	protected enum KeyMap {
		L_SHIFT(WIN_L_SHIFT, KeyEvent.VK_SHIFT, KeyEvent.KEY_LOCATION_LEFT),    //Left SHIFT
		R_SHIFT(WIN_R_SHIFT, KeyEvent.VK_SHIFT, KeyEvent.KEY_LOCATION_RIGHT),    //Right SHIFT
		L_CONTROL(WIN_L_CONTROL, KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_LEFT),    //Left CTRL
		R_CONTROL(WIN_R_CONTROL, KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_RIGHT),    //Right CTRL
		L_MENU(WIN_L_MENU, KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_LEFT),    //Left ALT
		R_MENU(WIN_R_MENU, KeyEvent.VK_ALT, KeyEvent.KEY_LOCATION_RIGHT),    //Right ALT
		;
		final int wCode;
		final int jCode;
		final int location;
		KeyMap(int wCode, int jCode, int location) {
			this.wCode = wCode;
			this.jCode = jCode;
			this.location = location;
		}
	}
	 */
}
