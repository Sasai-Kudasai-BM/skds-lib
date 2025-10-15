package net.skds.lib2.network.intercom.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public enum PacketSizeType {
	B0(0) {
		@Override
		public void write(DataOutput output, int size) {
		}

		@Override
		public int read(DataInput input) {
			return 0;
		}
	},
	B8(1) {
		@Override
		public void write(DataOutput output, int size) throws IOException {
			output.writeByte(size);
		}

		@Override
		public int read(DataInput input) throws IOException {
			return input.readUnsignedByte();
		}
	},
	B16(2) {
		@Override
		public void write(DataOutput output, int size) throws IOException {
			output.writeShort(size);
		}

		@Override
		public int read(DataInput input) throws IOException {
			return input.readUnsignedShort();
		}
	},
	B32(4) {
		@Override
		public void write(DataOutput output, int size) throws IOException {
			output.writeInt(size);
		}

		@Override
		public int read(DataInput input) throws IOException {
			return input.readInt();
		}
	};

	public final int byteSize;
	public final int limit;

	public static final PacketSizeType[] VALUES = values();

	PacketSizeType(int byteSize) {
		this.byteSize = byteSize;
		this.limit = (1 << (byteSize * 8)) - 1;
	}

	public abstract void write(DataOutput output, int size) throws IOException;

	public abstract int read(DataInput input) throws IOException;

	public static PacketSizeType getForSize(int size) {
		for (int i = 0; i < VALUES.length; i++) {
			PacketSizeType t = VALUES[i];
			if (size <= t.limit) return t;
		}
		throw new RuntimeException("Unsupported packet size/id " + size);
	}

}
