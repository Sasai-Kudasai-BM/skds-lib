package net.skds.lib2.misc.sound.formats.vorbis;

import lombok.Getter;
import net.skds.lib2.io.exception.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Getter
public class VorbisCommentHeader extends AbstractVorbisHeader {

	String vendor;
	String[] comments;

	public VorbisCommentHeader() {
		super(3);
	}

	@Override
	public void read(ByteArrayBitInputStream input) throws IOException {
		super.read(input);

		this.vendor = new String(input.readAlignedBytes(input.readAlignedInt(4)), StandardCharsets.UTF_8);
		int cl = input.readAlignedInt(4);
		this.comments = new String[cl];
		for (int i = 0; i < cl; i++) {
			this.comments[i] = new String(input.readAlignedBytes(input.readAlignedInt(4)), StandardCharsets.UTF_8);
		}

		boolean farmingFlag = input.readBit();
		if (!farmingFlag || !validate()) throw new ParseException("Validation failed");
	}

	@Override
	protected boolean validate() {
		return true;
	}
}
