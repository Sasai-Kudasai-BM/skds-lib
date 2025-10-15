package net.skds.lib2.network.intercom.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * <pre>
 *
 *  Header structure:
 *
 *  |  0   1  |     2       3      |   4   5   |    6   7    |
 *  +---------+--------------------+-----------+-------------+
 *  | type id | compression length | id length | size length |
 *  +---------+--------------------+-----------+-------------+
 *  |                       packet id                        |
 *  +--------------------------------------------------------+
 *  |               /- depends on id length -/               |
 *  +--------------------------------------------------------+
 *  |                      packet size                       |
 *  +--------------------------------------------------------+
 *  |              /- depends on size length -/              |
 *  +--------------------------------------------------------+
 *  |                decompressed packet size                |
 *  +--------------------------------------------------------+
 *  |         /- depends on decompressed size length -/      |
 *  +--------------------------------------------------------+
 * </pre>
 *
 * @param id   The id of application packet
 * @param size The size of application packet data
 * @apiNote Bundle uses id field as number of packets inside
 */
public record PacketHeader(PacketType type, int id, int size, boolean compression, int decompressedSize) {

	public static final int ID_MASK = 0xFF & 0B1100_0000;
	public static final int ID_SHIFT = Integer.numberOfTrailingZeros(ID_MASK);
	public static final int CMP_MASK = 0xFF & 0B0011_0000;
	public static final int CMP_SHIFT = Integer.numberOfTrailingZeros(CMP_MASK);
	public static final int PID_MASK = 0xFF & 0B0000_1100;
	public static final int PID_SHIFT = Integer.numberOfTrailingZeros(PID_MASK);
	public static final int P_SIZE_MASK = 0xFF & 0B0000_0011;

	public void write(DataOutput output) throws IOException {
		PacketSizeType idSize = PacketSizeType.getForSize(id);
		PacketSizeType dataSize = PacketSizeType.getForSize(size);
		PacketSizeType dSize = compression ? PacketSizeType.getForSize(decompressedSize) : PacketSizeType.B0;
		int head = type.maskedId | (dSize.ordinal() << CMP_SHIFT) | (idSize.ordinal() << PID_SHIFT) | dataSize.ordinal();
		output.writeByte(head);
		idSize.write(output, id);
		dataSize.write(output, size);
		dSize.write(output, decompressedSize);
	}

	public static void write(DataOutput output, PacketType type, int id, int size, int decompressedSize) throws IOException {
		PacketSizeType idSize = PacketSizeType.getForSize(id);
		PacketSizeType dataSize = PacketSizeType.getForSize(size);
		PacketSizeType dSize = (decompressedSize > 0) ? PacketSizeType.getForSize(decompressedSize) : PacketSizeType.B0;
		int head = type.maskedId | (dSize.ordinal() << CMP_SHIFT) | (idSize.ordinal() << PID_SHIFT) | dataSize.ordinal();
		output.writeByte(head);
		idSize.write(output, id);
		dataSize.write(output, size);
		dSize.write(output, decompressedSize);
	}

	public static PacketHeader read(DataInput input) throws IOException {
		int header = input.readUnsignedByte();
		PacketType type = PacketType.VALUES[(header & ID_MASK) >>> ID_SHIFT];
		PacketSizeType idSize = PacketSizeType.VALUES[(header & PID_MASK) >> PID_SHIFT];
		PacketSizeType dataSize = PacketSizeType.VALUES[header & P_SIZE_MASK];
		PacketSizeType dSize = PacketSizeType.VALUES[(header & CMP_MASK) >>> CMP_SHIFT];
		int id = idSize.read(input);
		int size = dataSize.read(input);
		int decompressedSize = dSize.read(input);
		return new PacketHeader(type, id, size, dSize.byteSize > 0, decompressedSize);
	}
}
