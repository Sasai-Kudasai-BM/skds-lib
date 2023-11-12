package net.skds.lib.collision;

import net.skds.lib.mat.Vec3;

public interface ConvexShape extends IShape {

	default ProjPair getProjection(Vec3 axis) {
		final Vec3[] points = getPoints();
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double dot = points[i].dot(axis);
			if (dot < min) {
				min = dot;
			}
			if (dot > max) {
				max = dot;
			}
		}
		return new ProjPair(min, max);
	}

	default double getProjectionMin(Vec3 axis) {
		final Vec3[] points = getPoints();
		double min = Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double dot = points[i].dot(axis);
			if (dot < min) {
				min = dot;
			}
		}
		return min;
	}

	default double getProjectionMax(Vec3 axis) {
		final Vec3[] points = getPoints();
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double dot = points[i].dot(axis);
			if (dot > max) {
				max = dot;
			}
		}
		return max;
	}

	default double getProjectionMin(Direction.Axis axis) {
		final Vec3[] points = getPoints();
		double min = Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double dot = axis.choose(points[i]);
			if (dot < min) {
				min = dot;
			}
		}
		return min;
	}

	default double getProjectionMax(Direction.Axis axis) {
		final Vec3[] points = getPoints();
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double dot = axis.choose(points[i]);
			if (dot > max) {
				max = dot;
			}
		}
		return max;
	}

	@Override
	default ConvexCollision.SimpleCollisionResult raytrace(Vec3 from, Vec3 to) {
		Vec3 dir = to.copy().sub(from);
		Vec3 delta = from.copy().scale(-1);

		double tMax = Double.POSITIVE_INFINITY;
		double tMin = Double.NEGATIVE_INFINITY;
		Vec3[] normals = getNormals();
		int normal = 0;
		boolean inverse = false;

		for (int i = 0; i < normals.length; i++) {
			double nomLen = normals[i].dot(delta);
			double denomLen = normals[i].dot(dir);

			double a = (nomLen + getProjectionMax(normals[i])) / denomLen;
			double b = (nomLen + getProjectionMin(normals[i])) / denomLen;
			double min;
			double max;
			if (a < b) {
				min = a;
				max = b;
			} else {
				min = b;
				max = a;
			}

			if (min > tMin) {
				tMin = min;
				normal = i;
				inverse = a > b;
			}
			if (max < tMax) {
				tMax = max;
			}

			if (tMax < tMin) {
				return null;
			}

		}
		Vec3 n = normals[normal].copy();
		if (inverse) {
			n.inverse();
		}
		return new ConvexCollision.SimpleCollisionResult(tMin, n, this);
	}

	Vec3[] getNormals();

	Vec3[] getPoints();

	@Override
	default boolean isConvex() {
		return true;
	}

	default Box createBounding() {
		return new BoxBuilder(getPoints()).build();
	}

}
