package net.skds.lib2.mat.quat;

public record QuatD(double x, double y, double z, double w) implements Quat {

	public static final QuatD ONE = new QuatD(0, 0, 0, 1);


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Quat q) {
			return Quat.equals(this, q);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Quat.hashCode(this);
	}

}