package net.skds.lib2.misc.sound.formats.vorbis;

import lombok.Getter;
import net.skds.lib2.io.exception.ParseException;

import java.io.IOException;

@Getter
public class VorbisIdentificationHeader extends AbstractVorbisHeader {

	private long vorbisVersion;
	private int channels;
	private long sampleRate;
	private int maxBitrate;
	private int normalBitrate;
	private int minBitrate;
	private int blockSize0;
	private int blockSize1;

	public VorbisIdentificationHeader() {
		super(1);
	}

	@Override
	public void read(ByteArrayBitInputStream input) throws IOException {
		super.read(input);

		this.vorbisVersion = input.readAlignedInt(4) & 0xFFFFFFFFL;
		this.channels = input.readAlignedInt(1);
		this.sampleRate = input.readAlignedInt(4) & 0xFFFFFFFFL;
		this.maxBitrate = input.readAlignedInt(4);
		this.normalBitrate = input.readAlignedInt(4);
		this.minBitrate = input.readAlignedInt(4);
		int blockSize = input.readAlignedInt(1);
		this.blockSize1 = 1 << (blockSize >> 4);
		this.blockSize0 = 1 << (blockSize & 0xF);

		boolean farmingFlag = input.readBit();
		if (!farmingFlag || !validate()) throw new ParseException("Validation failed");
	}

	@Override
	protected boolean validate() {
		if (vorbisVersion == 0) {
			boolean ok = channels > 0;
			ok &= sampleRate > 0;
			ok &= blockSize0 <= 8192;
			ok &= blockSize0 >= 64;
			ok &= blockSize1 <= 8192;
			ok &= blockSize1 >= 64;
			ok &= blockSize0 <= blockSize1;

			return ok;
		}
		return false;
	}
}
