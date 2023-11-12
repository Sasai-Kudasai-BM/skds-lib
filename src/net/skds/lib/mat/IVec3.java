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
}
