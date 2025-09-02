package net.skds.lib2.misc.ogg;

import lombok.Getter;
import net.skds.lib2.io.codec.CodecUtils;
import net.skds.lib2.io.codec.annotation.SkipSerialization;
import net.skds.lib2.mat.ByteArrayPrimitiveOperations;
import net.skds.lib2.utils.SKDSByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Getter
public class OggPage {

	private static final int MAGIC = (('O' << 8 | 'g') << 8 | 'g') << 8 | 'S';
	private static final int MAGIC_CRC = OggCRC32.getMagicCRC(MAGIC);
	private static final byte MAGIC_START = (byte) 'O';
	public static final int MINIMUM_PAGE_SIZE = 27;

	int version;
	int headerType;
	long granulePosition;
	int bitstreamSerialNumber;
	int pageSequenceNumber;
	int crcChecksum;
	int pageSegments;
	int segmentTable;

	byte[] data;
	int crc32 = MAGIC_CRC;

	@Getter
	OggPageReadState status = OggPageReadState.NONE;

	@Getter
	@SkipSerialization
	OggMediaType mediaType;

	public OggPageReadState read(InputStream in) throws IOException {
		if (this.status == OggPageReadState.NONE) {
			if (!findMagic(in)) {
				return OggPageReadState.NONE;
			}
			SKDSByteBuf input = new SKDSByteBuf(ByteBuffer.wrap(in.readNBytes(MINIMUM_PAGE_SIZE - 4)).order(ByteOrder.LITTLE_ENDIAN));

			this.version = input.readUnsignedByte();
			this.headerType = input.readUnsignedByte();
			this.granulePosition = input.readLong();
			this.bitstreamSerialNumber = input.readInt();
			this.pageSequenceNumber = input.readInt();
			this.crcChecksum = input.readInt();
			this.pageSegments = input.readUnsignedByte();

			input.getBuffer().putInt(18, 0);
			crc32 = OggCRC32.getCRC(input.array(), crc32);
			this.status = OggPageReadState.HEADER;
		}
		if (this.status == OggPageReadState.HEADER) {
			if (in.available() < pageSegments) return OggPageReadState.HEADER;
			SKDSByteBuf input = new SKDSByteBuf(in.readNBytes(pageSegments));
			crc32 = OggCRC32.getCRC(input.array(), crc32);

			int tableSize = 0;
			for (int i = 0; i < pageSegments; i++) {
				tableSize += input.readUnsignedByte();
			}
			this.segmentTable = tableSize;
			this.status = OggPageReadState.TABLE;
		}
		if (this.status == OggPageReadState.TABLE) {
			if (in.available() < segmentTable) return OggPageReadState.TABLE;
			byte[] d = in.readNBytes(segmentTable);
			this.crc32 = OggCRC32.getCRC(d, crc32);
			this.data = d;
			if (isBOS()) {
				this.mediaType = OggMediaType.readFromBody(d);
			}
			this.status = OggPageReadState.FULL;
		}
		return OggPageReadState.FULL;
	}

	public boolean validate() {
		return crcChecksum == crc32;
	}

	private boolean findMagic(InputStream in) throws IOException {
		while (in.available() > MINIMUM_PAGE_SIZE) {
			if (in.read() == 'O' && in.read() == 'g' && in.read() == 'g' && in.read() == 'S') {
				return true;
			}
		}
		return false;
	}

	protected byte[] getHeader() {
		byte[] header = new byte[MINIMUM_PAGE_SIZE + pageSegments];
		ByteArrayPrimitiveOperations.setInt(header, 0, MAGIC);

		header[4] = 0; // Version
		header[5] = (byte) headerType;

		ByteArrayPrimitiveOperations.setLong(header, 6, granulePosition);
		ByteArrayPrimitiveOperations.setInt(header, 14, bitstreamSerialNumber);
		ByteArrayPrimitiveOperations.setInt(header, 18, pageSequenceNumber);

		// Checksum @ 22 left blank for now

		header[26] = (byte) pageSegments;
		Arrays.fill(header, 27, 26 + pageSegments, (byte) 255);
		header[26 + pageSegments] = (byte) (segmentTable % 255);

		return header;
	}

	public boolean isFresh() {
		return (headerType & 0x1) == 0;
	}

	public boolean isBOS() {
		return (headerType & 0x2) != 0;
	}

	public boolean isEOS() {
		return (headerType & 0x4) != 0;
	}

	@Override
	public String toString() {
		return CodecUtils.toJson(this);
	}
}
