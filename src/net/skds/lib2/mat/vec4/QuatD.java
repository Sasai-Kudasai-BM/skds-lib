package net.skds.lib2.mat.vec4;

public record QuatD(double x, double y, double z, double w) implements Quat {

	public static final QuatD ONE = new QuatD(0, 0, 0, 1);


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