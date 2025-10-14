package net.skds.lib2.demo;

import lombok.CustomLog;
import net.skds.lib2.utils.logger.SKDSLogger;

import javax.swing.*;
import java.awt.*;

@CustomLog
public class LoggerDemoFrame extends JFrame {

	public LoggerDemoFrame() {
		super("Logger demo");

		setLayout(new FlowLayout());

		JButton btn = new JButton("replaceOuts");
		btn.addActionListener(_ -> {
			SKDSLogger.replaceOuts();
		});
		add(btn);

		btn = new JButton("log");
		btn.addActionListener(_ -> {
			log.log("log");
		});
		add(btn);

		btn = new JButton("warn");
		btn.addActionListener(_ -> {
			log.warn("warn");
		});
		add(btn);

		btn = new JButton("sout");
		btn.addActionListener(_ -> {
			System.out.println("sout");
		});
		add(btn);

		btn = new JButton("serr");
		btn.addActionListener(_ -> {
			System.err.println("serr");
		});
		add(btn);

		btn = new JButton("println");
		btn.addActionListener(_ -> {
			System.out.println();
		});
		add(btn);

		btn = new JButton("print");
		btn.addActionListener(_ -> {
			System.out.println("string: ");
			System.out.print("print");
		});
		add(btn);

		btn = new JButton("testAll");
		btn.addActionListener(_ -> {
			System.out.println("1");
			System.err.println("2");
			System.out.println();
			System.err.println();
			log.info("info");
			log.debug("debug");
			log.warn("warn");
			log.error("error");
			log.log("log");
		});
		add(btn);

		setMinimumSize(new Dimension(300, 50));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

}
