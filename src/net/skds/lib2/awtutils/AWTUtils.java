package net.skds.lib2.awtutils;

import lombok.experimental.UtilityClass;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.function.Function;

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
		final class HWNDAccessor {
			static Function<Window, Long> accessor = w0 -> {
				try {
					Class<?> ac = Class.forName("sun.awt.AWTAccessor");
					Class<?> cac = Class.forName("sun.awt.AWTAccessor$ComponentAccessor");
					Class<?> cpc = Class.forName("sun.awt.windows.WComponentPeer");
					Method ga = ac.getMethod("getComponentAccessor");
					Method gp = cac.getMethod("getPeer", Component.class);
					Method ghw = cpc.getMethod("getHWnd");
					ga.setAccessible(true);
					Object a = ga.invoke(null);
					gp.setAccessible(true);
					ghw.setAccessible(true);
					Function<Window, Long> accessor2 = w -> {
						try {
							Object peer = gp.invoke(a, w);
							return (long) ghw.invoke(peer);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					};
					accessor = accessor2;
					return accessor2.apply(w0);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			};
		}
		return HWNDAccessor.accessor.apply(window);
	}
}
