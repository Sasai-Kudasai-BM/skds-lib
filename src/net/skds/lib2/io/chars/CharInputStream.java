package net.skds.lib2.io.chars;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

// TODO WIP
public class CharInputStream implements Closeable {

	private final InputStream input;
	private final Charset charset;

	private final CharsetDecoder decoder;
	private final int bufferSize;
	private final byte[] inBufferArray;
	private final ByteBuffer inBuffer;
	private final CharBuffer outBuffer;

	public CharInputStream(InputStream input) {
		this(input, StandardCharsets.UTF_8, 64);
	}

	public CharInputStream(InputStream input, Charset charset, int bufferSize) {
		this.input = input;
		this.charset = charset;
		this.bufferSize = bufferSize;
		this.inBufferArray = new byte[bufferSize];
		this.inBuffer = ByteBuffer.wrap(inBufferArray);
		this.outBuffer = CharBuffer.allocate(bufferSize);
		decoder = charset.newDecoder();
	}

	public char readChar() throws IOException {
		if (outBuffer.hasRemaining()) {
			return outBuffer.get();
		}
		outBuffer.clear();
		int r = input.readNBytes(inBufferArray, 0, bufferSize);
		CoderResult result = decoder.decode(inBuffer, outBuffer, r < bufferSize);


		return outBuffer.get();
	}

	private void rewind() {
		int pos = inBuffer.position();
		int lim = inBuffer.limit();
		if (pos < lim) {
			if (pos == 0) {
				//inBuffer.po
			}
		}
	}

	@Override
	public void close() throws IOException {
		input.close();
	}
}
