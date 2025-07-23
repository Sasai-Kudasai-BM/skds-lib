package net.skds.lib2.misc.ogg;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OggCRC32 {

	private static final int CRC_POLYNOMIAL = 0x04C11DB7;
	private static final int[] CRC_TABLE = new int[256];

	static {
		int crc;
		for (int i = 0; i < 256; i++) {
			crc = i << 24;
			for (int j = 0; j < 8; j++) {
				if (crc < 0) {
					crc = ((crc << 1) ^ CRC_POLYNOMIAL);
				} else {
					crc <<= 1;
				}
			}
			CRC_TABLE[i] = crc;
		}
	}

	//public int getCRC(byte[] arr) {
	//	return getCRC(arr, 0);
	//}

	public int getCRC(byte[] arr, int previous) {
		int crc = previous;
		int a, b;

		for (int i = 0; i < arr.length; i++) {
			a = crc << 8;
			b = CRC_TABLE[(crc >>> 24) ^ (arr[i] & 0xff)];
			crc = a ^ b;
		}
		return crc;
	}

	public int getMagicCRC(int magic) {
		magic = Integer.reverseBytes(magic);
		int crc = 0;
		int a, b;

		for (int i = 0; i < 4; i++) {
			a = crc << 8;
			b = CRC_TABLE[(crc >>> 24) ^ (magic & 0xff)];
			magic >>>= 8;
			crc = a ^ b;
		}
		return crc;
	}

}

