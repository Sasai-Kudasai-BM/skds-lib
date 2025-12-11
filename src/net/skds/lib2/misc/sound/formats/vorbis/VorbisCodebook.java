package net.skds.lib2.misc.sound.formats.vorbis;

import net.skds.lib2.io.codec.SosisonUtils;
import net.skds.lib2.io.exception.ParseException;

import java.io.IOException;

public class VorbisCodebook implements VorbisReadable {

	public static final int SYNC_PATTERN = 0x564342;

	int[] multiplicands;
	float min;
	float delta;
	boolean sequence;
	int lookupType;
	int entries;
	int dimensions;
	int lookupValues;
	int[] codewordLengths;


	@Override
	public void read(ByteArrayBitInputStream input) throws IOException {
		int sync = input.readInt(24);
		if (sync != SYNC_PATTERN) throw new ParseException("Wrong sync pattern " + Integer.toUnsignedString(sync, 16));

		dimensions = input.readInt(16);
		entries = input.readInt(24);

		codewordLengths = new int[entries];
		boolean ordered = input.readBit();
		if (ordered) {
			int currentLength = input.readInt(5) + 1;
			for (int currentEntry = 0; currentEntry < entries; currentEntry++) {
				int number = VorbisUtils.iLog(entries - currentEntry);
				for (int i = 0; i < number; i++) {
					codewordLengths[i + currentEntry] = currentLength;
				}
				currentEntry += number;
			}
		} else {
			if (input.readBit()) { // sparse
				for (int i = 0; i < entries; i++) {
					if (input.readBit()) { // flag
						codewordLengths[i] = input.readInt(5) + 1;
					} else {
						codewordLengths[i] = -1;
					}
				}
			} else for (int i = 0; i < entries; i++) {
				codewordLengths[i] = input.readInt(5) + 1;
			}
		}

		lookupType = input.readInt(4);
		int valueBits;

		switch (lookupType) {
			case 0 -> {
			}
			case 1, 2 -> {
				min = VorbisUtils.intBits2Float(input.readInt(32));
				delta = VorbisUtils.intBits2Float(input.readInt(32));
				valueBits = input.readInt(4) + 1;
				sequence = input.readBit();
				if (lookupType == 1) {
					lookupValues = (int) VorbisUtils.lookup1Values(entries, dimensions);
				} else {
					lookupValues = entries * dimensions;
				}
				this.multiplicands = new int[lookupValues];
				for (int i = 0; i < lookupValues; i++) {
					this.multiplicands[i] = input.readInt(valueBits);
				}
			}
			default -> throw new ParseException("Unsupported lookupType " + lookupType);
		}

	}

	@Override
	public String toString() {
		return SosisonUtils.toJson(this);
	}
}
