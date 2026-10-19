package net.skds.lib2.utils.tlsf;

public final class TLSFBlock {
	long size;
	long offset;
	TLSFBlock prev;
	TLSFBlock next;
	TLSFBlock prevFree;
	TLSFBlock nextFree;
	boolean isFree;
	boolean isPrevFree;

	TLSFBlock(long size) {
		this.size = size;
	}

	public long getSize() {
		return this.size;
	}

	public long getOffset() {
		return this.offset;
	}
}
