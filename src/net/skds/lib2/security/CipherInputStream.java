package net.skds.lib2.security;

import net.skds.lib2.utils.SKDSUtils;

import javax.crypto.Cipher;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CipherInputStream extends FilterInputStream {

	private final Cipher cipher;
	private final byte[] miniBuffer = new byte[1];
	private final byte[] inputBuffer = new byte[SKDSUtils.DEFAULT_BUFFER_SIZE];
	private byte[] outputBuffer;
	private int pos = 0;
	private int limit = 0;


	protected CipherInputStream(InputStream in, Cipher cipher) {
		super(in);
		this.cipher = cipher;
	}

	private int bufferAvailable(int ensure) throws IOException {
		if (pos < limit) throw new IllegalStateException("pos < limit");
		int toRead = Math.min(Math.max(in.available(), ensure), inputBuffer.length);
		if (toRead == 0) {
			return 0;
		}
		int r = in.read(inputBuffer, 0, toRead);
		outputBuffer = cipher.update(inputBuffer, 0, r);
		pos = 0;
		limit = r;
		return r;
	}


	private int readAvailable(byte[] b, int off, int len) throws IOException {
		return 0;
	}

	@Override
	public int read() throws IOException {
		int r = read(miniBuffer, 0, 1);
		return r == -1 ? -1 : miniBuffer[0] & 0xff;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return this.read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return 0;
	}

	@Override
	public long skip(long n) throws IOException {
		return 0;
	}
	

	@Override
	public int available() throws IOException {
		return limit - pos;
	}

	@Override
	public boolean markSupported() {
		return false;
	}
}
