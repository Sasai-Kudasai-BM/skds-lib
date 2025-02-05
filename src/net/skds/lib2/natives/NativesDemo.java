package net.skds.lib2.natives;

import lombok.CustomLog;
import net.skds.lib2.utils.platforms.PlatformFeatures;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@CustomLog
public class NativesDemo extends JFrame {

	private static final KeyListener kl = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {
			System.out.println("--- W Press ---");
			System.out.println("vk: " + e.getKeyCode());
			//System.out.println("alt: " + e.isAltDown());
			//System.out.println("ctrl: " + e.isControlDown());
			//System.out.println("shift: " + e.isShiftDown());
			//System.out.println("---------------");
		}

		@Override
		public void keyReleased(KeyEvent e) {
			//System.out.println("--- W Release ---");
			//System.out.println("char: " + (int) e.getKeyChar());
			//System.out.println("vk: " + e.getKeyCode());
			//System.out.println("alt: " + e.isAltDown());
			//System.out.println("ctrl: " + e.isControlDown());
			//System.out.println("shift: " + e.isShiftDown());
			//System.out.println("-----------------");
		}
	};
	private static final KeyListener kl2 = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			System.out.println("--- J Press ---");
			System.out.println("vk: " + e.getKeyCode());
			//System.out.println("alt: " + e.isAltDown());
			//System.out.println("ctrl: " + e.isControlDown());
			//System.out.println("shift: " + e.isShiftDown());
			//System.out.println("---------------");
		}

		@Override
		public void keyReleased(KeyEvent e) {
			//System.out.println("--- J Release ---");
			//System.out.println("char: " + (int) e.getKeyChar());
			//System.out.println("vk: " + e.getKeyCode());
			//System.out.println("alt: " + e.isAltDown());
			//System.out.println("ctrl: " + e.isControlDown());
			//System.out.println("shift: " + e.isShiftDown());
			//System.out.println("-----------------");
		}
	};

	public NativesDemo() {
		super("NativesDemo");

		setLayout(new GridBagLayout());
		JButton button = new JButton("KL Add");
		button.addActionListener(e -> testKLAdd());
		add(button);
		button.addKeyListener(kl2);
		button = new JButton("KL Remove");
		button.addActionListener(e -> testKLRemove());
		add(button);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(270, 60));
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setVisible(true);

	}

	private void testKLAdd() {
		PlatformFeatures.getInstance().addKeyListener(kl);
	}

	private void testKLRemove() {
		PlatformFeatures.getInstance().removeKeyListener(kl);
	}
}
