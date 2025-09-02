package net.skds.tests;

import net.skds.lib2.utils.logger.SKDSLogger;
import net.skds.tests.json.JsonTest;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Map.Entry;

public class JsonTestFrame extends JFrame {
	JsonTestFrame() {
		super("SKDS Lib v2 demo");
		SKDSLogger.replaceOuts();

		setLayout(new GridBagLayout());

		Map<String, JsonTest.JsonTestRun> runs = JsonTest.createRuns();

		for (Entry<String, JsonTest.JsonTestRun> entry : runs.entrySet()) {
			JsonTest.JsonTestRegistry registry = new JsonTest.JsonTestRegistry();
			JButton button = new JButton(entry.getKey());
			JsonTest.JsonTestRun value = entry.getValue();
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
