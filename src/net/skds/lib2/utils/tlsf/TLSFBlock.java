package net.skds.lib2.utils.tlsf;

import lombok.Getter;


public final class TLSFBlock {
	@Getter
	long size;
	@Getter
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
}
