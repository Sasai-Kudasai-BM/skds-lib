package net.skds.lib2.mat.vec2;

import lombok.Getter;
import net.skds.lib2.mat.AxisDirection;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.utils.ArrayUtils;

import java.util.Random;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public enum Direction2D implements Vec2 {
	DOWN(0, 1, AxisDirection.NEGATIVE, Axis.Y, new Vec2I(0, -1)),
	UP(1, 0, AxisDirection.POSITIVE, Axis.Y, new Vec2I(0, 1)),
	RIGHT(2, 3, AxisDirection.NEGATIVE, Axis.X, new Vec2I(-1, 0)),
	LEFT(3, 2, AxisDirection.POSITIVE, Axis.X, new Vec2I(1, 0));

	public static final Direction2D[] VALUES = values();

	@Getter
	private final int id;
	private final int idOpposite;
	@Getter
	private final Axis axis;
	@Getter
	private final AxisDirection direction;
	private final Vec2I vector;

	public static final Direction2D XP = LEFT;
	public static final Direction2D XN = RIGHT;
	public static final Direction2D YP = UP;
	public static final Direction2D YN = DOWN;


	private static Direction2D[][] shuffleA;

	Direction2D(int id, int idOpposite, AxisDirection direction, Axis axis, Vec2I vector) {
		this.id = id;
		this.idOpposite = idOpposite;
		this.axis = axis;
		this.direction = direction;
		this.vector = vector;
	}

	public Direction2D getOpposite() {
		return VALUES[this.idOpposite];
	}

	public static Direction2D[] randomAll() {
		return randomAll(FastMath.RANDOM);
	}

	public static Direction2D[] randomAll(Random random) {
		Direction2D[][] a = shuffleA;
		if (a == null) {
			synchronized (Direction2D.class) {
				a = shuffleA;
				if (a == null) {
					a = ArrayUtils.shuffleArray(VALUES);
					shuffleA = a;
				}
			}
		}
		return ArrayUtils.getRandom(a, random);
	}

	public Direction2D rotateClockwise() {
		return switch (this) {
			case UP -> RIGHT;
			case RIGHT -> DOWN;
			case DOWN -> LEFT;
			case LEFT -> UP;
		};
	}

	public Direction2D rotateCounterclockwise() {
		return switch (this) {
			case UP -> LEFT;
			case LEFT -> DOWN;
			case DOWN -> RIGHT;
			case RIGHT -> UP;
		};
	}

	@Override
	public double x() {
		return this.vector.x();
	}

	@Override
	public double y() {
		return this.vector.y();
	}

	@Override
	public float xf() {
		return this.vector.xf();
	}

	@Override
	public float yf() {
		return this.vector.yf();
	}

	@Override
	public int xi() {
		return this.vector.xi();
	}

	@Override
	public int yi() {
		return this.vector.yi();
	}

	@Override
	public int floorX() {
		return this.vector.floorX();
	}

	@Override
	public int floorY() {
		return this.vector.floorY();
	}

	@Override
	public int ceilX() {
		return this.vector.ceilX();
	}

	@Override
	public int ceilY() {
		return this.vector.ceilY();
	}

	@Override
	public int roundX() {
		return this.vector.roundX();
	}

	@Override
	public int roundY() {
		return this.vector.roundY();
	}

	public static Direction2D byId(int id) {
		return VALUES[ArrayUtils.loop(id, VALUES.length)];
	}

	public static Direction2D from(Axis axis, AxisDirection direction) {
		return switch (axis) {
			case X -> {
				if (direction == AxisDirection.POSITIVE) {
					yield LEFT;
				}
				yield RIGHT;
			}
			case Y -> {
				if (direction == AxisDirection.POSITIVE) {
					yield UP;
				}
				yield DOWN;
			}
		};
	}

	public static Direction2D getFacing(double x, double y) {
		return Direction2D.getFacing((float) x, (float) y);
	}

	public static Direction2D getFacing(Vec2 from, Vec2 to) {
		return getFacing((float) (to.x() - from.x()), (float) (to.y() - from.y()));
	}

	public static Direction2D getFacingFrom(Vec2 from, Vec2 to, Iterable<Direction2D> iterable) {
		return getFacingFrom((float) (to.x() - from.x()), (float) (to.y() - from.y()), iterable);
	}

	public static Direction2D getFacingFrom(float x, float y, Iterable<Direction2D> iterable) {
		Direction2D direction = null;
		float f = Float.NEGATIVE_INFINITY;
		for (Direction2D direction2 : iterable) {
			float g = x * direction2.xf() + y * direction2.yf();
			if (!(g > f))
				continue;
			f = g;
			direction = direction2;
		}
		return direction;
	}

	public static Direction2D getFacing(float x, float y) {
		Direction2D direction = UP;
		float f = Float.NEGATIVE_INFINITY;
		for (Direction2D direction2 : VALUES) {
			float g = x * direction2.xf() + y * direction2.yf();
			if (!(g > f))
				continue;
			f = g;
			direction = direction2;
		}
		return direction;
	}

	public static Direction2D getFacing(int x, int y) {
		Direction2D direction = UP;
		int f = Integer.MIN_VALUE;
		for (Direction2D direction2 : VALUES) {
			int g = x * direction2.xi() + y * direction2.yi();
			if (!(g > f))
				continue;
			f = g;
			direction = direction2;
		}
		return direction;
	}


	public static Direction2D getFromOffset(int x, int y) {
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
		return null;
	}

	public static Direction2D get(AxisDirection direction, Axis axis) {
		for (Direction2D direction2 : VALUES) {
			if (direction2.getDirection() != direction || direction2.getAxis() != axis)
				continue;
			return direction2;
		}
		throw new IllegalArgumentException("No such direction: " + direction + " " + axis);
	}

	public Vec2I getOffset() {
		return this.vector;
	}

	@Getter
	public enum Axis implements Predicate<Direction2D> {
		X("xf") {
			@Override
			public int choose(int x, int y) {
				return x;
			}

			@Override
			public double choose(double x, double y) {
				return x;
			}

			@Override
			public Direction2D getDirection(double value) {
				return value < 0 ? RIGHT : LEFT;
			}

			@Override
			public Direction2D getPositiveDirection() {
				return LEFT;
			}

			@Override
			public Direction2D getDirection(boolean positive) {
				return positive ? Direction2D.XP : Direction2D.XN;
			}
		},
		Y("yf") {
			@Override
			public int choose(int x, int y) {
				return y;
			}

			@Override
			public double choose(double x, double y) {
				return y;
			}

			@Override
			public Direction2D getDirection(double value) {
				return value < 0 ? DOWN : UP;
			}

			@Override
			public Direction2D getPositiveDirection() {
				return UP;
			}

			@Override
			public Direction2D getDirection(boolean positive) {
				return positive ? Direction2D.YP : Direction2D.YN;
			}
		};

		public static final Axis[] VALUES;
		private final String name;

		Axis(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}

		@Override
		public boolean test(Direction2D direction) {
			return direction != null && direction.getAxis() == this;
		}

		public abstract int choose(int var1, int var2);

		public abstract double choose(double var1, double var3);

		public abstract Direction2D getDirection(double value);

		public abstract Direction2D getDirection(boolean positive);

		public abstract Direction2D getPositiveDirection();

		public double choose(Vec2 vec) {
			return choose(vec.x(), vec.y());
		}

		public Direction2D getDir(Vec2 vec) {
			return getDirection(choose(vec));
		}

		static {
			VALUES = Axis.values();
		}
	}


	@Override
	public Vec2I getAsIntVec() {
		return this.vector;
	}

	@Override
	public Vec2F getAsFloatVec() {
		return this.vector.getAsFloatVec();
	}

	@Override
	public Vec2D getAsDoubleVec() {
		return this.vector.getAsDoubleVec();
	}
}
