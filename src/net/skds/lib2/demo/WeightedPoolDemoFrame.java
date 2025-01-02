package net.skds.lib2.demo;

import lombok.CustomLog;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.utils.collection.WeightedPool;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@CustomLog
public class WeightedPoolDemoFrame extends JFrame {


	public WeightedPoolDemoFrame() {
		super("WeightedPool demo");

		setLayout(new FlowLayout());

		JButton btn = new JButton("size 0");
		btn.addActionListener(e -> {
			WeightedPool<String> wp = new WeightedPool<>(List.of(), s -> 1);

			log.debug("========= size 0 ==========");
			for (int i = 0; i < 12; i++) {
				log.debug(wp.get(i / 10f));
			}
		});
		add(btn);

		btn = new JButton("size 1");
		btn.addActionListener(e -> {
			WeightedPool<String> wp = new WeightedPool<>(List.of("ass"), s -> 1);

			log.debug("========= size 1 ==========");
			for (int i = 0; i < 12; i++) {
				log.debug(wp.get(i / 10f));
			}
		});
		add(btn);

		btn = new JButton("size 5");
		btn.addActionListener(e -> {
			List<String> list = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				list.add(String.valueOf(i));
			}
			WeightedPool<String> wp = new WeightedPool<>(list, s -> 1);

			log.debug("========= size 5 ==========");
			for (int i = 0; i < 12; i++) {
				log.debug(wp.get(i / 10f));
			}
		});
		add(btn);

		btn = new JButton("size 10");
		btn.addActionListener(e -> {
			List<String> list = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				list.add(String.valueOf(i));
			}
			WeightedPool<String> wp = new WeightedPool<>(list, s -> 1);

			log.debug("========= size 10 ==========");
			for (int i = 0; i < 12; i++) {
				log.debug(wp.get(i / 10f));
			}
		});
		add(btn);

		btn = new JButton("size 15");
		btn.addActionListener(e -> {
			List<String> list = new ArrayList<>();
			for (int i = 0; i < 15; i++) {
				list.add(String.valueOf(i));
			}
			WeightedPool<String> wp = new WeightedPool<>(list, s -> 1);

			log.debug("========= size 15 ==========");
			for (int i = 0; i < 12; i++) {
				log.debug(wp.get(i / 10f));
			}
		});
		add(btn);

		btn = new JButton("size 100 R");
		btn.addActionListener(e -> {
			List<String> list = new ArrayList<>();
			for (int i = 0; i < 100; i++) {
				list.add(String.valueOf(i));
			}
			WeightedPool<String> wp = new WeightedPool<>(list, String::hashCode);

			log.debug("========= size 100 R ==========");
			for (int i = 0; i < 12; i++) {
				log.debug(wp.get(i / 10f));
			}
		});
		add(btn);

		btn = new JButton("size 1000 R");
		btn.addActionListener(e -> {
			List<String> list = new ArrayList<>();
			for (int i = 0; i < 1000; i++) {
				list.add(String.valueOf(i));
			}
			WeightedPool<String> wp = new WeightedPool<>(list, String::hashCode);

			log.debug("========= size 1000 R ==========");
			for (int i = 0; i < 12; i++) {
				log.debug(wp.get(i / 10f));
			}
		});
		add(btn);

		btn = new JButton("size 10 Fail");
		btn.addActionListener(e -> {
			List<String> list = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				list.add(String.valueOf(i));
			}
			WeightedPool<String> wp = new WeightedPool<>(list, s -> FastMath.RANDOM.nextFloat());

			log.debug("========= size 10 Fail ==========");
			for (int i = 0; i < 11; i++) {
				log.debug(wp.get(i / 10f));
			}
		});
		add(btn);

		btn = new JButton("size 10 remove");
		btn.addActionListener(e -> {
			List<String> list = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				list.add(String.valueOf(i));
			}
			WeightedPool<String> wp = new WeightedPool<>(list, s -> 1);

			log.debug("========= size 10 remove ==========");
			for (int i = 0; i < 11; i++) {
				log.debug(wp.getAndRemove(.5f));
			}

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
