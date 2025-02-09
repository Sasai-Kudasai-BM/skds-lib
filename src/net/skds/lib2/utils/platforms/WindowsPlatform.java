package net.skds.lib2.utils.platforms;

import lombok.CustomLog;
import net.skds.lib2.natives.windows.Kernel32;
import net.skds.lib2.natives.windows.User32;
import net.skds.lib2.utils.ThreadUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.LinkedList;

import static java.awt.event.KeyEvent.ALT_DOWN_MASK;
import static java.awt.event.KeyEvent.CTRL_DOWN_MASK;
import static java.awt.event.KeyEvent.SHIFT_DOWN_MASK;
import static java.awt.event.KeyEvent.*;
import static java.awt.event.MouseEvent.*;
import static net.skds.lib2.natives.SafeLinker.INT;
import static net.skds.lib2.natives.SafeLinker.LONG;

@CustomLog
final class WindowsPlatform extends PlatformFeatures {

	private static final int WH_KEYBOARD_LL = 13;
	private static final int WH_MOUSE_LL = 14;

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

	private static final GroupLayout MSLLHOOK_STRUCT = MemoryLayout.structLayout(
			MemoryLayout.structLayout(
					INT.withName("x"),
					INT.withName("y")
			).withName("pt"),
			INT.withName("mouseData"),
			INT.withName("flags"),
			INT.withName("time"),
			MemoryLayout.paddingLayout(4),
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
			kt.keyListeners.add(listener);
		}
	}

	@Override
	public void removeKeyListener(KeyListener listener) {
		synchronized (this) {
			KeyListenerThread kt = keyListenerThread;
			if (kt != null) {
				kt.keyListeners.remove(listener);
			}
		}
	}

	@Override
	public void addMouseListener(MouseListener listener) {
		synchronized (this) {
			KeyListenerThread kt = keyListenerThread;
			if (kt == null) {
				kt = new KeyListenerThread();
				keyListenerThread = kt;
				kt.start();
			}
			kt.mouseListeners.add(listener);
		}
	}

	@Override
	public void removeMouseListener(MouseListener listener) {
		synchronized (this) {
			KeyListenerThread kt = keyListenerThread;
			if (kt != null) {
				kt.mouseListeners.remove(listener);
			}
		}
	}


	/*
	private KeyEvent processKeyEvent(int wp, long flags, long callTime, KeyListenerThread thread) {
		System.out.println(wp + " " + flags);
		int vkCode = wp;
		boolean up = (flags & (1L << 31)) != 0;
		int id = up ? KEY_RELEASED : KEY_PRESSED;

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
	 */

	private MouseEvent processMouseEventLL(int wp, long ptr, long callTime, KeyListenerThread thread) {
		if (wp != 512) {
			wp -= 512;
			if (wp == 10) {
				return null;
			}
			boolean down;
			int k;
			if (wp >= 11) {
				MemorySegment ms = MemorySegment.ofAddress(ptr).reinterpret(MSLLHOOK_STRUCT.byteSize());
				int md = ms.get(ValueLayout.JAVA_INT, 8L);
				k = (md >> 16) + 3;
				down = wp == 11;
			} else {
				k = wp / 3;
				down = wp % 3 != 2;
			}

			//System.out.println(wp + " " + k + " " + down);

			int id = down ? MOUSE_PRESSED : MOUSE_RELEASED;
			switch (k) {
				case 0 -> k = BUTTON1;
				case 1 -> k = BUTTON3;
			}
			return new MouseEvent(blankComponent, id, callTime, thread.keyFlags, 0, 0, 1, false, k);
		}
		return null;
	}

	private KeyEvent processKeyEventLL(long ptr, long callTime, KeyListenerThread thread) {

		MemorySegment ms = MemorySegment.ofAddress(ptr).reinterpret(KBDLLHOOK_STRUCT.byteSize());

		int vkCode = ms.get(ValueLayout.JAVA_INT, 0L);
		int flags = ms.get(ValueLayout.JAVA_INT, 8L);

		boolean up = (flags & 128) != 0;
		int id = up ? KEY_RELEASED : KEY_PRESSED;

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

		private final LinkedList<KeyListener> keyListeners = new LinkedList<>();
		private final LinkedList<MouseListener> mouseListeners = new LinkedList<>();
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
			User32.LowLevelKeyboardProc kCaller = (n, w, l) -> {
				if (n < 0) {
					log.warn("User32.LowLevelKeyboardProc: n is negative " + n);
					return 0;
				}
				long callTime = System.currentTimeMillis();
				//KeyEvent event = processKeyEvent(w, l, callTime, this);
				KeyEvent event = processKeyEventLL(l, callTime, this);
				synchronized (WindowsPlatform.this) {
					if (!keyListeners.isEmpty()) {
						switch (event.getID()) {
							case KEY_PRESSED -> {
								for (KeyListener kl : keyListeners) {
									kl.keyPressed(event);
								}
							}
							case KEY_RELEASED -> {
								for (KeyListener kl : keyListeners) {
									kl.keyReleased(event);
								}
							}
							case KEY_TYPED -> {
								for (KeyListener kl : keyListeners) {
									kl.keyTyped(event);
								}
							}
						}
					}
				}
				return 0;
			};
			User32.LowLevelMouseProc mCaller = (n, w, l) -> {
				if (n < 0) {
					log.warn("User32.LowLevelMouseProc: n is negative " + n);
					return 0;
				}
				long callTime = System.currentTimeMillis();
				MouseEvent event = processMouseEventLL(w, l, callTime, this);
				if (event == null) return 0;
				synchronized (WindowsPlatform.this) {
					if (!mouseListeners.isEmpty()) {
						switch (event.getID()) {
							case MOUSE_PRESSED -> {
								for (MouseListener ml : mouseListeners) {
									ml.mousePressed(event);
								}
							}
							case MOUSE_RELEASED -> {
								for (MouseListener ml : mouseListeners) {
									ml.mouseReleased(event);
								}
							}
						}
					}
				}
				return 0;
			};

			try {
				MemorySegment segment = user32.lowLevelKeyboardProcUL.bind(kCaller);
				hookHandle = user32.setWindowsHookExA(WH_KEYBOARD_LL, segment.address(), moduleHandle, 0);
				if (hookHandle == 0) {
					int err = kernel32.getLastError();
					throw new RuntimeException("Unable to create hook: err id " + err);
				}
				segment = user32.lowLevelMouseProc.bind(mCaller);
				hookHandle = user32.setWindowsHookExA(WH_MOUSE_LL, segment.address(), moduleHandle, 0);
				if (hookHandle == 0) {
					int err = kernel32.getLastError();
					throw new RuntimeException("Unable to create hook: err id " + err);
				}
				//long ptr = SafeLinker.ARENA.allocate(64).address();
				while (running) {
					//user32.waitMessage();
					//ThreadUtils.await(100);
					//while (user32.peekMessage(0, -1, 0, 0, 1)) ;
					user32.getMessageW(0, -1, 0, 0);
				}
			} finally {
				user32.unhookWindowsHookEx(hookHandle);
			}
		}
	}
}
