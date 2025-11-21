package net.skds.lib2.io.sosison;

import lombok.Getter;


public enum SosisonEntryType {
	NULL(0, 0, false, false, false),
	//BINARY(1, false, false), // use byte array
	// reserved
	BYTE(2, 1, true, true, false),
	SHORT(3, 2, true, true, false),
	INT(4, 4, true, true, false),
	LONG(5, 8, true, true, false),
	FLOAT(6, 4, true, true, false),
	DOUBLE(7, 8, true, true, false),
	BOOLEAN(8, 0, false, false, false),
	STRING(10, -1, false, false, false),
	UUID(11, 16, false, false, false),
	//TIME(12, 8, true, false, false),
	// reserved
	BEGIN_OBJECT(16, 0, false, false, false),
	END_OBJECT(17, 0, false, false, true),
	BEGIN_LIST(18, 0, false, false, false),
	END_LIST(19, 0, false, false, true),
	// reserved
	//COMMENT(32, false, false),
	//NUMBER(-1, false),
	;

	public static final int FLAG_BITS = 2;
	public static final int ID_MASK = 0xff >> FLAG_BITS;
	public static final int FLAG_MASK = 0xff & ~ID_MASK;
	public static final int FLAG_1_MASK = FLAG_MASK << 1 & FLAG_MASK;
	public static final int FLAG_2_MASK = FLAG_MASK >> 1 & FLAG_MASK;

	public static final int BOOLEAN_FLAG = FLAG_1_MASK;
	public static final int ARRAY_FLAG = FLAG_1_MASK;
	public static final int BYTE_SIZE_FLAG = FLAG_2_MASK;

	private static final byte[] NULL_DATA = {0, NULL.getId()}; // zero name, null
	private static final SosisonEntryType[] lookup = new SosisonEntryType[ID_MASK + 1];

	@Getter
	private final byte id;
	@Getter
	private final boolean isNumber;
	@Getter
	private final boolean isTerminal;
	private final boolean supportArrays;
	@Getter
	private final int size;

	SosisonEntryType(int id, int size, boolean isNumber, boolean supportArrays, boolean isTerminal) {
		this.id = (byte) id;
		this.isTerminal = isTerminal;
		this.isNumber = isNumber;
		this.size = size;
		this.supportArrays = supportArrays;
	}

	public boolean supportArrays() {
		return supportArrays;
	}

	public static byte[] nullData() {
		return NULL_DATA.clone();
	}

	public int packFlags(int flags) {
		return (flags & FLAG_MASK) | id;
	}

	public static SosisonEntryType number(Number n) {
		return switch (n) {
			case Integer ignored -> INT;
			case Float ignored -> FLOAT;
			case Long ignored -> LONG;
			case Double ignored -> DOUBLE;
			case Short ignored -> SHORT;
			case Byte ignored -> BYTE;
			default -> throw new IllegalArgumentException("Unsupported number type " + n.getClass().getSimpleName());
		};
	}

	public static SosisonEntryType byID(int id) {
		id = id & ID_MASK;
		SosisonEntryType value = lookup[id];
		if (value == null) {
			throw new IllegalArgumentException("Sosison entry with id " + id + " is not supported");
		}
		return value;
	}

	static {
		for (SosisonEntryType e : values()) {
			lookup[e.id & ID_MASK] = e;
		}
	}
}
