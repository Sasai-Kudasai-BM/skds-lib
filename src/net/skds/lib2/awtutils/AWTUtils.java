package net.skds.lib2.awtutils;

import lombok.experimental.UtilityClass;

import javax.swing.*;
import java.awt.*;

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
}
