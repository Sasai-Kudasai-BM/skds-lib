package net.skds.lib2.security;

import net.skds.lib2.utils.ArrayUtils;
import net.skds.lib2.utils.SKDSUtils;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CipherInputStream extends FilterInputStream {

	private final Cipher cipher;
	private final byte[] miniBuffer = new byte[1];
	private final byte[] inputBuffer = new byte[SKDSUtils.DEFAULT_BUFFER_SIZE];
	private final byte[] outputBuffer = new byte[SKDSUtils.DEFAULT_BUFFER_SIZE];
	private int pos = 0;
	private int limit = 0;


	public CipherInputStream(InputStream in, Cipher cipher) {
		super(in);
		this.cipher = cipher;
	}

	private int bufferAvailable(int ensure) throws IOException {
		int available = limit - pos;
		if (available < 0) throw new IllegalStateException("pos < limit");
		ensure -= available;
		if (ensure > 0) {
			if (pos > 0) {
				if (available > 0) {
					if (pos > outputBuffer.length / 2) {
						ArrayUtils.rewind(outputBuffer, pos, available);
						pos = 0;
						limit = available;
					}
				} else {
					pos = 0;
					limit = 0;
				}
			}
			int rd = 0;
			int toRead = Math.min(Math.max(in.available(), ensure), inputBuffer.length);
			while (toRead > 0) {
				int r = in.read(inputBuffer, 0, toRead);
				if (r == -1) break;
				try {
					for (int upd = 0; upd < r; ) {
						upd += cipher.update(inputBuffer, 0, r, outputBuffer, pos + rd + upd);
					}
				} catch (ShortBufferException e) {
					throw new IOException(e);
				}
				toRead -= r;
				limit += r;
				rd += r;
			}
			return available();
		}
		return available;
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
		int rd = 0;
		while (rd < len) {
			int available = bufferAvailable(len);
			if (available == 0) break;
			int toCopy = Math.min(available, len);
			rd += toCopy;
			System.arraycopy(this.outputBuffer, this.pos, b, off, toCopy);
			this.pos += toCopy;
		}
		return rd;
	}

	@Override
	public long skip(long n) throws IOException {
		int rd = 0;
		while (rd < n) {
			int available = bufferAvailable((int) n);
			if (available == 0) break;
			int toCopy = Math.min(available, (int) n);
			rd += toCopy;
			this.pos += toCopy;
		}
		return rd;
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
