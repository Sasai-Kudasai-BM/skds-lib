package net.skds.lib2.misc.sound.formats.vorbis;

import net.skds.lib2.io.codec.SosisonUtils;
import net.skds.lib2.io.exception.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class AbstractVorbisHeader implements VorbisReadable {

	public static final byte[] VORBIS_SEQ = "vorbis".getBytes(StandardCharsets.UTF_8);

	final transient int packetType;

	public AbstractVorbisHeader(int type) {
		this.packetType = type;
	}

	@Override
	public void read(ByteArrayBitInputStream input) throws IOException {
		if (this.packetType != input.readAlignedInt(1)) {
			throw new ParseException("Wrong type");
		}
		if (!Arrays.equals(VORBIS_SEQ, input.readAlignedBytes(VORBIS_SEQ.length))) {
			throw new ParseException("Wrong vorbis sequence");
		}
	}

	protected abstract boolean validate();

	@Override
	public String toString() {
		return SosisonUtils.toJson(this);
	}
}
