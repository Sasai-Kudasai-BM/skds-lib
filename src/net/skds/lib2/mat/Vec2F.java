package net.skds.lib2.mat;

public record Vec2F(float xf, float yf) implements Vec2 {
	public static final Vec2F ZERO = new Vec2F(0.0F, 0.0F);
	@Override
	public double x() {
		return xf;
	}

	@Override
	public double y() {
		return yf;
	}

	@Override
	public int xi() {
		return (int) xf;
	}

	@Override
	public int yi() {
		return (int) yf;
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
		int i = Float.floatToIntBits((float) this.xf);
		return 31 * i + Float.floatToIntBits((float) this.yf);
	}

	@Override
	public Vec2F getAsFloatVec() {
		return this;
	}
}
