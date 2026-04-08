package net.skds.lib2.demo;

import net.skds.lib2.misc.font.demo.FontDemoFrame;
import net.skds.lib2.shapes2d.Demo2D;
import net.skds.lib2.utils.logger.SKDSLogger;

import javax.swing.*;
import java.awt.*;

public class DemoFrame extends JFrame {

	public DemoFrame() {
		super("SKDS Lib demo");
		SKDSLogger.replaceOuts();

		setLayout(new GridBagLayout());

		JButton button = new JButton("Color");
		button.addActionListener(_ -> new ColorDemoFrame().setLocationRelativeTo(this));
		add(button);

		//button = new JButton("Natives");
		//button.addActionListener(_ -> new NativesDemo().setLocationRelativeTo(this));
		//add(button);

		button = new JButton("Noise");
		button.addActionListener(_ -> new NoiseFrame().setLocationRelativeTo(this));
		add(button);

		button = new JButton("Logger");
		button.addActionListener(_ -> new LoggerDemoFrame().setLocationRelativeTo(this));
		add(button);

		//button = new JButton("Classloader");
		//button.addActionListener(_ -> new DemoClassloaderFrame(this).setLocationRelativeTo(this));
		//add(button);

		//button = new JButton("StringClassloader");
		//button.addActionListener(_ -> new DemoClassloaderFrame2(this).setLocationRelativeTo(this));
		//add(button);

		button = new JButton("WeightedPool");
		button.addActionListener(_ -> new WeightedPoolDemoFrame().setLocationRelativeTo(this));
		add(button);

		button = new JButton("Overlay");
		button.addActionListener(_ -> new WindowOverlay().setLocationRelativeTo(this));
		add(button);

		button = new JButton("Font");
		button.addActionListener(_ -> new FontDemoFrame().setLocationRelativeTo(this));
		add(button);

		button = new JButton("2D");
		button.addActionListener(_ -> new Demo2D().setLocationRelativeTo(this));
		add(button);

		//button = new JButton("floor");
		//button.addActionListener(_ -> {
		//	var list = List.of(0.1, 0.8, -0.1, -0.8, 0d);
		//	for (double d : list) {
		//		System.out.println("==========");
		//		System.out.println(d);
		//		System.out.println("f: " + FastMath.floor(d) + " " + (int) Math.floor(d));
		//		System.out.println("c: " + FastMath.ceil(d) + " " + (int) Math.ceil(d));
		//		System.out.println("r: " + FastMath.round(d) + " " + (int) Math.round(d));
		//	}
		//});
		//add(button);

		//button = new JButton("Pow2");
		//button.addActionListener(_ -> {
		//	var list = List.of(1, 1024, 1025, 1023, 1 << 20);
		//	for (int i : list) {
		//		System.out.println("==========");
		//		System.out.println(FastMath.isPowerOf2(i) + " " + i);
		//	}
		//});
		//add(button);

		//button = new JButton("TestStruct");
		//button.addActionListener(_ -> {
		//	TestStruct.test();
		//});
		//add(button);
		//button = new JButton("Test natives");
		//button.addActionListener(_ -> {
		//	TestStruct.test2(this);
		//});
		//add(button);
		//button = new JButton("Demo3d");
		//button.addActionListener(_ ->
		//		Demo3dExample.init(new Demo3dFrame()).setLocationRelativeTo(this)
		//);
		//add(button);

		//button = new JButton("GraphicBuilder");
		//button.addActionListener(_ -> new GraphicBuilder().setLocationRelativeTo(this));
		//add(button);

		//button = new JButton("Json");
		//button.addActionListener(_ -> new JsonDemoFrame().setLocationRelativeTo(this));
		//add(button);


		setMinimumSize(new Dimension(300, 50));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args) throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		new DemoFrame();
	}
}
