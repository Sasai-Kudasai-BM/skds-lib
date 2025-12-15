package net.skds.lib2.io.codec;


import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.sosison.SosisonEntryType;
import net.skds.lib2.utils.ArrayUtils;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

public interface UniversalReader {

	SosisonEntryType nextEntryType() throws IOException;

	default SosisonEntryType getArrayTypeIfSupported() throws IOException {
		return null;
	}

	String readName() throws IOException;

	void beginObject() throws IOException;

	void endObject() throws IOException;

	void beginList() throws IOException;

	void endList() throws IOException;

	boolean readBoolean() throws IOException;

	void skipNull() throws IOException;

	void skipValue() throws IOException;

	Number readNumber() throws IOException;

	default byte readByte() throws IOException {
		return readNumber().byteValue();
	}

	default short readShort() throws IOException {
		return readNumber().shortValue();
	}

	default int readInt() throws IOException {
		return readNumber().intValue();
	}

	default long readLong() throws IOException {
		return readNumber().longValue();
	}

	default float readFloat() throws IOException {
		return readNumber().floatValue();
	}

	default double readDouble() throws IOException {
		return readNumber().doubleValue();
	}

	default long readTime() throws IOException {
		return readNumber().longValue();
	}

	String readString() throws IOException;

	default byte[] readByteArray() throws IOException {
		switch (nextEntryType()) {
			case STRING -> {
				return Base64.getDecoder().decode(readString());
			}
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.ByteGrowingArray array = new ArrayUtils.ByteGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					if (!et.isNumber()) {
						throw new ParseException("Non-number member in primitive array");
					}
					array.add(readNumber().byteValue());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected STRING, LIST or NULL but next entry is " + nextEntryType());
		}
	}

	default short[] readShortArray() throws IOException {
		switch (nextEntryType()) {
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.ShortGrowingArray array = new ArrayUtils.ShortGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					if (!et.isNumber()) {
						throw new ParseException("Non-number member in primitive array");
					}
					array.add(readNumber().shortValue());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected LIST or NULL but next entry is " + nextEntryType());
		}
	}

	default char[] readCharArray() throws IOException {
		switch (nextEntryType()) {
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.CharGrowingArray array = new ArrayUtils.CharGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					switch (et) {
						case NULL -> {
							skipNull();
							array.add((char) 0);
						}
						case STRING -> {
							String cs = readString();
							if (cs.length() == 1) {
								array.add(cs.charAt(0));
							} else {
								throw new ParseException("Unexpected char " + cs);
							}
						}
						default -> {
							if (et.isNumber()) {
								array.add((char) readInt());
							} else throw new ParseException("Unexpected token " + et);
						}
					}
					array.add((char) readInt());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected STRING, LIST or NULL but next entry is " + nextEntryType());
		}
	}

	default int[] readIntArray() throws IOException {
		switch (nextEntryType()) {
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.IntGrowingArray array = new ArrayUtils.IntGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					if (!(et.isNumber() || et == SosisonEntryType.STRING)) {
						throw new ParseException("Non-number member in primitive array");
					}
					array.add(readInt());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected STRING, LIST or NULL but next entry is " + nextEntryType());
		}
	}

	default long[] readLongArray() throws IOException {
		switch (nextEntryType()) {
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.LongGrowingArray array = new ArrayUtils.LongGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					if (!et.isNumber()) {
						throw new ParseException("Non-number member in primitive array");
					}
					array.add(readLong());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected STRING, LIST or NULL but next entry is " + nextEntryType());
		}
	}

	default float[] readFloatArray() throws IOException {
		switch (nextEntryType()) {
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.FloatGrowingArray array = new ArrayUtils.FloatGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					if (!et.isNumber()) {
						throw new ParseException("Non-number member in primitive array");
					}
					array.add(readFloat());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected STRING, LIST or NULL but next entry is " + nextEntryType());
		}
	}

	default double[] readDoubleArray() throws IOException {
		switch (nextEntryType()) {
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.DoubleGrowingArray array = new ArrayUtils.DoubleGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					if (!et.isNumber()) {
						throw new ParseException("Non-number member in primitive array");
					}
					array.add(readDouble());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected STRING, LIST or NULL but next entry is " + nextEntryType());
		}
	}

	UUID readUUID() throws IOException;
}
