package net.skds.lib2.misc.ogg;

import net.skds.lib2.io.exception.EndOfInputException;
import net.skds.lib2.utils.StringUtils;

import java.io.IOException;
import java.util.Arrays;

public enum OggMediaType {
	DIRAC("BBCD\0", "dirac"),
	FLAC("\177FLAC", "flac"),
	THEORA("\u0080theora", "theora"),
	VORBIS("\u0001vorbis", "vorbis"),
	CELT("CELT    ", "celt"),
	OPUS("OpusHead", "opus"),
	CMML("CMML\0\0\0\0", "cmml"),
	JNG("\213JNG\r\n\032\n", "jng"),
	KATE("\u0080kate\0\0\0", "kate"),
	MIDI("OggMIDI\0", "midi"),
	MNG("\212MNG\r\n\032\n", "mng"),
	PCM("PCM     ", "pcm"),
	PNG("\211PNG\r\n\032\n", "png"),
	SPEEX("Speex   ", "speex"),
	YUV4MPEG("YUV4MPEG", "yuv4mpeg");

	public final byte[] id;
	public final String mediaName;

	public static final OggMediaType[] VALUES = values();

	OggMediaType(String id, String mediaName) {
		byte[] arr = new byte[8];
		int l = id.length();
		for (int i = 0; i < l; i++) {
			arr[i] = (byte) id.charAt(i);
		}
		this.id = arr;
		this.mediaName = mediaName;
	}

	public static OggMediaType readFromBody(byte[] body) throws IOException {
		if (body.length < 5) throw new EndOfInputException();

		int l = switch (body[0]) {
			case 'B', '\177' -> 5;
			case (byte) 0x80, 0x01 -> 7;
			default -> 8;
		};
		byte[] b = new byte[8];
		System.arraycopy(body, 0, b, 0, l);

		for (int i = 0; i < VALUES.length; i++) {
			if (Arrays.equals(VALUES[i].id, b)) {
				return VALUES[i];
			}
		}
		throw new IOException("Unknown media type " + StringUtils.HEX_FORMAT_UC.formatHex(b));
	}
}
