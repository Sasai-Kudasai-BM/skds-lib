package net.skds.lib2.security;

import net.skds.lib2.utils.SKDSUtils;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CipherOutputStream extends FilterOutputStream {

	private final Cipher cipher;
	private final byte[] miniBuffer = new byte[1];
	private final byte[] outputBuffer = new byte[SKDSUtils.DEFAULT_BUFFER_SIZE];


	public CipherOutputStream(OutputStream out, Cipher cipher) {
		super(out);
		this.cipher = cipher;
	}

	@Override
	public void write(int b) throws IOException {
		miniBuffer[0] = (byte) b;
		write(miniBuffer, 0, 1);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		int wr = 0;
		while (wr < len) {
			int toWrite = Math.min(len, outputBuffer.length - wr);
			try {
				toWrite = cipher.update(b, off, toWrite, outputBuffer, wr);
			} catch (ShortBufferException e) {
				throw new IOException(e);
			}
			out.write(outputBuffer, 0, toWrite);
			wr += toWrite;
		}
	}

	@Override
	public void flush() {
	}
}
