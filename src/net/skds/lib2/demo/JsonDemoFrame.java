package net.skds.lib2.demo;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Map.Entry;

import net.skds.lib2.io.json.test.JsonTest;
import net.skds.lib2.io.json.test.JsonTest.JsonTestRun;
import net.skds.lib2.utils.logger.SKDSLogger;

public class JsonDemoFrame extends JFrame {
	JsonDemoFrame() {
		super("SKDS Lib v2 demo");
		SKDSLogger.replaceOuts();

		setLayout(new GridBagLayout());

		Map<String, JsonTest.JsonTestRun> runs = JsonTest.createRuns();

		for (Entry<String, JsonTestRun> entry : runs.entrySet()) {
			JsonTest.JsonTestRegistry registry = new JsonTest.JsonTestRegistry();
			JButton button = new JButton(entry.getKey());
			JsonTestRun value = entry.getValue();
			button.addActionListener(e -> value.run(registry));
			add(button);
		}

		setMinimumSize(new Dimension(300, 50));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
