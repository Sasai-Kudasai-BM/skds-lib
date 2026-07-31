package net.skds.lib2.utils.tlsf;

public final class TLSFPage {

	private final int slSize;
	private final int slLog2;

	private int flBits;
	private final long[] slBits;

	private final TLSFBlock[] blocks;

	public TLSFPage(long size, int l2Size) {
		if (Long.bitCount(size) != 1)
			throw new IllegalArgumentException("TLSF page size must be power of 2, got " + size);
		if (Long.bitCount(l2Size) != 1)
			throw new IllegalArgumentException("TLSF level 2 size must be power of 2, got " + l2Size);
		this.slSize = l2Size;
		this.slLog2 = Long.numberOfTrailingZeros(l2Size);
		int flSize = Long.numberOfTrailingZeros(size) + 1;
		this.blocks = new TLSFBlock[flSize * l2Size];
		this.slBits = new long[flSize];

		int fl = flPow2(size);
		int sl = sl(size, fl);
		this.flBits = 1 << fl;
		this.slBits[fl] = 1L << sl;

		TLSFBlock initial = new TLSFBlock(size);
		this.blocks[blocksIndex(fl, sl)] = initial;
	}

	public TLSFBlock allocate(long allocSize) {
		int fl = fl(allocSize);
		int sl = sl(allocSize, fl);
		TLSFBlock foundBlock = findSuitableBlock(fl, sl);
		if (foundBlock == null) {
			return null;
		}
		removeBlock(foundBlock);
		long remainder = foundBlock.size - allocSize;
		if (remainder > 0) {
			TLSFBlock remainderBlock = new TLSFBlock(remainder);
			remainderBlock.offset = foundBlock.offset + allocSize;
			remainderBlock.prev = foundBlock;
			remainderBlock.next = foundBlock.next;
			if (foundBlock.next != null) {
				foundBlock.next.prev = remainderBlock;
			}
			foundBlock.next = remainderBlock;
			foundBlock.size = allocSize;
			insert(remainderBlock);
		}
		foundBlock.isFree = false;
		if (foundBlock.next != null) {
			foundBlock.next.isPrevFree = false;
		}
		return foundBlock;
	}

	public void free(TLSFBlock block) {
		if (block == null || block.isFree) {
			return;
		}
		TLSFBlock next = block.next;
		if (next != null && next.isFree) {
			removeBlock(next);
			block.size += next.size;
			block.next = next.next;
			if (next.next != null) {
				next.next.prev = block;
			}
		}
		if (block.isPrevFree) {
			TLSFBlock prev = block.prev;
			if (prev != null) {
				removeBlock(prev);
				prev.size += block.size;
				prev.next = block.next;
				if (block.next != null) {
					block.next.prev = prev;
				}
				block = prev;
			}
		}
		if (block.next != null) {
			block.next.isPrevFree = true;
			block.next.prev = block;
		}
		insert(block);
	}

	private void removeBlock(TLSFBlock block) {
		int fl = fl(block.size);
		int sl = sl(block.size, fl);
		TLSFBlock prev = block.prevFree;
		TLSFBlock next = block.nextFree;
		if (prev != null) {
			prev.nextFree = next;
		}
		if (next != null) {
			next.prevFree = prev;
		}
		int blockIndex = blocksIndex(fl, sl);
		if (blocks[blockIndex] == block) {
			blocks[blockIndex] = next;
			if (next == null) {
				slBits[fl] &= ~(1L << sl);
				if (slBits[fl] == 0) {
					flBits &= ~(1 << fl);
				}
			}
		}
		block.nextFree = null;
		block.prevFree = null;
		block.isFree = false;
		if (block.next != null) {
			block.next.isPrevFree = false;
		}
	}

	private TLSFBlock findSuitableBlock(int fl, int sl) {
		long slMap = slBits[fl] & (~0L << sl);
		if (slMap == 0) {
			int flMap = flBits & (~0 << (fl + 1));
			if (flMap == 0) {
				return null;
			}
			fl = Integer.numberOfTrailingZeros(flMap);
			slMap = slBits[fl];
		}
		sl = Long.numberOfTrailingZeros(slMap);
		return blocks[blocksIndex(fl, sl)];
	}

	private void insert(TLSFBlock block) {
		long blockSize = block.size;
		int fl = fl(blockSize);
		int sl = sl(blockSize, fl);
		TLSFBlock currentHead = blocks[blocksIndex(fl, sl)];
		block.nextFree = currentHead;
		block.prevFree = null;
		block.isFree = true;
		if (currentHead != null) {
			currentHead.prevFree = block;
		}
		this.blocks[blocksIndex(fl, sl)] = block;
		flBits |= (1 << fl);
		slBits[fl] |= (1L << sl);
		TLSFBlock next = block.next;
		if (next != null) {
			next.isPrevFree = true;
			next.prev = block;
		}
	}

	private int flPow2(long size) {
		return Long.numberOfTrailingZeros(size);
	}

	private int fl(long size) {
		return 63 - Long.numberOfLeadingZeros(size);
	}

	private int sl(long size, int fl) {
		return (int) ((size >>> (fl - this.slLog2)) ^ this.slSize);
	}

	private int blocksIndex(int fl, int sl) {
		return fl * this.slSize + sl;
	}
}
