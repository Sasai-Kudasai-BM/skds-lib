package net.skds.lib2.mat;

@SuppressWarnings("unused")
public record Vec3F(float xf, float yf, float zf) implements Vec3 {

	public static final Vec3F XN = new Vec3F(-1.0F, 0.0F, 0.0F);
	public static final Vec3F XP = new Vec3F(1.0F, 0.0F, 0.0F);
	public static final Vec3F YN = new Vec3F(0.0F, -1.0F, 0.0F);
	public static final Vec3F YP = new Vec3F(0.0F, 1.0F, 0.0F);
	public static final Vec3F ZN = new Vec3F(0.0F, 0.0F, -1.0F);
	public static final Vec3F ZP = new Vec3F(0.0F, 0.0F, 1.0F);
	public static final Vec3F SINGLE = new Vec3F(1.0F, 1.0F, 1.0F);
	public static final Vec3F ZERO = new Vec3F(0.0F, 0.0F, 0.0F);

	@Override
	public double x() {
		return xf;
	}

	@Override
	public double y() {
		return yf;
	}

	@Override
	public double z() {
		return zf;
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
	public int zi() {
		return (int) zf;
	}

	public Vec3F(Vec3 vec) {
		this(vec.xf(), vec.yf(), vec.zf());
	}

	public Vec3F(float size) {
		this(size, size, size);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Vec3 vec) {
			return Vec3.equals(this, vec);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Vec3.hashCode(this);
	}

	@Override
	public Vec3F getAsFloatVec() {
		return this;
	}
}