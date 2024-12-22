package net.skds.lib.collision;

import net.sdteam.libmerge.Lib2Merge;
import net.skds.lib.mat.FastMath;
import net.skds.lib.mat.IVec3;
import net.skds.lib.mat.Vec3;

import java.util.function.Predicate;

public enum Direction {
	DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new BlockPos(0, -1, 0)),
	UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new BlockPos(0, 1, 0)),
	NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new BlockPos(0, 0, -1)),
	SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new BlockPos(0, 0, 1)),
	WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new BlockPos(-1, 0, 0)),
	EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new BlockPos(1, 0, 0));

	public static final Direction[] VALUES = values();
	public static final Direction[] HORIZONTAL = {NORTH, SOUTH, WEST, EAST};

	private final int id;
	private final int idOpposite;
	private final int idHorizontal;
	private final String name;
	private final Axis axis;
	private final AxisDirection direction;
	private final BlockPos vector;

	public static final Direction LEFT = EAST;
	public static final Direction RIGHT = WEST;
	public static final Direction FORWARD = SOUTH;
	public static final Direction BACKWARD = NORTH;

	public static final Direction XP = EAST;
	public static final Direction XN = WEST;
	public static final Direction YP = UP;
	public static final Direction YN = DOWN;
	public static final Direction ZP = SOUTH;
	public static final Direction ZN = NORTH;

	private static final Direction[][] shuffleH = {
			{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH},
			{Direction.EAST, Direction.WEST, Direction.SOUTH, Direction.NORTH},
			{Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST},
			{Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH},
			{Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH},
			{Direction.EAST, Direction.SOUTH, Direction.NORTH, Direction.WEST},
			{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH},
			{Direction.WEST, Direction.EAST, Direction.SOUTH, Direction.NORTH},
			{Direction.WEST, Direction.NORTH, Direction.SOUTH, Direction.EAST},
			{Direction.WEST, Direction.NORTH, Direction.EAST, Direction.SOUTH},
			{Direction.WEST, Direction.SOUTH, Direction.EAST, Direction.NORTH},
			{Direction.WEST, Direction.SOUTH, Direction.NORTH, Direction.EAST},
			{Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH},
			{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST},
			{Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST},
			{Direction.NORTH, Direction.WEST, Direction.EAST, Direction.SOUTH},
			{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST},
			{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST},
			{Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.NORTH},
			{Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST},
			{Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST},
			{Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.NORTH},
			{Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST},
			{Direction.SOUTH, Direction.NORTH, Direction.WEST, Direction.EAST},
	};

	Direction(int id, int idOpposite, int idHorizontal, String name, AxisDirection direction, Axis axis,
			  BlockPos vector) {
		this.id = id;
		this.idHorizontal = idHorizontal;
		this.idOpposite = idOpposite;
		this.name = name;
		this.axis = axis;
		this.direction = direction;
		this.vector = vector;
	}

	public int getId() {
		return this.id;
	}

	public int getHorizontal() {
		return this.idHorizontal;
	}

	public AxisDirection getDirection() {
		return this.direction;
	}

	public Direction getOpposite() {
		return Direction.byId(this.idOpposite);
	}

	public Direction rotateClockwise(Axis axis) {
		return switch (axis) {
			case X -> {
				if (this == WEST || this == EAST) {
					yield this;
				}
				yield this.rotateXClockwise();
			}
			case Y -> {
				if (this == UP || this == DOWN) {
					yield this;
				}
				yield this.rotateYClockwise();
			}
			case Z -> this == NORTH || this == SOUTH ? this : this.rotateZClockwise();
		};
	}


	public static Direction[] randomHorizontal() {
		return shuffleH[FastMath.RANDOM.nextInt(shuffleH.length)];
	}

	public Direction rotateCounterclockwise(Axis axis) {
		return switch (axis) {
			case X -> {
				if (this == WEST || this == EAST) {
					yield this;
				}
				yield this.rotateXCounterclockwise();
			}
			case Y -> {
				if (this == UP || this == DOWN) {
					yield this;
				}
				yield this.rotateYCounterclockwise();
			}
			case Z -> this == NORTH || this == SOUTH ? this : this.rotateZCounterclockwise();
		};
	}

	public Direction rotateYClockwise() {
		return switch (this) {
			case NORTH -> EAST;
			case EAST -> SOUTH;
			case SOUTH -> WEST;
			case WEST -> NORTH;
			default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		};
	}

	private Direction rotateXClockwise() {
		return switch (this) {
			case UP -> NORTH;
			case NORTH -> DOWN;
			case DOWN -> SOUTH;
			case SOUTH -> UP;
			default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
		};
	}

	private Direction rotateXCounterclockwise() {
		return switch (this) {
			case UP -> SOUTH;
			case SOUTH -> DOWN;
			case DOWN -> NORTH;
			case NORTH -> UP;
			default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
		};
	}

	private Direction rotateZClockwise() {
		return switch (this) {
			case UP -> EAST;
			case EAST -> DOWN;
			case DOWN -> WEST;
			case WEST -> UP;
			default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
		};
	}

	private Direction rotateZCounterclockwise() {
		return switch (this) {
			case UP -> WEST;
			case WEST -> DOWN;
			case DOWN -> EAST;
			case EAST -> UP;
			default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
		};
	}

	public Direction rotateYCounterclockwise() {
		return switch (this) {
			case NORTH -> WEST;
			case EAST -> NORTH;
			case SOUTH -> EAST;
			case WEST -> SOUTH;
			default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
		};
	}

	public int getOffsetX() {
		return this.vector.x;
	}

	public int getOffsetY() {
		return this.vector.y;
	}

	public int getOffsetZ() {
		return this.vector.z;
	}

	public String getName() {
		return this.name;
	}

	public Axis getAxis() {
		return this.axis;
	}

	public static Direction byId(int id) {
		return VALUES[Math.abs(id % VALUES.length)];
	}

	public static Direction fromHorizontal(int value) {
		return HORIZONTAL[Math.abs(value % HORIZONTAL.length)];
	}

	public static Direction fromRotation(double rotation) {
		return Direction.fromHorizontal(FastMath.floor(rotation / 90.0 + 0.5) & 3);
	}

	public static Direction from(Axis axis, AxisDirection direction) {
		return switch (axis) {
			case X -> {
				if (direction == AxisDirection.POSITIVE) {
					yield EAST;
				}
				yield WEST;
			}
			case Y -> {
				if (direction == AxisDirection.POSITIVE) {
					yield UP;
				}
				yield DOWN;
			}
			case Z -> direction == AxisDirection.POSITIVE ? SOUTH : NORTH;
		};
	}

	public float asRotation() {
		return (this.idHorizontal & 3) * 90;
	}

	public static Direction getFacing(double x, double y, double z) {
		return Direction.getFacing((float) x, (float) y, (float) z);
	}

	public static Direction getFacing(IVec3 from, IVec3 to) {
		return getFacing((float) (to.x() - from.x()), (float) (to.y() - from.y()), (float) (to.z() - from.z()));
	}

	@Lib2Merge(complete = true)
	public static Direction getFacingFrom(IVec3 from, IVec3 to, Iterable<Direction> iterable) {
		return getFacingFrom((float) (to.x() - from.x()), (float) (to.y() - from.y()), (float) (to.z() - from.z()), iterable);
	}

	@Lib2Merge(complete = true)
	public static Direction getFacingFrom(float x, float y, float z, Iterable<Direction> iterable) {
		Direction direction = null;
		float f = Float.NEGATIVE_INFINITY;
		for (Direction direction2 : iterable) {
			float g = x * direction2.vector.x + y * direction2.vector.y + z * direction2.vector.z;
			if (!(g > f))
				continue;
			f = g;
			direction = direction2;
		}
		return direction;
	}

	@Lib2Merge(complete = true)
	public static Direction getFacing(float x, float y, float z) {
		Direction direction = NORTH;
		float f = Float.NEGATIVE_INFINITY;
		for (Direction direction2 : VALUES) {
			float g = x * direction2.vector.x + y * direction2.vector.y + z * direction2.vector.z;
			if (!(g > f))
				continue;
			f = g;
			direction = direction2;
		}
		return direction;
	}

	public static Direction getHorizontalFacing(IVec3 from, IVec3 to) {
		return getHorizontalFacing((float) (to.x() - from.x()), (float) (to.z() - from.z()));
	}

	public static Direction getHorizontalFacingFrom(IVec3 from, IVec3 to, Iterable<Direction> iterable) {
		return getHorizontalFacingFrom((float) (to.x() - from.x()), (float) (to.z() - from.z()), iterable);
	}

	public static Direction getHorizontalFacing(float x, float z) {
		Direction direction = NORTH;
		float f = Float.NEGATIVE_INFINITY;
		for (Direction direction2 : HORIZONTAL) {
			float g = x * direction2.vector.x + z * direction2.vector.z;
			if (!(g > f))
				continue;
			f = g;
			direction = direction2;
		}
		return direction;
	}

	public static Direction getHorizontalFacingFrom(float x, float z, Iterable<Direction> iterable) {
		Direction direction = null;
		float f = Float.NEGATIVE_INFINITY;
		for (Direction direction2 : iterable) {
			float g = x * direction2.vector.x + z * direction2.vector.z;
			if (!(g > f))
				continue;
			f = g;
			direction = direction2;
		}
		return direction;
	}

	public static Direction getFromOffset(int x, int y, int z) {

		if (x > 0) {
			return XP;
		}
		if (x < 0) {
			return XN;
		}
		if (y > 0) {
			return YP;
		}
		if (y < 0) {
			return YN;
		}
		if (z > 0) {
			return ZP;
		}
		if (z < 0) {
			return ZN;
		}
		return null;
	}

	public String toString() {
		return this.name;
	}

	public static Direction get(AxisDirection direction, Axis axis) {
		for (Direction direction2 : VALUES) {
			if (direction2.getDirection() != direction || direction2.getAxis() != axis)
				continue;
			return direction2;
		}
		throw new IllegalArgumentException("No such direction: " + direction + " " + axis);
	}

	public BlockPos getBlockOffset() {
		return this.vector;
	}

	public Vec3 createVector3D() {
		return new Vec3(this.vector);
	}


	@Deprecated // rewrite
	public boolean pointsTo(float yaw) {
		float g = -FastMath.sinDegr(yaw);
		float h = FastMath.cosDegr(yaw);
		return (float) this.vector.x * g + (float) this.vector.z * h > 0.0f;
	}

	public static enum Axis implements Predicate<Direction> {
		X("x") {
			@Override
			public int choose(int x, int y, int z) {
				return x;
			}

			@Override
			public double choose(double x, double y, double z) {
				return x;
			}

			@Override
			public Direction getDirection(double value) {
				return value < 0 ? WEST : EAST;
			}

			@Override
			public Vec3 getNormal(boolean negative) {
				return negative ? Vec3.XN : Vec3.XP;
			}
		},
		Y("y") {
			@Override
			public int choose(int x, int y, int z) {
				return y;
			}

			@Override
			public double choose(double x, double y, double z) {
				return y;
			}

			@Override
			public Direction getDirection(double value) {
				return value < 0 ? DOWN : UP;
			}

			@Override
			public Vec3 getNormal(boolean negative) {
				return negative ? Vec3.YN : Vec3.YP;
			}
		},
		Z("z") {
			@Override
			public int choose(int x, int y, int z) {
				return z;
			}

			@Override
			public double choose(double x, double y, double z) {
				return z;
			}

			@Override
			public Direction getDirection(double value) {
				return value < 0 ? NORTH : SOUTH;
			}

			@Override
			public Vec3 getNormal(boolean negative) {
				return negative ? Vec3.ZN : Vec3.ZP;
			}
		};

		public static final Axis[] VALUES;
		private final String name;

		Axis(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public boolean isVertical() {
			return this == Y;
		}

		public boolean isHorizontal() {
			return this == X || this == Z;
		}

		public String toString() {
			return this.name;
		}

		@Override
		public boolean test(Direction direction) {
			return direction != null && direction.getAxis() == this;
		}

		public abstract int choose(int var1, int var2, int var3);

		public abstract double choose(double var1, double var3, double var5);

		public abstract Direction getDirection(double value);

		public double choose(Vec3 vec) {
			return choose(vec.x, vec.y, vec.z);
		}

		public abstract Vec3 getNormal(boolean negative);

		public Direction getDir(Vec3 vec) {
			return getDirection(choose(vec));
		}

		static {
			VALUES = Axis.values();
		}
	}

	public enum AxisDirection {
		POSITIVE(1, "Towards positive"),
		NEGATIVE(-1, "Towards negative");

		private final int offset;
		private final String description;

		private AxisDirection(int offset, String description) {
			this.offset = offset;
			this.description = description;
		}

		public int offset() {
			return this.offset;
		}

		public String getDescription() {
			return this.description;
		}

		public String toString() {
			return this.description;
		}

		public AxisDirection getOpposite() {
			return this == POSITIVE ? NEGATIVE : POSITIVE;
		}
	}

}
