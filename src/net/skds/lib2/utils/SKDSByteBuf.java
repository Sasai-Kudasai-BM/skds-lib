package net.skds.lib2.utils;

import lombok.Getter;
import net.skds.lib2.io.ExtendedDataInput;
import net.skds.lib2.io.ExtendedDataOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

//@SuppressWarnings("unused")
@Getter
public final class SKDSByteBuf implements ExtendedDataInput, ExtendedDataOutput {
	private final ByteBuffer buffer;

	public SKDSByteBuf(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	public SKDSByteBuf(byte[] array) {
		this.buffer = ByteBuffer.wrap(array);
	}

	public static SKDSByteBuf allocate(int size) {
		return new SKDSByteBuf(ByteBuffer.allocate(size));
	}

	public void clear() {
		buffer.clear();
	}

	public String remainingAsString() {
		return new String(buffer.array(), buffer.position(), buffer.remaining(), StandardCharsets.UTF_8);
	}

	public byte[] getBytes(int length) {
		byte[] bytes = new byte[length];
		buffer.get(bytes);
		return bytes;
	}

	public void getBytes(byte[] bytes) {
		buffer.get(bytes);
	}

	public SKDSByteBuf remainingAsCopy() {
		byte[] data = new byte[buffer.remaining()];
		buffer.get(data);
		return new SKDSByteBuf(ByteBuffer.wrap(data));
	}

	public byte[] remainingAsArray() {
		byte[] data = new byte[buffer.remaining()];
		buffer.get(data);
		return data;
	}

	public byte[] array() {
		return buffer.array();
	}

	public boolean canRead(int bytes) {
		return buffer.remaining() >= bytes;
	}

	public void rewind() {
		buffer.rewind();
	}

	public SKDSByteBuf flip() {
		buffer.flip();
		return this;
	}

	public int skip(int bytes) {
		buffer.position(buffer.position() + bytes);
		return bytes;
	}

	public void position(int newPos) {
		buffer.position(newPos);
	}

	public void mark() {
		buffer.mark();
	}

	public void reset() {
		buffer.reset();
	}

	public int capacity() {
		return buffer.capacity();
	}

	public int remaining() {
		return buffer.remaining();
	}

	public boolean hasRemaining() {
		return buffer.hasRemaining();
	}

	public int position() {
		return buffer.position();
	}

	public SKDSByteBuf slice(int index, int length) {
		return new SKDSByteBuf(buffer.slice(index, length));
	}

	public OutputStream getOutputStream() {
		return new BufferOutputStream();
	}

	public InputStream getInputStream() {
		return new BufferInputStream();
	}

	@Override
	public void write(byte[] b) throws IOException {
		buffer.put(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		buffer.put(b, off, len);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException {
		buffer.put(v ? (byte) 1 : 0);
	}

	@Override
	public void writeByte(int v) throws IOException {
		buffer.put((byte) v);
	}

	@Override
	public void writeShort(int v) throws IOException {
		buffer.putShort((short) v);
	}

	@Override
	public void writeChar(int v) throws IOException {
		buffer.putChar((char) v);
	}

	@Override
	public void writeInt(int v) throws IOException {
		buffer.putInt(v);
	}

	@Override
	public void writeLong(long v) throws IOException {
		buffer.putLong(v);
	}

	@Override
	public void writeFloat(float v) throws IOException {
		buffer.putFloat(v);
	}

	@Override
	public void writeDouble(double v) throws IOException {
		buffer.putDouble(v);
	}

	@Override
	public void readToByteBuffer(ByteBuffer buffer, int offset, int length) {
		buffer.put(this.buffer.position(), this.buffer, offset, length);
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		buffer.get(b);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		buffer.get(b, off, len);
	}

	@Override
	public int skipBytes(int n) throws IOException {
		return skip(n);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return buffer.get() != 0;
	}

	@Override
	public byte readByte() throws IOException {
		return buffer.get();
	}

	@Override
	public int readUnsignedByte() {
		return buffer.get() & 0xff;
	}

	@Override
	public short readShort() {
		return buffer.getShort();
	}

	@Override
	public int readUnsignedShort() {
		return buffer.getShort() & 0xffff;
	}

	@Override
	public char readChar() throws IOException {
		return buffer.getChar();
	}

	@Override
	public int readInt() throws IOException {
		return buffer.getInt();
	}

	@Override
	public long readLong() throws IOException {
		return buffer.getLong();
	}

	@Override
	public float readFloat() throws IOException {
		return buffer.getFloat();
	}

	@Override
	public double readDouble() throws IOException {
		return buffer.getDouble();
	}

	@Override
	public void writeFromByteBuffer(ByteBuffer buffer, int offset, int length) {
		this.buffer.put(this.buffer.position(), buffer, offset, length);
	}

	private class BufferOutputStream extends OutputStream {
		@Override
		public void write(int b) {
			buffer.put((byte) b);
		}

		@Override
		public void write(byte[] b) {
			buffer.put(b);
		}

		@Override
		public void write(byte[] b, int off, int len) {
			buffer.put(b, off, len);
		}
	}

	private class BufferInputStream extends InputStream {
		@Override
		public int read() {
			if (buffer.remaining() <= 0) {
				return -1;
			}
			return Byte.toUnsignedInt(buffer.get());
		}

		@Override
		public int read(byte[] b) {
			int count = Math.min(b.length, buffer.remaining());
			buffer.get(b, 0, count);
			return count;
		}

		@Override
		public int read(byte[] b, int off, int len) {
			int count = Math.min(b.length, buffer.remaining());
			count = Math.min(count, len);
			buffer.get(b, off, count);
			return count;
		}
	}

}