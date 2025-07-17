package net.skds.lib2.misc.ogg;

import lombok.Getter;
import net.skds.lib2.io.exception.WrongFormatException;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.mat.ByteArrayPrimitiveOperations;
import net.skds.lib2.utils.SKDSByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Getter
public class OggPage {

	private static final int MAGIC = (('O' << 8 | 'g') << 8 | 'g') << 8 | 'S';
	private static final int MINIMUM_PAGE_SIZE = 27;

	int magic;
	int version;
	int headerType;
	long granulePosition;
	int bitstreamSerialNumber;
	int pageSequenceNumber;
	int crcChecksum;
	int pageSegments;
	int segmentTable;

	byte[] data;
	int crc32;

	public void read(InputStream in) throws IOException {

		SKDSByteBuf input = new SKDSByteBuf(in.readNBytes(MINIMUM_PAGE_SIZE));

		this.magic = input.readInt();
		if (this.magic != MAGIC) {
			throw new WrongFormatException();
		}
		this.version = input.readUnsignedByte();
		this.headerType = input.readUnsignedByte();
		this.granulePosition = input.readLong();
		this.bitstreamSerialNumber = input.readInt();
		this.pageSequenceNumber = input.readInt();
		this.crcChecksum = input.readInt();
		int segments = this.pageSegments = input.readUnsignedByte();

		int tableSize = 0;

		input.getBuffer().putInt(22, 0);
		int crc = OggCRC32.getCRC(input.array());

		input = new SKDSByteBuf(in.readNBytes(segments));

		crc = OggCRC32.getCRC(input.array(), crc);

		for (int i = 0; i < segments; i++) {
			tableSize += input.readUnsignedByte();
			if (tableSize > 65307) {
				throw new IOException("Page is too large");
			}
		}
		this.segmentTable = tableSize;
		byte[] d = in.readNBytes(tableSize);
		this.data = d;

		//crc32 = OggCRC32.getCRC(getHeader());

		this.crc32 = OggCRC32.getCRC(d, crc);

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

	public boolean isContinue() {
		return (headerType & 0x1) != 0;
	}

	public boolean isFirstPage() {
		return (headerType & 0x2) != 0;
	}

	public boolean isLastPage() {
		return (headerType & 0x4) != 0;
	}

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
}
