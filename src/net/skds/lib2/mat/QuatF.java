package net.skds.lib2.mat;


public record QuatF(float xf, float yf, float zf, float wf) implements Quat {

	public static final QuatF ONE = new QuatF(0, 0, 0, 1);

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
	public double w() {
		return wf;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Vec4 vec) {
			return Vec4.equals(this, vec);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Vec4.hashCode(this);
	}
}