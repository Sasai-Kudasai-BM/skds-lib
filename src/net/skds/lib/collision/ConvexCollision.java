package net.skds.lib.collision;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib.collision.Direction.Axis;
import net.skds.lib.mat.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ConvexCollision {

	public static final Vec3[] NORMALS = {Vec3.XP, Vec3.YP, Vec3.ZP};

	public static CollisionResult collide(ConvexShape s1, ConvexShape s2, Vec3 velocity21) {

		Vec3[] s1Norm = s1.getNormals();
		Vec3[] s2Norm = s2.getNormals();

		List<Vec3> terminators = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			terminators.add(s1Norm[i]);
			terminators.add(s2Norm[i]);
			for (int j = 0; j < 3; j++) {
				Vec3 cross = s1Norm[i].copy().cross(s2Norm[i]);
				double len = cross.length();
				if (len > 1E-10) {
					terminators.add(cross);
				}
				cross.div(len);
			}
		}
		return intersectionMoving(s1, s2, terminators, velocity21);
	}

	//TODO correct normals
	private static CollisionResult intersectionMoving(ConvexShape a, ConvexShape b, List<Vec3> terminators, Vec3 velocityBA) {

		ProjPair projInterval = new ProjPair(0, 1);
		Vec3 minTeminator = null;
		double minL = Double.MAX_VALUE;

		//boolean velN = false;
		boolean revNorm = false;

		boolean iS = true;

		boolean intersect = true;

		for (int i = 0; i < terminators.size(); i++) {
			Vec3 terminator = terminators.get(i);
			double v = velocityBA.projOnNormal(terminator);
			ProjPair projA = a.getProjection(terminator);
			ProjPair projB = b.getProjection(terminator);
			ProjPair interA = projA.copy();
			if (Math.abs(v) < 1E-30) {
				interA.intersect(projB);
				if (!interA.isNormal()) {
					intersect = false;
					break;
				}
				continue;
			}
			double zero;
			double inner;
			double outer;
			if (v > 0) {
				zero = projB.min;
				outer = interA.max;
				inner = interA.min - projB.len();
			} else {
				zero = projB.max;
				outer = interA.min;
				inner = interA.max + projB.len();
			}
			inner -= zero;
			outer -= zero;
			ProjPair inter = new ProjPair(inner / v, outer / v);
			projInterval.intersect(inter);
			if (!projInterval.isNormal()) {
				intersect = false;
				break;
			}

			interA.intersect(projB);
			double d = interA.len();
			if (d < 0) {
				iS = false;
			}
			d = Math.abs(d);
			//log.info("====  " + d + "  " + terminator);
			if (d < minL) {
				minL = d;
				minTeminator = terminator;

				revNorm = projA.mid() < projB.mid();
				//velN = v < 0;
			}
		}

		intersect &= projInterval.isNormal();

		if (intersect && minTeminator != null) {
			double dep = projInterval.min;
			Vec3 point = Vec3.ZERO();
			Vec3 normal = minTeminator.copy();
			//log.info(revNorm ^ velN);
			if (revNorm) {
				normal.inverse();
			}

			return new CollisionResult(dep, normal, Vec3.ZERO(), null, null);
		}
		return null;
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
		public final double depth;
		public final double insert;
		public final Vec3 normal;
		public final Direction direction;
		@Getter
		@Setter
		private IShape shape;

		public SimpleCollisionResult(double depth, Vec3 normal, Direction direction, IShape shape) {
			this.depth = depth;
			this.normal = normal;
			this.shape = shape;
			this.direction = direction;
			this.insert = 0;
		}

		public SimpleCollisionResult(double depth, Vec3 normal, Direction direction, IShape shape, double insert) {
			this.depth = depth;
			this.normal = normal;
			this.shape = shape;
			this.direction = direction;
			this.insert = insert;
		}
	}

	public static class CollisionResult extends SimpleCollisionResult {
		public final Vec3 point;

		public CollisionResult(double distance, Vec3 normal, Vec3 point, Direction direction, IShape shape) {
			super(distance, normal, direction, shape);
			this.point = point;
		}
	}


}