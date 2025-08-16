package net.skds.lib2.io;

import net.skds.lib2.mat.ByteArrayPrimitiveOperations;
import net.skds.lib2.mat.VarInt;
import net.skds.lib2.mat.VarLong;
import net.skds.lib2.mat.quat.QuatF;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec3.Vec3D;
import net.skds.lib2.mat.vec3.Vec3F;
import net.skds.lib2.utils.ArrayUtils;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;

public interface ExtendedDataInput extends DataInput {

	@Override
	@Deprecated(forRemoval = true)
	default String readLine() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated(forRemoval = true)
	default String readUTF() {
		throw new UnsupportedOperationException();
	}

	void readToByteBuffer(ByteBuffer buffer, int offset, int length) throws IOException;

	default void readToByteBuffer(ByteBuffer buffer) throws IOException {
		readToByteBuffer(buffer, buffer.position(), buffer.remaining());
		buffer.position(buffer.limit());
	}

	default byte[] readBytes(int count) throws IOException {
		byte[] array = new byte[count];
		readFully(array, 0, count);
		return array;
	}

	default String readStringBytes(int count) throws IOException {
		byte[] array = new byte[count];
		readFully(array, 0, count);
		return new String(array, StandardCharsets.UTF_8);
	}

	default int readVarInt() throws IOException {
		return VarInt.read(this);
	}

	default long readVarLong() throws IOException {
		return VarLong.read(this);
	}

	default String readSizedString() throws IOException {
		int l = readVarInt();
		if (l == 0) return "";
		byte[] data = new byte[l];
		readFully(data);
		return new String(data, StandardCharsets.UTF_8);
	}

	default String readSizedString(int maxLength) throws IOException {
		int l = readVarInt();
		if (l == 0) return "";
		if (l > maxLength) throw new IOException("Too large string sized " + l + " bytes, but limit is " + maxLength);
		byte[] data = new byte[l];
		readFully(data);
		return new String(data, StandardCharsets.UTF_8);
	}

	default BitSet readBitSet() throws IOException {
		int l = readVarInt();
		if (l == 0) return new BitSet(0);
		long[] data = new long[l];
		for (int i = 0; i < l; i++) {
			data[i] = readLong();
		}
		return BitSet.valueOf(data);
	}


	default UUID readUUID() throws IOException {
		return new UUID(readLong(), readLong());
	}

	default byte[] readByteArray() throws IOException {
		int l = readVarInt();
		if (l == 0) return ArrayUtils.EMPTY_BYTE;
		byte[] data = new byte[l];
		readFully(data);
		return data;
	}


	default <T> List<T> readCollection(DataReader<T> reader) throws IOException {
		final int size = readVarInt();
		final ArrayList<T> list = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			list.add(i, reader.read(this));
		}
		return list;
	}

	default <T> T[] readArray(DataReader<T> reader, Class<T> type) throws IOException {
		final int size = readVarInt();
		final T[] list = ArrayUtils.createGenericArray(type, size);
		for (int i = 0; i < size; i++) {
			list[i] = reader.read(this);
		}
		return list;
	}

	default <T extends Enum<T>> T readVarIntEnum(Class<T> type) throws IOException {
		int index = readVarInt();
		return type.getEnumConstants()[index];
	}

	default <T extends Enum<T>> T readByteEnum(Class<T> type) throws IOException {
		return type.getEnumConstants()[readByte()];
	}

	default Vec3D readDoubleVector() throws IOException {
		return new Vec3D(readDouble(), readDouble(), readDouble());
	}

	default Vec3F readFloatVector(Vec3 vec) throws IOException {
		return new Vec3F(readFloat(), readFloat(), readFloat());
	}

	default QuatF readFloatQuat(Vec3 vec) throws IOException {
		return new QuatF(readFloat(), readFloat(), readFloat(), readFloat());
	}

	default InputStream getAsInputStream() {
		return new InputStream() {
			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				ExtendedDataInput.this.readFully(b, off, len);
				return len;
			}

			@Override
			public int read() throws IOException {
				return readByte() & 0xFF;
			}

			@Override
			public long skip(long n) throws IOException {
				return ExtendedDataInput.this.skipBytes((int) n);
			}

			@Override
			public void skipNBytes(long n) throws IOException {
				ExtendedDataInput.this.skipBytes((int) n);
			}
		};
	}

	static ExtendedDataInput wrap(DataInput in) {
		return new ExtendedDataInput() {
			@Override
			public void readToByteBuffer(ByteBuffer buffer, int offset, int length) throws IOException {
				if (length < 1) return;
				byte[] array;
				if (!buffer.isDirect() && !buffer.isReadOnly()) {
					array = buffer.array();
					buffer.get(array);
					in.readFully(array);
				} else {
					final int bufferSize = Math.min(8196, length);
					array = new byte[bufferSize];
					int rem = length;
					do {
						buffer.get(array);
						in.readFully(array);
						rem -= bufferSize;
						buffer.put(array, offset + length - rem, bufferSize);
					} while (rem > 0);
				}
			}

			@Override
			public void readFully(byte[] b) throws IOException {
				in.readFully(b);
			}

			@Override
			public void readFully(byte[] b, int off, int len) throws IOException {
				in.readFully(b, off, len);
			}

			@Override
			public int skipBytes(int n) throws IOException {
				return in.skipBytes(n);
			}

			@Override
			public boolean readBoolean() throws IOException {
				return in.readBoolean();
			}

			@Override
			public byte readByte() throws IOException {
				return in.readByte();
			}

			@Override
			public int readUnsignedByte() throws IOException {
				return in.readUnsignedByte();
			}

			@Override
			public short readShort() throws IOException {
				return in.readShort();
			}

			@Override
			public int readUnsignedShort() throws IOException {
				return in.readUnsignedShort();
			}

			@Override
			public char readChar() throws IOException {
				return in.readChar();
			}

			@Override
			public int readInt() throws IOException {
				return in.readInt();
			}

			@Override
			public long readLong() throws IOException {
				return in.readLong();
			}

			@Override
			public float readFloat() throws IOException {
				return in.readFloat();
			}

			@Override
			public double readDouble() throws IOException {
				return in.readDouble();
			}
		};
	}

	static ExtendedDataInput wrap(InputStream in) {
		return new ExtendedDataInput() {

			final byte[] readBuffer = new byte[8];

			@Override
			public InputStream getAsInputStream() {
				return in;
			}

			@Override
			public void readToByteBuffer(ByteBuffer buffer, int offset, int length) throws IOException {
				if (length < 1) return;
				int rem = length;
				byte[] array;
				if (!buffer.isDirect() && !buffer.isReadOnly()) {
					array = buffer.array();
					do {
						buffer.get(array);
						rem -= in.read(array);
					} while (rem > 0);
				} else {
					final int bufferSize = Math.min(8196, length);
					array = new byte[bufferSize];
					do {
						buffer.get(array);
						int r = in.read(array);
						rem -= r;
						buffer.put(array, offset + length - rem, r);
					} while (rem > 0);
				}
			}

			@Override
			public void readFully(byte[] b) throws IOException {
				int rem = b.length;
				do {
					int r = in.read(b, b.length - rem, rem);
					if (r < 0) throw new EOFException();
					rem -= r;
				} while (rem > 0);
			}

			@Override
			public void readFully(byte[] b, int off, int len) throws IOException {
				int rem = len;
				do {
					int r = in.read(b, len - rem, rem);
					if (r < 0) throw new EOFException();
					rem -= r;
				} while (rem > 0);
			}

			@Override
			public int skipBytes(int n) throws IOException {
				return (int) in.skip(n);
			}

			@Override
			public boolean readBoolean() throws IOException {
				return readUnsignedByte() != 0;
			}

			@Override
			public byte readByte() throws IOException {
				return (byte) readUnsignedByte();
			}

			@Override
			public int readUnsignedByte() throws IOException {
				int ch = in.read();
				if (ch < 0) throw new EOFException();
				return ch;
			}

			@Override
			public short readShort() throws IOException {
				readFully(readBuffer, 0, 2);
				return ByteArrayPrimitiveOperations.getShort(readBuffer, 0);
			}

			@Override
			public int readUnsignedShort() throws IOException {
				readFully(readBuffer, 0, 2);
				return ByteArrayPrimitiveOperations.getUnsignedShort(readBuffer, 0);
			}

			@Override
			public char readChar() throws IOException {
				readFully(readBuffer, 0, 2);
				return ByteArrayPrimitiveOperations.getChar(readBuffer, 0);
			}

			@Override
			public int readInt() throws IOException {
				readFully(readBuffer, 0, 4);
				return ByteArrayPrimitiveOperations.getInt(readBuffer, 0);
			}

			@Override
			public long readLong() throws IOException {
				readFully(readBuffer, 0, 8);
				return ByteArrayPrimitiveOperations.getLong(readBuffer, 0);
			}

			@Override
			public float readFloat() throws IOException {
				readFully(readBuffer, 0, 4);
				return ByteArrayPrimitiveOperations.getFloat(readBuffer, 0);
			}

			@Override
			public double readDouble() throws IOException {
				readFully(readBuffer, 0, 8);
				return ByteArrayPrimitiveOperations.getDouble(readBuffer, 0);
			}

		};
	}

}
