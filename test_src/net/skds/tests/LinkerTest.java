package net.skds.tests;

import lombok.CustomLog;
import net.skds.lib2.utils.ThreadUtils;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.skds.lib2.utils.platforms.PlatformFeatures;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@CustomLog
public class LinkerTest {

	public static void main(String[] args_) {
		SKDSLogger.replaceOuts();

		PlatformFeatures.getInstance().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println(e.getKeyCode());
			}
		});

		System.out.println("lol");
		ThreadUtils.await(100_000);

	}


}
