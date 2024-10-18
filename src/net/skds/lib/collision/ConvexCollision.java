package net.skds.lib.collision;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib.collision.Direction.Axis;
import net.skds.lib.mat.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConvexCollision {

	public static CollisionResult collide(ConvexShape s1, ConvexShape s2, Vec3 velocity21) {

		Vec3[] s1Norm = s1.getNormals();
		Vec3[] s2Norm = s2.getNormals();

		List<Vec3> terminators = new ArrayList<>();
		if (s1Norm.length * s2Norm.length == 0) {
			terminators.add(velocity21.copy().normalize());
		}

		Collections.addAll(terminators, s1Norm);
		Collections.addAll(terminators, s2Norm);

		for (int i = 0; i < s1Norm.length; i++) {
			for (int j = 0; j < s2Norm.length; j++) {
				Vec3 cross = s1Norm[i].copy().cross(s2Norm[j]);
				double len = cross.length();
				if (len > 1E-30) {
					terminators.add(cross);
				}
				cross.div(len);
			}
		}
		return intersectionMoving(s1, s2, terminators, velocity21);
	}

	private static CollisionResult intersectionMoving(ConvexShape a, ConvexShape b, List<Vec3> terminators, Vec3 velocityBA) {

		double pMin = 0;
		double pMax = 1;

		for (int i = 0; i < terminators.size(); i++) {
			Vec3 terminator = terminators.get(i);
			double v = velocityBA.dot(terminator);
			double aMin = a.getProjectionMin(terminator);
			double aMax = a.getProjectionMax(terminator);
			double bMin = b.getProjectionMin(terminator);
			double bMax = b.getProjectionMax(terminator);

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
		Vec3 minTerm = null;
		boolean nInv = false;
		for (int i = 0; i < terminators.size(); i++) {
			Vec3 terminator = terminators.get(i);
			double v = velocityBA.dot(terminator);
			double aMin = a.getProjectionMin(terminator);
			double aMax = a.getProjectionMax(terminator);
			double bMin = b.getProjectionMin(terminator) + v * distance;
			double bMax = b.getProjectionMax(terminator) + v * distance;

			double tMin = Math.max(aMin, bMin);
			double tMax = Math.min(aMax, bMax);

			double d = tMax - tMin;

			if (d < termLen) {
				double dc = (aMax - aMin) - (bMax - bMin);
				nInv = v * dc < 0;
				termLen = d;
				minTerm = terminator;
			}
		}

		assert minTerm != null : "nan or infinite values";
		minTerm = minTerm.copy();
		if (nInv) {
			minTerm.inverse();
		}
		if (distance > 0) {
			termLen = 0;
		} else if (termLen < 0) {
			termLen = -termLen;
		}
		return new CollisionResult(distance, termLen, minTerm, new Vec3(), null, null);
	}

	public static SimpleCollisionResult intersectionMoving(Box a, Box b, Vec3 velocityBA) {

		if (a.intersects(b)) {
			final Vec3 ac = a.getCenter();
			final Vec3 bc = b.getCenter();
			final Box inter = a.intersection(b);
			final Axis term = inter.minTerminator();
			final double d = term.choose(bc) - term.choose(ac);
			final Vec3 norm = term.getDirection(d).createVector3D();
			if (d * term.choose(velocityBA) >= 0) {
				return null;
			}
			return new SimpleCollisionResult(0, norm, null, null, inter.getProjection(term));
		}

		double pMin = 0;
		double pMax = 1;

		for (int i = 0; i < Axis.VALUES.length; i++) {
			Axis terminator = Axis.VALUES[i];
			double v = terminator.choose(velocityBA);
			double aMin = a.getMin(terminator);
			double aMax = a.getMax(terminator);
			double bMin = b.getMin(terminator);
			double bMax = b.getMax(terminator);

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

		// TODO optimal calculation
		final Vec3 ac = a.getCenter();
		final Vec3 bc = b.getCenter();
		final Box inter = a.intersection(b.offset(velocityBA.copy().scale(pMin)));
		final Axis term = inter.minTerminator();
		final double d = term.choose(bc) - term.choose(ac);
		Direction direction = term.getDirection(d);
		final Vec3 norm = direction.createVector3D();
		if (d * term.choose(velocityBA) >= 0) {
			return null;
		}
		return new SimpleCollisionResult(pMin, norm, direction, null);

	}

	public static class SimpleCollisionResult {
		public final double distance;
		public final double depth;
		public final Vec3 normal;
		public final Direction direction;
		@Getter
		@Setter
		private IShape shape;

		public SimpleCollisionResult(double distance, Vec3 normal, Direction direction, IShape shape) {
			this.distance = distance;
			this.normal = normal;
			this.shape = shape;
			this.direction = direction;
			this.depth = 0;
		}

		public SimpleCollisionResult(double distance, Vec3 normal, Direction direction, IShape shape, double depth) {
			this.distance = distance;
			this.normal = normal;
			this.shape = shape;
			this.direction = direction;
			this.depth = depth;
		}

		public Object getAttachment() {
			return (shape == null) ? null : shape.getAttachment();
		}
	}

	public static class CollisionResult extends SimpleCollisionResult {
		public final Vec3 point;

		public CollisionResult(double distance, Vec3 normal, Vec3 point, Direction direction, IShape shape) {
			super(distance, normal, direction, shape);
			this.point = point;
		}

		public CollisionResult(double distance, double depth, Vec3 normal, Vec3 point, Direction direction, IShape shape) {
			super(distance, normal, direction, shape, depth);
			this.point = point;
		}
	}


}