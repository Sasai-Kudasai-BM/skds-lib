package net.skds.lib2.awtutils;

import lombok.experimental.UtilityClass;
import net.skds.lib2.utils.exception.UnsupportedSystemException;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

@UtilityClass
public class AWTUtils {

	public static void initWindow(JFrame window) {
		window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		window.pack();
		window.setLocation(-window.getWidth() / 2, -window.getHeight() / 2);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	public static void displayImage(Image image) {
		JFrame frame = new JFrame("Image");
		frame.add(new JLabel(new ImageIcon(image)));
		initWindow(frame);
	}

	/**
	 * <h2>!!! Only for Windows OS !!!</h2>
	 *
	 * @param window the window to get HWnd from
	 * @return the HWnd of given window
	 * <pre>
	 * use with <code>--add-exports java.desktop/sun.awt=ALL-UNNAMED --add-exports java.desktop/sun.awt.windows=ALL-UNNAMED</code>
	 * </pre>
	 */
	public static long getHWnd(Window window) {
		@FunctionalInterface
		interface Accessor {
			long get(Window window);

			Accessor ACCESSOR = createAccessor();

			private static Accessor createAccessor() {
				try {
					Class<?> cpc = Class.forName("sun.awt.windows.WComponentPeer");
					Method ghw = cpc.getMethod("getHWnd");
					ghw.setAccessible(true);
					return w -> {
						try {
							Object peer = getWindowPeer(w);
							return (long) ghw.invoke(peer);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					};
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return Accessor.ACCESSOR.get(window);
	}

	/**
	 * <h2>!!! Only for Linux/X11 !!!</h2>
	 *
	 * <pre>
	 * use with <code>--add-exports java.desktop/sun.awt.X11=ALL-UNNAMED</code>
	 * </pre>
	 */
	public static long getX11Display() {
		@FunctionalInterface
		interface Accessor {
			long get();

			Accessor ACCESSOR = createAccessor();

			private static Accessor createAccessor() {
				try {
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Class<?> tc = toolkit.getClass();
					if (!tc.getName().equals("sun.awt.X11.XToolkit")) {
						throw new UnsupportedSystemException("getX11Display is not supported on your system");
					}
					Method gd = tc.getMethod("getDisplay");
					gd.setAccessible(true);
					return () -> {
						try {
							return (long) gd.invoke(null);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					};
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return Accessor.ACCESSOR.get();
	}

	/**
	 * <h2>!!! Only for Linux/X11 !!!</h2>
	 *
	 * <pre>
	 * use with <code>--add-exports java.desktop/sun.awt=ALL-UNNAMED
	 * --add-exports java.desktop/sun.awt.X11=ALL-UNNAMED</code>
	 * </pre>
	 */
	public static long getX11WindowId(Window window) {
		@FunctionalInterface
		interface Accessor {
			long get(Window window);

			Accessor ACCESSOR = createAccessor();

			private static Accessor createAccessor() {
				try {
					Class<?> xwc = Class.forName("sun.awt.X11.XBaseWindow");
					Method gxp = xwc.getMethod("getWindow");
					gxp.setAccessible(true);
					return w -> {
						try {
							Object peer = getWindowPeer(w);
							return (long) gxp.invoke(peer);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					};
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return Accessor.ACCESSOR.get(window);
	}

	/**
	 * <h2>!!! Only for Linux/X11 !!!</h2>
	 *
	 * <pre>
	 * use with <code>--add-exports java.desktop/sun.awt=ALL-UNNAMED
	 * --add-exports java.desktop/sun.awt.X11=ALL-UNNAMED
	 * --add-opens java.desktop/sun.awt.X11=ALL-UNNAMED</code>
	 * </pre>
	 */
	public static int getX11WindowVisualId(Window window) {
		@FunctionalInterface
		interface Accessor {
			int get(Window window);

			Accessor ACCESSOR = createAccessor();

			private static Accessor createAccessor() {
				try {
					Class<?> xwc = Class.forName("sun.awt.X11.XWindow");
					Method ggc = xwc.getMethod("getGraphicsConfiguration");
					ggc.setAccessible(true);
					Class<?> x11gcc = Class.forName("sun.awt.X11GraphicsConfig");
					Method gv = x11gcc.getMethod("getVisual");
					gv.setAccessible(true);
					return w -> {
						try {
							Object peer = getWindowPeer(w);
							Object gc = ggc.invoke(peer);
							return (int) gv.invoke(gc);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					};
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return Accessor.ACCESSOR.get(window);
	}

	private static Object getWindowPeer(Window window) throws Exception {
		@FunctionalInterface
		interface Accessor {
			Object get(Window window) throws Exception;

			Accessor ACCESSOR = createAccessor();

			private static Accessor createAccessor() {
				try {
					Class<?> ac = Class.forName("sun.awt.AWTAccessor");
					Class<?> cac = Class.forName("sun.awt.AWTAccessor$ComponentAccessor");
					Method ga = ac.getMethod("getComponentAccessor");
					Method gp = cac.getMethod("getPeer", Component.class);
					ga.setAccessible(true);
					Object a = ga.invoke(null);
					gp.setAccessible(true);
					return w -> {
						try {
							return gp.invoke(a, w);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					};
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return Accessor.ACCESSOR.get(window);
	}
}
