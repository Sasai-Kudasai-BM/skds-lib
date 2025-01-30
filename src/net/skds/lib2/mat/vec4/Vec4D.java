package net.skds.lib2.mat.vec4;

public record Vec4D(double x, double y, double z, double w) implements Vec4 {

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
