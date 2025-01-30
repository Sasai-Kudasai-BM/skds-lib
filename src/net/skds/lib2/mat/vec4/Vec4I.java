package net.skds.lib2.mat.vec4;

public record Vec4I(int xi, int yi, int zi, int wi) implements Vec4 {

	@Override
	public double x() {
		return this.xi;
	}

	@Override
	public double y() {
		return this.yi;
	}

	@Override
	public double z() {
		return this.zi;
	}

	@Override
	public double w() {
		return this.wi;
	}

	@Override
	public int floorX() {
		return this.xi;
	}

	@Override
	public int floorY() {
		return this.yi;
	}

	@Override
	public int floorZ() {
		return this.zi;
	}

	@Override
	public int floorW() {
		return this.wi;
	}

	@Override
	public int ceilX() {
		return this.xi;
	}

	@Override
	public int ceilY() {
		return this.yi;
	}

	@Override
	public int ceilZ() {
		return this.zi;
	}

	@Override
	public int ceilW() {
		return this.wi;
	}

	@Override
	public int roundX() {
		return this.xi;
	}

	@Override
	public int roundY() {
		return this.yi;
	}

	@Override
	public int roundZ() {
		return this.zi;
	}

	@Override
	public int roundW() {
		return this.wi;
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
