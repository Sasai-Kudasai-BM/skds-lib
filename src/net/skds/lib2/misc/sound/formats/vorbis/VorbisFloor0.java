package net.skds.lib2.misc.sound.formats.vorbis;

import java.io.IOException;

public class VorbisFloor0 extends VorbisFloor {

	int order;
	int rate;
	int barkMapSize;
	int amplitudeBits;
	int amplitudeOffset;
	int[] bookList;

	@Override
	public void read(ByteArrayBitInputStream input) throws IOException {

		this.order = input.readInt(8);
		this.rate = input.readInt(16);
		this.barkMapSize = input.readInt(16);
		this.amplitudeBits = input.readInt(6);
		this.amplitudeOffset = input.readInt(8);
		int numberOfBook = input.readInt(4) + 1;
		this.bookList = new int[numberOfBook];
		for (int i = 0; i < numberOfBook; i++) {
			this.bookList[i] = input.readInt(8);
		}
	}

}
