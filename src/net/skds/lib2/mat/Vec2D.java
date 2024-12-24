package net.skds.lib2.mat;

public record Vec2D(double x, double y) implements Vec2 {
	public static final Vec2D ZERO = new Vec2D(0.0D, 0.0D);

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
		int i = Float.floatToIntBits((float) this.x);
		return 31 * i + Float.floatToIntBits((float) this.y);
	}

	@Override
	public Vec2D getAsDoubleVec() {
		return this;
	}
}
