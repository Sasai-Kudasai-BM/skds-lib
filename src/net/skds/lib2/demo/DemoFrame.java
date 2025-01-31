package net.skds.lib2.demo;

import net.skds.lib2.demo.classloader.DemoClassloaderFrame;
import net.skds.lib2.demo.demo3d.Demo3dFrame;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.misc.font.demo.FontDemoFrame;
import net.skds.lib2.utils.logger.SKDSLogger;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DemoFrame extends JFrame {

	DemoFrame() {
		super("SKDS Lib v2 demo");
		SKDSLogger.replaceOuts();

		setLayout(new FlowLayout());
		JButton button = new JButton("Color");
		button.addActionListener(e -> new ColorDemoFrame());
		add(button);

		button = new JButton("Perlin");
		button.addActionListener(e -> new NoiseFrame());
		add(button);

		button = new JButton("Logger");
		button.addActionListener(e -> new LoggerDemoFrame());
		add(button);

		button = new JButton("Classloader");
		button.addActionListener(e -> new DemoClassloaderFrame(this));
		add(button);

		button = new JButton("WeightedPool");
		button.addActionListener(e -> new WeightedPoolDemoFrame());
		add(button);

		button = new JButton("Overlay");
		button.addActionListener(e -> new WindowOverlay());
		add(button);

		button = new JButton("Font");
		button.addActionListener(e -> new FontDemoFrame());
		add(button);

		button = new JButton("floor");
		button.addActionListener(e -> {
			var list = List.of(0.1, 0.8, -0.1, -0.8, 0d);
			for (double d : list) {
				System.out.println("==========");
				System.out.println(d);
				System.out.println("f: " + FastMath.floor(d) + " " + (int) Math.floor(d));
				System.out.println("c: " + FastMath.ceil(d) + " " + (int) Math.ceil(d));
				System.out.println("r: " + FastMath.round(d) + " " + (int) Math.round(d));
			}
		});
		add(button);
		button = new JButton("Demo3d");
		button.addActionListener(e -> 
			new Demo3dFrame().initDefault()
		);
		add(button);


		setMinimumSize(new Dimension(300, 50));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	public static void main(String[] args) {
		new DemoFrame();
	}
}
