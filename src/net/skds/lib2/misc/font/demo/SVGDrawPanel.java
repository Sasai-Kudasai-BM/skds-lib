package net.skds.lib2.misc.font.demo;

import javax.swing.*;
import java.awt.*;

public class SVGDrawPanel extends JPanel {

	private final Color lineColor = Color.GREEN;
	private final Color dotColor = Color.RED;
	private final Color curveColor = Color.BLUE;


	public SVGDrawPanel() {
		setBackground(Color.GRAY);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}
