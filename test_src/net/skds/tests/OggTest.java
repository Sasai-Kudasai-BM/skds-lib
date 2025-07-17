package net.skds.tests;

import net.skds.lib2.misc.ogg.OggPage;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OggTest {

	public static void main(String[] args) throws IOException {
		SKDSLogger.replaceOuts();
		try (InputStream is = new BufferedInputStream(new FileInputStream("run/sound/1.ogg"))) {
			while (is.available() > 0) {
				OggPage page = new OggPage();
				page.read(is);
				System.out.println(page);
			}
		}
	}
}
