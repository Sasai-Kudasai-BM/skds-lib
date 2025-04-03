package net.skds.lib2.shapes2d;

import net.skds.lib2.mat.matrix2.Matrix2;
import net.skds.lib2.mat.vec2.Direction2D;
import net.skds.lib2.mat.vec2.Vec2;
import net.skds.lib2.mat.vec2.Vec2D;
import net.skds.lib2.utils.linkiges.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public non-sealed interface ConvexShape2D extends Shape2D {

	default Vec2D getProjection(Vec2 axis) {
		final Vec2[] points = getPoints();
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

	default double getProjectionMin(Vec2 axis) {
		final Vec2[] points = getPoints();
		double min = Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double dot = points[i].dot(axis);
			if (dot < min) {
				min = dot;
			}
		}
		return min;
	}

	default double getProjectionMax(Vec2 axis) {
		final Vec2[] points = getPoints();
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double dot = points[i].dot(axis);
			if (dot > max) {
				max = dot;
			}
		}
		return max;
	}

	default double getProjectionMin(Direction2D.Axis axis) {
		final Vec2[] points = getPoints();
		double min = Double.MAX_VALUE;
		for (int i = 0; i < points.length; i++) {
			double dot = axis.choose(points[i]);
			if (dot < min) {
				min = dot;
			}
		}
		return min;
	}

	default double getProjectionMax(Direction2D.Axis axis) {
		final Vec2[] points = getPoints();
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
	ConvexShape2D move(Vec2 delta);

	@Override
	ConvexShape2D rotate(Matrix2 m);

	@Override
	ConvexShape2D moveRotScale(Vec2 pos, Matrix2 m2, double scale);

	@Override
	ConvexShape2D scale(double scale);

	@Override
	default Collision2D collide(Shape2D shapeB, Vec2 velocityBA, CollisionContext2D context) {
		if (shapeB instanceof ConvexShape2D convex) {
			return ConvexShape2D.collide(this, convex, velocityBA, context);
		} else if (shapeB instanceof CompositeShape2D composite) {
			return CompositeShape2D.collideConvex(composite, this, velocityBA.inverse(), context);
		}
		throw new UnsupportedOperationException("Unable to collide \"%s\" with \"%s\"".formatted(this, shapeB));
	}

	@Override
	default Collision2D raytrace(Vec2 from, Vec2 to, CollisionContext2D context) {
		Vec2 dir = to.sub(from);

		double tMax = Double.POSITIVE_INFINITY;
		double tMin = Double.NEGATIVE_INFINITY;
		Vec2 normal = null;
		boolean inverse = false;

		Vec2[] norms = getNormals();
		for (int i = 0; i < norms.length; i++) {
			Vec2 n = norms[i];
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

			if (tMax < tMin) {
				return null;
			}

		}
		if (inverse) {
			normal = normal.inverse();
		}
		return new Collision2D(tMin, 0, normal, from.add(dir.normalizeScale(tMin)), null, this, null);
	}

	Vec2[] getNormals();

	Vec2[] getPoints();

	@SuppressWarnings("unchecked")
	default Pair<Vec2, Vec2>[] getLines() {
		Vec2[] points = getPoints();
		if (points.length == 4) {
			return new Pair[]{
					new Pair<>(points[0], points[1]),
					new Pair<>(points[1], points[2]),
					new Pair<>(points[2], points[3]),
					new Pair<>(points[3], points[0])
			};
		}
		return new Pair[]{};
	}

	@Override
	default boolean isConvex() {
		return true;
	}

	double surfaceArea();

	static boolean equals(AABR shape1, AABR shape2) {
		if (shape1 == shape2) {
			return true;
		}
		if (shape1 == null || shape2 == null) {
			return false;
		}
		if (shape1.minX != shape2.minX) {
			return false;
		}
		if (shape1.minY != shape2.minY) {
			return false;
		}
		if (shape1.maxX != shape2.maxX) {
			return false;
		}
		return shape1.maxY == shape2.maxY;
	}


	static Collision2D collide(ConvexShape2D s1, ConvexShape2D s2, Vec2 velocity21, CollisionContext2D context) {

		Vec2[] s1Norm = s1.getNormals();
		Vec2[] s2Norm = s2.getNormals();

		List<Vec2> terminators = new ArrayList<>();
		if (s1Norm.length * s2Norm.length == 0) {
			terminators.add(velocity21.normalize());
		} else {
			Collections.addAll(terminators, s1Norm);
			Collections.addAll(terminators, s2Norm);
		}

		return intersectionMoving(s1, s2, terminators, velocity21);
	}

	private static Collision2D intersectionMoving(ConvexShape2D a, ConvexShape2D b, List<Vec2> terminators, Vec2 velocityBA) {

		double pMin = 0;
		double pMax = 1;

		for (int i = 0; i < terminators.size(); i++) {
			Vec2 terminator = terminators.get(i);
			double v = velocityBA.dot(terminator);
			double aMin = a.getProjectionMin(terminator);
			double aMax = a.getProjectionMax(terminator);
			double bMin = b.getProjectionMin(terminator);
			double bMax = b.getProjectionMax(terminator);

			if (Math.abs(v) < 1E-30) {
				if (aMax + aMin < bMax + bMin) {
					if (aMax <= bMin) {
						return null;
					}
				} else {
					if (bMax <= aMin) {
						return null;
					}
				}
				continue;
			}

			double tMin = (aMin - bMax) / v;
			double tMax = (aMax - bMin) / v;

			if (v < 0) {
				double d = tMin;
				tMin = tMax;
				tMax = d;
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

		final double distance = pMin;

		double termLen = Double.MAX_VALUE;
		Vec2 minTerm = null;
		boolean nInv = false;
		for (int i = 0; i < terminators.size(); i++) {
			Vec2 terminator = terminators.get(i);
			double v = velocityBA.dot(terminator);
			double aMin = a.getProjectionMin(terminator);
			double aMax = a.getProjectionMax(terminator);
			double bMin = b.getProjectionMin(terminator) + v * distance;
			double bMax = b.getProjectionMax(terminator) + v * distance;

			double tMin = Math.max(aMin, bMin);
			double tMax = Math.min(aMax, bMax);

			double d = tMax - tMin;

			if (d < termLen) {
				double dc = (bMax + bMin) - (aMax + aMin);
				nInv = dc > 0;
				termLen = d;
				minTerm = terminator;
			}
		}

		assert minTerm != null : "nan or infinite values";
		if (!nInv) {
			minTerm = minTerm.inverse();
		}
		if (distance > 0) {
			termLen = 0;
		} else if (termLen < 0) {
			termLen = -termLen;
		}
		return new Collision2D(distance, termLen, minTerm, Vec2.ZERO, null, a, b);
	}

	static Collision2D collideAABB(AABR a, AABR b, Vec2 velocityBA, CollisionContext2D context) {

		double pMin = 0;
		double pMax = 1;

		var axs = Direction2D.Axis.VALUES;

		for (int i = 0; i < axs.length; i++) {
			Direction2D.Axis axis = axs[i];
			double v = axis.choose(velocityBA);
			double aMin = a.getProjectionMin(axis);
			double aMax = a.getProjectionMax(axis);
			double bMin = b.getProjectionMin(axis);
			double bMax = b.getProjectionMax(axis);

			if (Math.abs(v) < 1E-30) {
				if (aMax + aMin < bMax + bMin) {
					if (aMax <= bMin) {
						return null;
					}
				} else {
					if (bMax <= aMin) {
						return null;
					}
				}
				continue;
			}

			double tMin = (aMin - bMax) / v;
			double tMax = (aMax - bMin) / v;

			if (v < 0) {
				double d = tMin;
				tMin = tMax;
				tMax = d;
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

		final double distance = pMin;

		double termLen = Double.MAX_VALUE;
		Direction2D minTerm = null;

		for (int i = 0; i < axs.length; i++) {
			Direction2D.Axis axis = axs[i];
			double v = axis.choose(velocityBA);
			double aMin = a.getProjectionMin(axis);
			double aMax = a.getProjectionMax(axis);
			double bMin = b.getProjectionMin(axis) + v * distance;
			double bMax = b.getProjectionMax(axis) + v * distance;

			double tMin = Math.max(aMin, bMin);
			double tMax = Math.min(aMax, bMax);

			double d = tMax - tMin;

			if (d < termLen) {
				double dc = (bMax + bMin) - (aMax + aMin);
				termLen = d;
				minTerm = axis.getDirection(dc > 0);
			}
		}

		assert minTerm != null : "nan or infinite values";
		if (distance > 0) {
			termLen = 0;
		} else if (termLen < 0) {
			termLen = -termLen;
		}
		return new Collision2D(distance, termLen, minTerm, Vec2.ZERO, minTerm, a, b);
	}
}
