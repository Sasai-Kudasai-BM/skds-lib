package net.skds.lib2.security;

import net.skds.lib2.awtutils.layouts.LayoutMode;
import net.skds.lib2.awtutils.layouts.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordGenerator extends JFrame {

	private final JTextField length = new JTextField("64");
	private final JTextField out = new JTextField();

	private PasswordGenerator() {
		setLayout(new VerticalLayout(2, LayoutMode.FILL));
		add(new JLabel("length"));
		add(length);
		JButton gen = new JButton("generate");
		gen.addActionListener(e -> out.setText(generate()));
		add(gen);
		add(new JLabel("Output"));
		add(out);

		setMinimumSize(new Dimension(600, 0));
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
	}

	private String generate() {
		try {
			int l = Integer.parseInt(length.getText());
			byte[] b = new byte[l];
			SecureRandom sr = SecureRandom.getInstanceStrong();
			sr.nextBytes(b);
			String raw = Base64.getEncoder().encodeToString(b);
			return raw.substring(0, l);
		} catch (Exception e) {
			return "error " + e;
		}
	}

	public static void main(String[] args) {
		new PasswordGenerator().setVisible(true);
	}
}
