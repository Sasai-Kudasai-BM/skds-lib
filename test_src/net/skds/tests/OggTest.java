package net.skds.tests;

import lombok.CustomLog;
import net.skds.lib2.misc.ogg.OggInputStream;
import net.skds.lib2.misc.ogg.OggMediaType;
import net.skds.lib2.misc.ogg.OggProcessor;
import net.skds.lib2.misc.sound.formats.opus.OpusCommentHeader;
import net.skds.lib2.misc.sound.formats.opus.OpusHeader;
import net.skds.lib2.misc.sound.formats.vorbis.VorbisProcessor;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

@CustomLog
public class OggTest {

	static void main() throws IOException {
		SKDSLogger.replaceOuts();

		try (OggInputStream ogg = new OggInputStream(new BufferedInputStream(new FileInputStream("run/sound/vorbis.ogg")))) {
			OggProcessor processor = new OggProcessor(p0 -> {
				try {
					System.out.println(p0);
					{
						OggMediaType type = OggMediaType.readFromBody(p0.getData());
						System.out.println(type);
						if (type == OggMediaType.VORBIS) {
							return new VorbisProcessor();
						}
					}

					// Opus
					return p -> {
						try {
							System.out.println(p.getPageSequenceNumber());
							if (p.getPageSequenceNumber() == 0) {
								OpusHeader oh = new OpusHeader(p);
								log.info(oh);
							} else if (p.getPageSequenceNumber() == 1) {
								OpusCommentHeader oh = new OpusCommentHeader(p);
								log.info(oh);
							}
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					};
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});

			processor.process(ogg);
		}
	}
}
