/*
 * $ProjectName$
 * $ProjectRevision$
 * -----------------------------------------------------------
 * $Id: ByteArrayBitInputStream.java,v 1.3 2003/04/10 19:48:31 jarnbjo Exp $
 * -----------------------------------------------------------
 *
 * $Author: jarnbjo $
 *
 * Description:
 *
 * Copyright 2002-2003 Tor-Einar Jarnbjo
 * -----------------------------------------------------------
 *
 * Change History
 * -----------------------------------------------------------
 * $Log: ByteArrayBitInputStream.java,v $
 * Revision 1.3  2003/04/10 19:48:31  jarnbjo
 * no message
 *
 * Revision 1.2  2003/03/16 01:11:39  jarnbjo
 * no message
 *
 * Revision 1.1  2003/03/03 21:02:20  jarnbjo
 * no message
 */
package net.skds.lib2.misc.sound.formats.vorbis;

public class ByteArrayBitInputStream {

	private final byte[] source;

	private int byteIndex = 0;
	private int bitIndex = 0;

	public ByteArrayBitInputStream(byte[] source) {
		this.source = source;
	}

	public boolean readBit() {
		boolean ret = (source[byteIndex] & (1 << bitIndex++)) != 0;
		if (bitIndex > 7) {
			bitIndex = 0;
			byteIndex++;
		}
		return ret;
	}

	public int availableBytes() {
		return source.length - byteIndex;
	}

	public int readInt(int bits) {
		int offset = 0;
		int res = 0;
		while (bits > 0) {
			int readBits = Math.min(8 - bitIndex, bits);

			res |= ((source[byteIndex] >>> bitIndex) & ~(-1 << readBits)) << offset;

			offset += readBits;
			bits -= readBits;
			bitIndex += readBits;
			if (bitIndex > 7) {
				bitIndex = 0;
				byteIndex++;
			}
		}
		return res;
	}

	public int readAlignedInt(int bytes) {
		int res = 0;
		if (bitIndex > 0) byteIndex++;
		for (int i = 0; i < bytes; i++) {
			res |= (source[byteIndex++] & 0xFF) << (i * 8);
		}
		bitIndex = 0;
		return res;
	}


	public byte[] readAlignedBytes(int count) {
		if (bitIndex > 0) byteIndex++;
		byte[] bytes = new byte[count];
		System.arraycopy(source, byteIndex, bytes, 0, count);
		byteIndex += count;
		bitIndex = 0;
		return bytes;
	}
}
