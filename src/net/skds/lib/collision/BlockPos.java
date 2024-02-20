package net.skds.lib.collision;

import net.skds.lib.mat.FastMath;
import net.skds.lib.mat.Vec3;

public final class BlockPos extends Vec3I implements Comparable<BlockPos> {

	public static final BlockPos ZERO = new BlockPos(0, 0, 0);
	private static final int SIZE_BITS_X;
	private static final int SIZE_BITS_Z;
	public static final int SIZE_BITS_Y;
	private static final long BITS_X;
	private static final long BITS_Y;
	private static final long BITS_Z;
	private static final int BIT_SHIFT_Z;
	private static final int BIT_SHIFT_X;

	public BlockPos(int x, int y, int z) {
		super(x, y, z);
	}

	public BlockPos(double x, double y, double z) {
		super(FastMath.floor(x), FastMath.floor(y), FastMath.floor(z));
	}

	public BlockPos(Vec3 pos) {
		super(FastMath.floor(pos.x), FastMath.floor(pos.y), FastMath.floor(pos.z));
	}

	public static long offset(long value, Direction direction) {
		return BlockPos.add(value, direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
	}

	public static long add(long value, int x, int y, int z) {
		return BlockPos.asLong(BlockPos.unpackLongX(value) + x, BlockPos.unpackLongY(value) + y,
				BlockPos.unpackLongZ(value) + z);
	}

	public static int unpackLongX(long packedPos) {
		return (int) (packedPos << 64 - BIT_SHIFT_X - SIZE_BITS_X >> 64 - SIZE_BITS_X);
	}

	public static int unpackLongY(long packedPos) {
		return (int) (packedPos << 64 - SIZE_BITS_Y >> 64 - SIZE_BITS_Y);
	}

	public static int unpackLongZ(long packedPos) {
		return (int) (packedPos << 64 - BIT_SHIFT_Z - SIZE_BITS_Z >> 64 - SIZE_BITS_Z);
	}

	public static BlockPos fromLong(long packedPos) {
		return new BlockPos(BlockPos.unpackLongX(packedPos), BlockPos.unpackLongY(packedPos),
				BlockPos.unpackLongZ(packedPos));
	}

	public long asLong() {
		return BlockPos.asLong(this.x, this.y, this.z);
	}

	public static long asLong(int x, int y, int z) {
		long l = 0L;
		l |= ((long) x & BITS_X) << BIT_SHIFT_X;
		l |= ((long) y & BITS_Y);
		return l |= ((long) z & BITS_Z) << BIT_SHIFT_Z;
	}

	public static long removeChunkSectionLocalY(long y) {
		return y & 0xFFFFFFFFFFFFFFF0L;
	}

	public Vec3 blockCenter() {
		return new Vec3(x + 0.5, y + 0.5, z + 0.5);
	}

	public Vec3 blockZero() {
		return new Vec3(x, y, z);
	}


	public BlockPos up() {
		return this.offset(Direction.UP);
	}

	public BlockPos up(int distance) {
		return this.offset(Direction.UP, distance);
	}

	public BlockPos down() {
		return this.offset(Direction.DOWN);
	}

	public BlockPos down(int i) {
		return this.offset(Direction.DOWN, i);
	}

	public BlockPos north() {
		return this.offset(Direction.NORTH);
	}

	public BlockPos north(int distance) {
		return this.offset(Direction.NORTH, distance);
	}

	public BlockPos south() {
		return this.offset(Direction.SOUTH);
	}

	public BlockPos south(int distance) {
		return this.offset(Direction.SOUTH, distance);
	}

	public BlockPos west() {
		return this.offset(Direction.WEST);
	}

	public BlockPos west(int distance) {
		return this.offset(Direction.WEST, distance);
	}

	public BlockPos east() {
		return this.offset(Direction.EAST);
	}

	public BlockPos east(int distance) {
		return this.offset(Direction.EAST, distance);
	}

	public BlockPos offset(Direction direction) {
		return new BlockPos(this.x + direction.getOffsetX(), this.y + direction.getOffsetY(),
				this.z + direction.getOffsetZ());
	}

	public BlockPos offset(Direction direction, int i) {
		if (i == 0) {
			return this;
		}
		return new BlockPos(this.x + direction.getOffsetX() * i, this.y + direction.getOffsetY() * i,
				this.z + direction.getOffsetZ() * i);
	}

	public BlockPos offset(Direction.Axis axis, int i) {
		if (i == 0) {
			return this;
		}
		int j = axis == Direction.Axis.X ? i : 0;
		int k = axis == Direction.Axis.Y ? i : 0;
		int l = axis == Direction.Axis.Z ? i : 0;
		return new BlockPos(this.x + j, this.y + k, this.z + l);
	}

	//public BlockPos rotate(BlockRotation rotation) {
	//	switch (rotation) {
	//		default: {
	//			return this;
	//		}
	//		case CLOCKWISE_90: {
	//			return new BlockPos(-this.z, this.y, this.x);
	//		}
	//		case CLOCKWISE_180: {
	//			return new BlockPos(-this.x, this.y, -this.z);
	//		}
	//		case COUNTERCLOCKWISE_90:
	//	}
	//	return new BlockPos(this.z, this.y, -this.x);
	//}

	//public static Optional<BlockPos> findClosest(BlockPos pos, int horizontalRange, int verticalRange,
	//		Predicate<BlockPos> condition) {
	//	for (BlockPos blockPos : BlockPos.iterateOutwards(pos, horizontalRange, verticalRange, horizontalRange)) {
	//		if (!condition.test(blockPos))
	//			continue;
	//		return Optional.of(blockPos);
	//	}
	//	return Optional.empty();
	//}

	//public static Stream<BlockPos> streamOutwards(BlockPos center, int maxX, int maxY, int maxZ) {
	//	return StreamSupport.stream(BlockPos.iterateOutwards(center, maxX, maxY, maxZ).spliterator(), false);
	//}

	//public static Iterable<BlockPos> iterate(BlockPos start, BlockPos end) {
	//	return BlockPos.iterate(Math.min(start.x, end.x), Math.min(start.y, end.y),
	//			Math.min(start.z, end.z), Math.max(start.x, end.x),
	//			Math.max(start.y, end.y), Math.max(start.z, end.z));
	//}

	//public static Stream<BlockPos> stream(BlockPos start, BlockPos end) {
	//	return StreamSupport.stream(BlockPos.iterate(start, end).spliterator(), false);
	//}

	//public static Stream<BlockPos> stream(BlockBox box) {
	//	return BlockPos.stream(Math.min(box.getMinX(), box.getMaxX()), Math.min(box.getMinY(), box.getMaxY()),
	//			Math.min(box.getMinZ(), box.getMaxZ()), Math.max(box.getMinX(), box.getMaxX()),
	//			Math.max(box.getMinY(), box.getMaxY()), Math.max(box.getMinZ(), box.getMaxZ()));
	//}

	//public static Stream<BlockPos> stream(Box box) {
	//	return BlockPos.stream(FastMath.floor(box.minX), FastMath.floor(box.minY), FastMath.floor(box.minZ),
	//			FastMath.floor(box.maxX), FastMath.floor(box.maxY), FastMath.floor(box.maxZ));
	//}

	//public static Stream<BlockPos> stream(int startX, int startY, int startZ, int endX, int endY, int endZ) {
	//	return StreamSupport.stream(BlockPos.iterate(startX, startY, startZ, endX, endY, endZ).spliterator(), false);
	//}

	//public static Iterable<BlockPos> iterate(final int startX, final int startY, final int startZ, int endX, int endY,
	//		int endZ) {
	//	final int i = endX - startX + 1;
	//	final int j = endY - startY + 1;
	//	int k = endZ - startZ + 1;
	//	final int l = i * j * k;
	//	return () -> new AbstractIterator<BlockPos>() {
	//		private final Mutable pos = new Mutable();
	//		private int index;

	//		@Override
	//		protected BlockPos computeNext() {
	//			if (this.index == l) {
	//				return (BlockPos) this.endOfData();
	//			}
	//			int i2 = this.index % i;
	//			int j2 = this.index / i;
	//			int k = j2 % j;
	//			int l2 = j2 / j;
	//			++this.index;
	//			return this.pos.set(startX + i2, startY + k, startZ + l2);
	//		}

	//		@Override
	//		protected /* synthetic */ Object computeNext() {
	//			return this.computeNext();
	//		}
	//	};
	//}

	static {
		SIZE_BITS_Z = SIZE_BITS_X = 33 - Integer.numberOfLeadingZeros(30000000);
		SIZE_BITS_Y = 64 - SIZE_BITS_X - SIZE_BITS_Z;
		BITS_X = (1L << SIZE_BITS_X) - 1L;
		BITS_Y = (1L << SIZE_BITS_Y) - 1L;
		BITS_Z = (1L << SIZE_BITS_Z) - 1L;
		BIT_SHIFT_Z = SIZE_BITS_Y;
		BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_Z;
	}

	@Override
	public int compareTo(final BlockPos o) {

		if (x > o.x) {
			return 1;
		}
		if (x < o.x) {
			return -1;
		}
		if (y > o.y) {
			return 1;
		}
		if (y < o.y) {
			return -1;
		}
		return Integer.compare(z, o.z);

	}
}
