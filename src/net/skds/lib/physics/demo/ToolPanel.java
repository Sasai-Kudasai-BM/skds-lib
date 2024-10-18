package net.skds.lib.physics.demo;

import javax.swing.*;
import java.awt.*;

public class ToolPanel extends JPanel {

	private final PhysicsDemo physicsDemo;

	public ToolPanel(PhysicsDemo physicsDemo) {
		this.physicsDemo = physicsDemo;
		setBackground(Color.GRAY);
		setPreferredSize(new Dimension(300, 0));
	}
}
