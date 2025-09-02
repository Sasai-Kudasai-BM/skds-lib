package net.skds.lib2.io;

import net.skds.lib2.mat.ByteArrayPrimitiveOperations;
import net.skds.lib2.mat.VarInt;
import net.skds.lib2.mat.VarLong;
import net.skds.lib2.mat.quat.Quat;
import net.skds.lib2.mat.vec3.Vec3;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ExtendedDataOutput extends DataOutput {

	@Override
	@Deprecated(forRemoval = true)
	default void writeBytes(String s) {
		throw new UnsupportedOperationException();
	}


	@Override
	@Deprecated(forRemoval = true)
	default void writeChars(String s) {
		throw new UnsupportedOperationException();
	}


	@Override
	@Deprecated(forRemoval = true)
	default void writeUTF(String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	default void write(int b) throws IOException {
		writeByte(b);
	}

	void writeFromByteBuffer(ByteBuffer buffer, int offset, int length) throws IOException;

	default void writeFromByteBuffer(ByteBuffer buffer) throws IOException {
		writeFromByteBuffer(buffer, buffer.position(), buffer.remaining());
		buffer.position(buffer.limit());
	}

	default void writeSizedString(String s) throws IOException {
		if (s.isEmpty()) {
			writeVarInt(0);
			return;
		}
		byte[] data = s.getBytes(StandardCharsets.UTF_8);
		writeVarInt(data.length);
		write(data);
	}


	default <T> void writeOptional(Optional<T> op, DataWriter<T> consumer) throws IOException {
		if (op.isPresent()) {
			this.writeBoolean(true);
			consumer.write(this, op.get());
		} else {
			this.writeBoolean(false);
		}
	}

	default <T> void writeOptional(T op, DataWriter<T> consumer) throws IOException {
		if (op != null) {
			this.writeBoolean(true);
			consumer.write(this, op);
		} else {
			this.writeBoolean(false);
		}
	}

	default void writeBitSet(BitSet bitSet) throws IOException {
		if (bitSet == null || bitSet.isEmpty()) {
			writeVarInt(0);
		} else {
			long[] array = bitSet.toLongArray();
			writeVarInt(array.length);
			for (int i = 0; i < array.length; i++) {
				writeLong(array[i]);
			}
		}
	}

	default void writeVarInt(int i) throws IOException {
		VarInt.write(this, i);
	}

	default void writeVarLong(long value) throws IOException {
		VarLong.write(this, value);
	}

	default void writeUUID(UUID uuid) throws IOException {
		writeLong(uuid.getMostSignificantBits());
		writeLong(uuid.getLeastSignificantBits());
	}

	default void writeByteArray(byte[] array) throws IOException {
		if (array == null) {
			writeVarInt(0);
			return;
		}
		writeVarInt(array.length);
		write(array);
	}

	default void writeCharArray(char[] array) throws IOException {
		if (array == null) {
			writeVarInt(0);
			return;
		}
		writeVarInt(array.length);
		for (int i = 0; i < array.length; i++) {
			writeChar(array[i]);
		}
	}

	default void writeShortArray(short[] array) throws IOException {
		if (array == null) {
			writeVarInt(0);
			return;
		}
		writeVarInt(array.length);
		for (int i = 0; i < array.length; i++) {
			writeShort(array[i]);
		}
	}

	default void writeIntArray(int[] array) throws IOException {
		if (array == null) {
			writeVarInt(0);
			return;
		}
		writeVarInt(array.length);
		for (int i = 0; i < array.length; i++) {
			writeInt(array[i]);
		}
	}

	default void writeLongArray(long[] array) throws IOException {
		if (array == null) {
			writeVarInt(0);
			return;
		}
		writeVarInt(array.length);
		for (int i = 0; i < array.length; i++) {
			writeLong(array[i]);
		}
	}

	default void writeFloatArray(float[] array) throws IOException {
		if (array == null) {
			writeVarInt(0);
			return;
		}
		writeVarInt(array.length);
		for (int i = 0; i < array.length; i++) {
			writeFloat(array[i]);
		}
	}

	default void writeDoubleArray(double[] array) throws IOException {
		if (array == null) {
			writeVarInt(0);
			return;
		}
		writeVarInt(array.length);
		for (int i = 0; i < array.length; i++) {
			writeDouble(array[i]);
		}
	}

	default <T> void writeCollection(Collection<T> collection, DataWriter<T> consumer) throws IOException {
		writeVarInt(collection.size());
		for (T object : collection) {
			consumer.write(this, object);
		}
	}

	default <T> void writeArray(T[] array, DataWriter<T> consumer) throws IOException {
		writeVarInt(array.length);
		for (T object : array) {
			consumer.write(this, object);
		}
	}

	default void writeVarIntEnum(Enum<?> value) throws IOException {
		writeVarInt(value.ordinal());
	}

	default void writeByteEnum(Enum<?> value) throws IOException {
		writeByte(value.ordinal());
	}

	default void writeFloatQuat(Quat q) throws IOException {
		writeFloat(q.xf());
		writeFloat(q.yf());
		writeFloat(q.zf());
		writeFloat(q.wf());
	}

	default void writeDoubleVector(Vec3 vec) throws IOException {
		writeDouble(vec.x());
		writeDouble(vec.y());
		writeDouble(vec.z());
	}

	default void writeFloatVector(Vec3 vec) throws IOException {
		writeFloat(vec.xf());
		writeFloat(vec.yf());
		writeFloat(vec.zf());
	}

	default OutputStream getAsOutputStream() {
		return new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				ExtendedDataOutput.this.writeByte(b);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				ExtendedDataOutput.this.write(b, off, len);
			}
		};
	}

	static ExtendedDataOutput wrap(DataOutput out) {
		return new ExtendedDataOutput() {

			@Override
			public void writeFromByteBuffer(ByteBuffer buffer, int offset, int length) throws IOException {
				if (length < 1) return;
				if (!buffer.isDirect() && !buffer.isReadOnly()) {
					byte[] array = buffer.array();
					out.write(array, offset, length);
				} else {
					final int bufferSize = Math.min(8196, length);
					int rem = length;
					byte[] array = new byte[bufferSize];
					do {
						buffer.get(array);
						out.write(array);
						rem -= bufferSize;
					} while (rem > 0);
				}
			}

			@Override
			public void write(byte[] b) throws IOException {
				out.write(b);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				out.write(b, off, len);
			}

			@Override
			public void writeBoolean(boolean v) throws IOException {
				out.writeBoolean(v);
			}

			@Override
			public void writeByte(int v) throws IOException {
				out.writeByte(v);
			}

			@Override
			public void writeShort(int v) throws IOException {
				out.writeShort(v);
			}

			@Override
			public void writeChar(int v) throws IOException {
				out.writeChar(v);
			}

			@Override
			public void writeInt(int v) throws IOException {
				out.writeInt(v);
			}

			@Override
			public void writeLong(long v) throws IOException {
				out.writeLong(v);
			}

			@Override
			public void writeFloat(float v) throws IOException {
				out.writeFloat(v);
			}

			@Override
			public void writeDouble(double v) throws IOException {
				out.writeDouble(v);
			}
		};
	}

	static ExtendedDataOutput wrap(OutputStream out) {
		return new ExtendedDataOutput() {
			final byte[] writeBuffer = new byte[8];

			@Override
			public OutputStream getAsOutputStream() {
				return out;
			}

			@Override
			public void writeFromByteBuffer(ByteBuffer buffer, int offset, int length) throws IOException {
				if (length < 1) return;
				if (!buffer.isDirect() && !buffer.isReadOnly()) {
					byte[] array = buffer.array();
					out.write(array, offset, length);
				} else {
					final int bufferSize = Math.min(8196, length);
					int rem = length;
					byte[] array = new byte[bufferSize];
					do {
						buffer.get(array);
						out.write(array);
						rem -= bufferSize;
					} while (rem > 0);
				}
			}

			@Override
			public void write(byte[] b) throws IOException {
				out.write(b);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				out.write(b, off, len);
			}

			@Override
			public void writeBoolean(boolean v) throws IOException {
				out.write(v ? 1 : 0);
			}

			@Override
			public void writeByte(int v) throws IOException {
				out.write(v);
			}

			@Override
			public void writeShort(int v) throws IOException {
				ByteArrayPrimitiveOperations.setShort(writeBuffer, 0, (short) v);
				out.write(writeBuffer, 0, 2);
			}

			@Override
			public void writeChar(int v) throws IOException {
				ByteArrayPrimitiveOperations.setChar(writeBuffer, 0, (char) v);
				out.write(writeBuffer, 0, 2);
			}

			@Override
			public void writeInt(int v) throws IOException {
				ByteArrayPrimitiveOperations.setInt(writeBuffer, 0, v);
				out.write(writeBuffer, 0, 4);
			}

			@Override
			public void writeLong(long v) throws IOException {
				ByteArrayPrimitiveOperations.setLong(writeBuffer, 0, v);
				out.write(writeBuffer, 0, 8);
			}

			@Override
			public void writeFloat(float v) throws IOException {
				ByteArrayPrimitiveOperations.setFloat(writeBuffer, 0, v);
				out.write(writeBuffer, 0, 4);
			}

			@Override
			public void writeDouble(double v) throws IOException {
				ByteArrayPrimitiveOperations.setDouble(writeBuffer, 0, v);
				out.write(writeBuffer, 0, 8);
			}
		};
	}
}
