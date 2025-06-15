package net.skds.lib2.io;

import net.skds.lib2.mat.ByteArrayPrimitiveOperations;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class ByteArrayExtendedDataOutput implements ExtendedDataOutput {

	private byte[] buffer;
	private int pos = 0;

	public ByteArrayExtendedDataOutput() {
		this.buffer = new byte[64];
	}

	public ByteArrayExtendedDataOutput(int initialCapacity) {
		this.buffer = new byte[initialCapacity];
	}

	private void ensureCapacity(int cap) {
		cap += this.pos;
		int ns = buffer.length;
		if (cap > ns) {
			do ns *= 2; while (cap > ns);
			this.buffer = Arrays.copyOf(this.buffer, ns);
		}
	}

	@Override
	public void write(byte[] b) {
		ensureCapacity(b.length);
		System.arraycopy(b, 0, this.buffer, this.pos, b.length);
		pos += b.length;
	}

	@Override
	public void write(byte[] b, int off, int len) {
		ensureCapacity(len);
		System.arraycopy(b, off, this.buffer, this.pos, len);
		pos += len;
	}

	@Override
	public void writeBoolean(boolean v) {
		ensureCapacity(1);
		buffer[pos++] = v ? (byte) 1 : 0;
	}

	@Override
	public void writeByte(int v) {
		ensureCapacity(1);
		buffer[pos++] = (byte) v;
	}

	@Override
	public void writeShort(int v) {
		ensureCapacity(2);
		ByteArrayPrimitiveOperations.setShort(buffer, pos, (short) v);
		pos += 2;
	}

	@Override
	public void writeChar(int v) {
		ensureCapacity(2);
		ByteArrayPrimitiveOperations.setChar(buffer, pos, (char) v);
		pos += 2;
	}

	@Override
	public void writeInt(int v) {
		ensureCapacity(4);
		ByteArrayPrimitiveOperations.setInt(buffer, pos, v);
		pos += 4;
	}

	@Override
	public void writeLong(long v) {
		ensureCapacity(8);
		ByteArrayPrimitiveOperations.setLong(buffer, pos, v);
		pos += 8;
	}

	@Override
	public void writeFloat(float v) {
		ensureCapacity(4);
		ByteArrayPrimitiveOperations.setFloat(buffer, pos, v);
		pos += 4;
	}

	@Override
	public void writeDouble(double v) {
		ensureCapacity(8);
		ByteArrayPrimitiveOperations.setDouble(buffer, pos, v);
		pos += 8;
	}

	public void reset() {
		pos = 0;
	}

	public void resetAndCap(int maxCapacity) {
		pos = 0;
		if (buffer.length > maxCapacity) {
			buffer = new byte[maxCapacity];
		}
	}

	public int size() {
		return pos;
	}

	public boolean isEmpty() {
		return pos == 0;
	}

	public void setPos(int newPos) {
		this.pos = newPos;
	}

	public byte[] rawArray() {
		return buffer;
	}

	public byte[] toByteArray() {
		return Arrays.copyOf(buffer, pos);
	}

	public void writeToOutput(DataOutput output) throws IOException {
		output.write(this.buffer, 0, pos);
	}

	public void writeToOutput(OutputStream output) throws IOException {
		output.write(this.buffer, 0, pos);
	}

	@Override
	public void writeFromByteBuffer(ByteBuffer buffer, int offset, int len) {
		ensureCapacity(len);
		buffer.get(offset, this.buffer, pos, len);
		pos += len;
	}
}
