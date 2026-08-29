package net.skds.lib2.shapes;

import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.quat.Quat;
import net.skds.lib2.mat.vec2.Vec2D;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.utils.linkiges.Pair;

public non-sealed interface ConvexShape extends Shape {

	default Vec2D getProjection(Vec3 axis) {
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
		return new Vec2D(min, max);
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
	ConvexShape move(Vec3 delta);

	@Override
	ConvexShape move(double dx, double dy, double dz);

	@Override
	ConvexShape rotate(Matrix3 m3);

	@Override
	default ConvexShape rotate(Quat q) {
		return rotate(Matrix3.fromQuat(q));
	}

	@Override
	ConvexShape scale(double scale);

	@Override
	ConvexShape scale(Vec3 scale);

	@Override
	ConvexShape moveRotScale(Vec3 pos, Matrix3 m3, double scale);

	@Override
	default ConvexShape moveRotScale(Vec3 pos, Quat q, double scale) {
		return moveRotScale(pos, Matrix3.fromQuat(q), scale);
	}

	@Override
	default Collision collide(Shape shapeB, Vec3 velocityBA, CollisionContext context) {
		if (!context.canCollide(this, shapeB)) {
			return null;
		}
		if (shapeB.isConvex()) {
			return ConvexCollision.collide(this, (ConvexShape) shapeB, velocityBA, context);
		} else {
			return CompositeShape.collideConvex((CompositeShape) shapeB, this, velocityBA.inverse(), context);
		}
	}

	@Override
	default boolean intersects(Shape shapeB) {
		if (shapeB.isConvex()) {
			return ConvexCollision.intersects(this, (ConvexShape) shapeB);
		} else {
			return CompositeShape.intersects((CompositeShape) shapeB, this);
		}
	}

	@Override
	default Collision raytrace(Vec3 from, Vec3 to, CollisionContext context) {
		if (!context.canCollide(this, null)) return null;
		Vec3 dir = to.sub(from);
		double pMin = 0;
		double pMax = 1;
		double tMax = Double.POSITIVE_INFINITY;
		double tMin = Double.NEGATIVE_INFINITY;
		Vec3 normal = null;
		boolean inverse = false;
		Vec3[] norms = getNormals();
		for (int i = 0; i < norms.length; i++) {
			Vec3 n = norms[i];
			double nomLen = -n.dot(from);
			double denomLen = n.dot(dir);
			double a = (nomLen + getProjectionMax(n)) / denomLen;
			double b = (nomLen + getProjectionMin(n)) / denomLen;
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
				normal = n;
				inverse = a > b;
			}
			if (max < tMax) {
				tMax = max;
			}
			if (tMin > pMin) {
				pMin = tMin;
			}
			if (tMax < pMax) {
				pMax = tMax;
			}
			if (pMax < pMin) {
				return null;
			}
		}
		if (inverse) {
			normal = normal.inverse();
		}
		if (tMin < 0) {
			return new Collision(0, -tMin, normal, from, null, this, null);
		}
		return new Collision(tMin, 0, normal, from.addScale(dir, tMin), null, this, null);
	}

	@Override
	default boolean intersectsRay(Vec3 from, Vec3 to) {
		Vec3 dir = to.sub(from);
		double pMin = 0;
		double pMax = 1;
		double tMax = Double.POSITIVE_INFINITY;
		double tMin = Double.NEGATIVE_INFINITY;
		Vec3[] norms = getNormals();
		for (int i = 0; i < norms.length; i++) {
			Vec3 n = norms[i];
			double nomLen = -n.dot(from);
			double denomLen = n.dot(dir);
			double a = (nomLen + getProjectionMax(n)) / denomLen;
			double b = (nomLen + getProjectionMin(n)) / denomLen;
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
			}
			if (max < tMax) {
				tMax = max;
			}
			if (tMin > pMin) {
				pMin = tMin;
			}
			if (tMax < pMax) {
				pMax = tMax;
			}
			if (pMax < pMin) {
				return false;
			}
		}
		return true;
	}

	Vec3[] getNormals();

	Vec3[] getPoints();

	@SuppressWarnings("unchecked")
	default Pair<Vec3, Vec3>[] getLines() {
		Vec3[] points = getPoints();
		if (points.length == 8) {
			return new Pair[]{
					new Pair<>(points[0], points[1]), //000 001
					new Pair<>(points[1], points[5]), //001 101
					new Pair<>(points[5], points[4]), //101 100
					new Pair<>(points[4], points[0]), //100 000

					new Pair<>(points[2], points[3]), //010 011
					new Pair<>(points[3], points[7]), //011 111
					new Pair<>(points[7], points[6]), //111 110
					new Pair<>(points[6], points[2]), //110 010

					new Pair<>(points[0], points[2]), //000 010
					new Pair<>(points[1], points[3]), //001 011
					new Pair<>(points[4], points[6]), //100 110
					new Pair<>(points[5], points[7]), //101 111
			};
		}
		return new Pair[]{};
	}

	@Override
	default boolean isConvex() {
		return true;
	}

	double surfaceArea();

	// TODO ellipsoid
	static boolean equals(ConvexShape shape1, ConvexShape shape2) {
		if (shape1 == shape2) {
			return true;
		}
		if (shape1 == null || shape2 == null) {
			return false;
		}
		switch (shape1) {
			case AABB aabb1 when shape2 instanceof AABB aabb2 -> {
				return aabb1.equals(aabb2);
			}
			case OBB obb1 when shape2 instanceof OBB obb2 -> {
				return obb1.equals(obb2);
			}
			case AABB aabb when shape2 instanceof OBB obb && Matrix3.equals(obb.normals, Matrix3.SINGLE) -> {
				return aabb.equals(obb.getBoundingBox());
			}
			default -> {
			}
		}
		if (shape2 instanceof AABB aabb && shape1 instanceof OBB obb && Matrix3.equals(obb.normals, Matrix3.SINGLE)) {
			return aabb.equals(obb.getBoundingBox());
		}
		return false;
	}


}
