package net.skds.lib2.natives.struct;

import net.skds.lib2.natives.MemoryAccess;

import java.lang.foreign.MemorySegment;

public abstract class CStruct {

	protected MemorySegment segment;
	protected long offset;

	public CStruct() {
	}

	public CStruct(MemorySegment segment, long offset) {
		this.segment = segment;
		this.offset = offset;
	}

	public CStruct(MemorySegment segment) {
		this.segment = segment;
		this.offset = 0;
	}

	public CStruct(long address) {
		this.segment = MemoryAccess.ALL_MEMORY;
		this.offset = address;
	}

	public abstract int getSize();

	public abstract int getAlignment();

	public final MemorySegment getSegment() {
		return segment;
	}

	public final long getOffset() {
		return offset;
	}

	public final long getAddress() {
		return segment.address() + offset;
	}
}
