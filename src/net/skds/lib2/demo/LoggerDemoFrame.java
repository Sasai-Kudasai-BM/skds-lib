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
		btn.addActionListener(e -> {
			SKDSLogger.replaceOuts();
		});
		add(btn);


		btn = new JButton("log");
		btn.addActionListener(e -> {
			log.log("amogus");
		});
		add(btn);


		btn = new JButton("sout");
		btn.addActionListener(e -> {
			System.out.println("sout");
		});
		add(btn);


		btn = new JButton("serr");
		btn.addActionListener(e -> {
			System.err.println("serr");
		});
		add(btn);


		btn = new JButton("warn");
		btn.addActionListener(e -> {
			log.warn("warn");
		});
		add(btn);

		btn = new JButton("println");
		btn.addActionListener(e -> {
			System.out.println();
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
