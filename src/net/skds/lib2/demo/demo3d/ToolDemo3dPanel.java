package net.skds.lib2.demo.demo3d;

import javax.swing.*;

import net.skds.lib2.mat.vec3.Vec3;

import java.awt.*;

public class ToolDemo3dPanel extends JPanel {

	private final Demo3dFrame demo;

	public ToolDemo3dPanel(Demo3dFrame demo) {
		this.demo = demo;
		setBackground(Color.LIGHT_GRAY);
		setPreferredSize(new Dimension(300, 0));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Vec3 cameraPos = this.demo.renderPanel.getCameraPos();
		float cameraYaw = this.demo.renderPanel.getCameraYaw();
		float cameraPitch = this.demo.renderPanel.getCameraPitch();
		g.drawString(String.format("[%s, %s, %s]", 
				String.format("%.1f", cameraPos.x()), 
				String.format("%.1f", cameraPos.y()), 
				String.format("%.1f", cameraPos.z())
		), 5, 13);
		g.drawString(String.format("[%s, %s]", 
				String.format("%.1f", cameraYaw), 
				String.format("%.1f", cameraPitch)
		), 5, 13 + 13);
	}
}
