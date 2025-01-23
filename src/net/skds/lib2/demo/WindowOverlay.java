package net.skds.lib2.demo;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class WindowOverlay extends JFrame {

	public WindowOverlay() {
		super("Overlay demo");

		setPreferredSize(new Dimension(600, 600));

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		setUndecorated(true);
		Border border = BorderFactory.createLineBorder(new Color(0, 0, 255, 127), 5);
		getRootPane().setBorder(border);
		setBackground(new Color(0, true));
		pack();

		//Toolkit.getDefaultToolkit().getLockingKeyState()
		//		setVisible(true);

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println(e.getKeyChar());
			}
		});
	}
}
