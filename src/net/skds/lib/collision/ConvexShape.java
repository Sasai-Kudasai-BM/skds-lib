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

	// TODO Direction
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
		Direction direction = switch (normal) {
			case 0 -> inverse ? Direction.WEST : Direction.EAST;
			case 1 -> inverse ? Direction.NORTH : Direction.SOUTH;
			case 2 -> inverse ? Direction.DOWN : Direction.UP;
			default -> null;
		};
		return new ConvexCollision.SimpleCollisionResult(tMin, n, direction, this);
	}

	@Override
	default ConvexCollision.CollisionResult collide(IShape shape, Vec3 relativeVelocity) {
		Box ext = getBoundingBox();
		if (relativeVelocity != null) {
			ext = ext.stretch(relativeVelocity);
		}
		if (!ext.intersects(shape.getBoundingBox())) {
			return null;
		}

		ConvexCollision.CollisionResult cc = null;
		final ConvexShape[] shapes = shape instanceof CompositeShape cs ? cs.simplify() : new ConvexShape[]{(ConvexShape) shape};
		for (int i = 0; i < shapes.length; i++) {
			final ConvexShape convexShape = shapes[i];
			final Box convexBound = convexShape.getBoundingBox();
			if (!convexBound.intersects(ext)) {
				continue;
			}
			if (getBoundingBox().intersects(convexBound)) {
				ConvexCollision.CollisionResult cc2 = ConvexCollision.collide(convexShape, this, relativeVelocity);
				if (cc2 != null) {
					if (cc == null) {
						cc = cc2;
						cc.setShape(this);
					} else if (cc2.distance < cc.distance) {
						cc = cc2;
						cc.setShape(this);
					}
				}
			}
		}
		return cc;
	}

	Vec3[] getNormals();

	Vec3[] getPoints();
	
	default Vec3[] getPointsNew() {
		return getPoints();
	}

	@Override
	default boolean isConvex() {
		return true;
	}

	default Box createBounding() {
		return new BoxBuilder(getPoints()).build();
	}

}
