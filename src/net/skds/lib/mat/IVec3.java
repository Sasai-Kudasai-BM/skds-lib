package net.skds.lib.mat;

public interface IVec3 {

	public static final IVec3 ZERO = new IVec3() {

		@Override
		public double x() {
			return 0;
		}

		@Override
		public double y() {
			return 0;
		}

		@Override
		public double z() {
			return 0;
		}

	};

	double x();

	double y();

	double z();

	default int floorX() {
		return (int) Math.floor(x());
	}

	default int floorY() {
		return (int) Math.floor(y());
	}

	default int floorZ() {
		return (int) Math.floor(z());
	}

	default int roundX() {
		return (int) x();
	}

	default int roundY() {
		return (int) y();
	}

	default int roundZ() {
		return (int) z();
	}

	default double distanceTo(IVec3 vec) {
		double d0 = vec.x() - this.x();
		double d1 = vec.y() - this.y();
		double d2 = vec.z() - this.z();
		return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	}

	default double distanceTo(double dx, double dy, double dz) {
		double d0 = dx - this.x();
		double d1 = dy - this.y();
		double d2 = dz - this.z();
		return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	}

	default double squareDistanceTo(IVec3 vec) {
		double d0 = vec.x() - this.x();
		double d1 = vec.y() - this.y();
		double d2 = vec.z() - this.z();
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	default double squareDistanceTo(double xIn, double yIn, double zIn) {
		double d0 = xIn - this.x();
		double d1 = yIn - this.y();
		double d2 = zIn - this.z();
		return d0 * d0 + d1 * d1 + d2 * d2;
	}
}
