package net.skds.tests;

import lombok.CustomLog;
import net.skds.lib2.misc.ogg.OggInputStream;
import net.skds.lib2.misc.ogg.OggProcessor;
import net.skds.lib2.misc.sound.formats.opus.OpusHeader;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

@CustomLog
public class OggTest {

	public static void main(String[] args) throws IOException {
		SKDSLogger.replaceOuts();

		try (OggInputStream ogg = new OggInputStream(new BufferedInputStream(new FileInputStream("run/sound/1.ogg")))) {

			OggProcessor processor = new OggProcessor(p0 -> {
				System.out.println(p0);
				return p -> {
					System.out.println(p.getPageSequenceNumber());
					if (p.getPageSequenceNumber() == 0) {
						OpusHeader oh = new OpusHeader();
						oh.read(p);
						log.info(oh);
					}
				};
			});

			processor.process(ogg);

		}
	}
}
