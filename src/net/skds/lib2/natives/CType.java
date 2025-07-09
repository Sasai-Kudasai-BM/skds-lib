package net.skds.lib2.natives;

public enum CType {
	INT_8(1, true),
	UINT_8(1, false),
	INT_16(2, true),
	UINT_16(2, false),
	INT_32(4, true),
	UINT_32(4, false),
	INT_64(8, true),
	UINT_64(8, false),

	POINTER(8, false),

	STRUCT(-1, false),
	;

	public final int byteSize;
	public final boolean signed;

	CType(int byteSize, boolean signed) {
		this.byteSize = byteSize;
		this.signed = signed;
	}
}
