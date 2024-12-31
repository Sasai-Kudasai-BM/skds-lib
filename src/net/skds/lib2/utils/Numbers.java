package net.skds.lib2.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Numbers {

	public static final Number ZERO = 0;

	public static Number parseNumber(String stringValue) {
		if (stringValue.length() > 2) {
			char c0 = stringValue.charAt(0);
			char c1 = stringValue.charAt(1);
			if (c0 == '0' && (c1 == 'x' || c1 == 'X')) {
				String stringSubValue = stringValue.substring(2);
				return Long.parseLong(stringSubValue, 16);
			}
		}
		if (stringValue.indexOf('.') != -1) {
			return Double.parseDouble(stringValue);
		}
		return Long.parseLong(stringValue);
	}

	public int leftBytes(long l) {
		return (int) (l >> 32);
	}

	public int rightBytes(long l) {
		return (int) (l & 0xFFFFFFFFL);
	}

	public int leftBytes(double d) {
		return (int) (Double.doubleToLongBits(d) >> 32);
	}

	public int rightBytes(double d) {
		return (int) (Double.doubleToLongBits(d) & 0xFFFFFFFFL);
	}
}
