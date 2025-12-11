package net.skds.lib2.misc.sound.formats.vorbis;

import java.io.IOException;

public interface VorbisReadable {
	void read(ByteArrayBitInputStream input) throws IOException;
}
