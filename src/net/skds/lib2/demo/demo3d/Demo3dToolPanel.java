package net.skds.lib2.demo.demo3d;

import javax.swing.*;

import net.skds.lib2.mat.vec3.Vec3;

import java.awt.*;

public class Demo3dToolPanel extends JPanel {

	private final Demo3dFrame demo;

	public Demo3dToolPanel(Demo3dFrame demo) {
		this.demo = demo;
		setBackground(Color.LIGHT_GRAY);
		setPreferredSize(new Dimension(300, 0));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Vec3 cameraPos = this.demo.getCameraPos();

		int y = 0;
		int d = 20;

		Font font = g.getFont().deriveFont( 20.0f );
		g.setFont(font);

		g.drawString(String.format("Pos: [%s, %s, %s] (w,a,s,d)", 
				String.format("%.1f", cameraPos.x()), 
				String.format("%.1f", cameraPos.y()), 
				String.format("%.1f", cameraPos.z())
		), 5, y += d);

		g.drawString(String.format("Rot: [%s, %s] (mouse)", 
				String.format("%.1f", this.demo.getCameraYaw()), 
				String.format("%.1f", this.demo.getCameraPitch())
		), 5, y += d);

		g.drawString(String.format("Spd: %s (scroll)", 
				String.format("%.2f", this.demo.getSpeed())
		), 5, y += d);

		g.drawString(String.format("Fov: %s (shift + scroll)", 
				String.format("%.0f", this.demo.getCameraFov())
		), 5, y += d);
	}
}
