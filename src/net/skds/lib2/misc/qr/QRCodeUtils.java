package net.skds.lib2.misc.qr;

import lombok.experimental.UtilityClass;

@UtilityClass
public class QRCodeUtils {

	public static int getVersion(int dots) {
		dots -= 17;
		if (dots % 4 != 0) {
			throw new QRCodeException("Invalid dot count " + dots);
		}
		return dots / 4;
	}

	public static int getDots(int version) {
		if (version < 1 || version > 40) {
			throw new QRCodeException("Invalid dot version " + version);
		}
		return version * 4 + 17;
	}
}
