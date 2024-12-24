package net.skds.lib2.mat;

public record Vec2I(int xi, int yi) implements Vec2 {

	public static final Vec2I ZERO = new Vec2I(0, 0);
	public static final Vec2I XP = new Vec2I(1, 0);
	public static final Vec2I XN = new Vec2I(-1, 0);
	public static final Vec2I YP = new Vec2I(0, 1);
	public static final Vec2I YN = new Vec2I(0, -1);

	@Override
	public double x() {
		return xi;
	}

	@Override
	public double y() {
		return yi;
	}

	@Override
	public float xf() {
		return xi;
	}

	@Override
	public float yf() {
		return yi;
	}

	@Override
	public int floorX() {
		return xi;
	}

	@Override
	public int floorY() {
		return yi;
	}

	@Override
	public int ceilX() {
		return xi;
	}

	@Override
	public int ceilY() {
		return yi;
	}

	@Override
	public int roundX() {
		return xi;
	}

	@Override
	public int roundY() {
		return yi;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Vec2 vec) {
			return Vec2.equals(this, vec);
		}
		return false;
	}

	@Override
	public int hashCode() {
		// TODO
		int i = xi;
		return 31 * i + yi;
	}

	@Override
	public Vec2I getAsIntVec() {
		return this;
	}
}
