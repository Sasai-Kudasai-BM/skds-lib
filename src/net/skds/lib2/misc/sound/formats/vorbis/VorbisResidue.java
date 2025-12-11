package net.skds.lib2.misc.sound.formats.vorbis;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.SerializeOnlyCodec;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.misc.fields.IntField2D;
import net.skds.lib2.misc.fields.IntField2DImpl;

import java.io.IOException;
import java.lang.reflect.Type;

public class VorbisResidue extends VorbisFloor {

	int begin;
	int end;
	int partitionSize;
	int classbook;
	@DefaultCodec(StringCodec.class)
	IntField2D books;

	@Override
	public void read(ByteArrayBitInputStream input) throws IOException {

		int classifications;

		begin = input.readInt(24);
		end = input.readInt(24);
		partitionSize = input.readInt(24) + 1;
		classifications = input.readInt(6) + 1;
		classbook = input.readInt(8);

		byte[] cascade = new byte[classifications];
		for (int i = 0; i < classifications; i++) {
			int highBits = 0;
			int lowBits = input.readInt(3);
			if (input.readBit()) {
				highBits = input.readInt(5);
			}
			cascade[i] = (byte) ((highBits << 3) | lowBits);
		}
		books = new IntField2DImpl(classifications, 8);
		for (int i = 0; i < classifications; i++) {
			for (int j = 0; j < 8; j++) {
				if ((cascade[i] & (1 << j)) != 0) {
					books.setValue(input.readInt(8), i, j);
				} else {
					books.setValue(-1, i, j);
				}
			}
		}
	}

	private static class StringCodec extends SerializeOnlyCodec<IntField2D> {

		public StringCodec(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(IntField2D value, UniversalWriter writer) throws IOException {
			int w = value.width();
			int h = value.height();
			String sb = "Field " +
					w + 'x' +
					h;

			writer.writeString(sb);
		}
	}
}
