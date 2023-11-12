package net.skds.lib.collision;

import net.skds.lib.mat.FastMath;
import net.skds.lib.mat.Vec3;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * An enum representing 6 cardinal directions in Minecraft.
 *
 * <p>In Minecraft, the X axis determines the east-west direction, the Y axis determines
 * the up-down direction, and the Z axis determines the south-north direction (note
 * that positive-Z direction is south, not north).
 */
public enum Direction {
	DOWN(0, 1, -1, "down", AxisDirection.NEGATIVE, Axis.Y, new BlockPos(0, -1, 0)),
	UP(1, 0, -1, "up", AxisDirection.POSITIVE, Axis.Y, new BlockPos(0, 1, 0)),
	NORTH(2, 3, 2, "north", AxisDirection.NEGATIVE, Axis.Z, new BlockPos(0, 0, -1)),
	SOUTH(3, 2, 0, "south", AxisDirection.POSITIVE, Axis.Z, new BlockPos(0, 0, 1)),
	WEST(4, 5, 1, "west", AxisDirection.NEGATIVE, Axis.X, new BlockPos(-1, 0, 0)),
	EAST(5, 4, 3, "east", AxisDirection.POSITIVE, Axis.X, new BlockPos(1, 0, 0));

	public static Direction[] values = values();

	private final int id;
	private final int idOpposite;
	private final int idHorizontal;
	private final String name;
	private final Axis axis;
	private final AxisDirection direction;
	private final BlockPos vector;
	private static final Direction[] ALL;
	private static final Direction[] VALUES;
	private static final Direction[] HORIZONTAL;

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

	private Direction(int id, int idOpposite, int idHorizontal, String name, AxisDirection direction, Axis axis,
					  BlockPos vector) {
		this.id = id;
		this.idHorizontal = idHorizontal;
		this.idOpposite = idOpposite;
		this.name = name;
		this.axis = axis;
		this.direction = direction;
		this.vector = vector;
	}

	//public static Direction[] getEntityFacingOrder(Entity entity) {
	//    Direction direction3;
	//    float f = entity.getPitch(1.0f) * ((float)Math.PI / 180);
	//    float g = -entity.getYaw(1.0f) * ((float)Math.PI / 180);
	//    float h = Math.sin(f);
	//    float i = Math.cos(f);
	//    float j = Math.sin(g);
	//    float k = Math.cos(g);
	//    boolean bl = j > 0.0f;
	//    boolean bl2 = h < 0.0f;
	//    boolean bl3 = k > 0.0f;
	//    float l = bl ? j : -j;
	//    float m = bl2 ? -h : h;
	//    float n = bl3 ? k : -k;
	//    float o = l * i;
	//    float p = n * i;
	//    Direction direction = bl ? EAST : WEST;
	//    Direction direction2 = bl2 ? UP : DOWN;
	//    Direction direction4 = direction3 = bl3 ? SOUTH : NORTH;
	//    if (l > n) {
	//        if (m > o) {
	//            return Direction.listClosest(direction2, direction, direction3);
	//        }
	//        if (p > m) {
	//            return Direction.listClosest(direction, direction3, direction2);
	//        }
	//        return Direction.listClosest(direction, direction2, direction3);
	//    }
	//    if (m > p) {
	//        return Direction.listClosest(direction2, direction3, direction);
	//    }
	//    if (o > m) {
	//        return Direction.listClosest(direction3, direction, direction2);
	//    }
	//    return Direction.listClosest(direction3, direction2, direction);
	//}

	/**
	 * Helper function that returns the 3 directions given, followed by the 3 opposite given in opposite order.
	 */

	//public static Direction transform(Matrix4f matrix, Direction direction) {
	//    BlockPos BlockPos = direction.getVector();
	//    Vector4f vector4f = matrix.transform(new Vector4f(BlockPos.getX(), BlockPos.getY(), BlockPos.getZ(), 0.0f));
	//    return Direction.getFacing(vector4f.x(), vector4f.y(), vector4f.z());
	//}

	///**
	// * {@return a shuffled collection of all directions}
	// */
	//public static Collection<Direction> shuffle(Random random) {
	//    return Util.copyShuffled(Direction.values(), random);
	//}

	//public static Stream<Direction> stream() {
	//    return Stream.of(ALL);
	//}

	//public Quaternionf getRotationQuaternion() {
	//    return switch (this) {
	//        default -> throw new IncompatibleClassChangeError();
	//        case DOWN -> new Quaternionf().rotationX((float)Math.PI);
	//        case UP -> new Quaternionf();
	//        case NORTH -> new Quaternionf().rotationXYZ(1.5707964f, 0.0f, (float)Math.PI);
	//        case SOUTH -> new Quaternionf().rotationX(1.5707964f);
	//        case WEST -> new Quaternionf().rotationXYZ(1.5707964f, 0.0f, 1.5707964f);
	//        case EAST -> new Quaternionf().rotationXYZ(1.5707964f, 0.0f, -1.5707964f);
	//    };
	//}
	public int getId() {
		return this.id;
	}

	public int getHorizontal() {
		return this.idHorizontal;
	}

	public AxisDirection getDirection() {
		return this.direction;
	}

	//public static Direction getLookDirectionForAxis(Entity entity, Axis axis) {
	//    return switch (axis) {
	//        default -> throw new IncompatibleClassChangeError();
	//        case X -> {
	//            if (EAST.pointsTo(entity.getYaw(1.0f))) {
	//                yield EAST;
	//            }
	//            yield WEST;
	//        }
	//        case Z -> {
	//            if (SOUTH.pointsTo(entity.getYaw(1.0f))) {
	//                yield SOUTH;
	//            }
	//            yield NORTH;
	//        }
	//        case Y -> entity.getPitch(1.0f) < 0.0f ? UP : DOWN;
	//    };
	//}

	public Direction getOpposite() {
		return Direction.byId(this.idOpposite);
	}

	public Direction rotateClockwise(Axis axis) {
		return switch (axis) {
			default -> throw new IncompatibleClassChangeError();
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

	public Direction rotateCounterclockwise(Axis axis) {
		return switch (axis) {
			default -> throw new IncompatibleClassChangeError();
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
			default -> throw new IncompatibleClassChangeError();
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

	public static Direction getFacing(float x, float y, float z) {
		Direction direction = NORTH;
		float f = Float.MIN_VALUE;
		for (Direction direction2 : ALL) {
			float g = x * direction2.vector.x + y * direction2.vector.y + z * direction2.vector.z;
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
		for (Direction direction2 : ALL) {
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

	/**
	 * {@return whether the given yaw points to the direction}
	 *
	 * @implNote This returns whether the yaw can make an acute angle with the direction.
	 *
	 * <p>This always returns {@code false} for vertical directions.
	 */
	public boolean pointsTo(float yaw) {
		float f = yaw * ((float) Math.PI / 180);
		float g = -FastMath.sin(f);
		float h = FastMath.cos(f);
		return (float) this.vector.x * g + (float) this.vector.z * h > 0.0f;
	}

	static {
		ALL = Direction.values();
		VALUES = (Direction[]) Arrays.stream(ALL).sorted(Comparator.comparingInt(direction -> direction.id))
				.toArray(Direction[]::new);
		HORIZONTAL = (Direction[]) Arrays.stream(ALL).filter(direction -> direction.getAxis().isHorizontal())
				.sorted(Comparator.comparingInt(direction -> direction.idHorizontal)).toArray(Direction[]::new);
	}

	/*
	 * Uses 'sealed' constructs - enablewith --sealed true
	 */
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

	public static enum AxisDirection {
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
