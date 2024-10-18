package net.skds.lib.physics;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib.collision.Box;
import net.skds.lib.collision.ConvexCollision;
import net.skds.lib.collision.ConvexShape;
import net.skds.lib.collision.tree.Base2BoundingTree;
import net.skds.lib.collision.tree.BoundingTree;
import net.skds.lib.mat.Vec3;
import net.skds.lib.utils.Holders;

import java.util.HashSet;
import java.util.Set;

public class PhysicalSpace {

	public static final Vec3 DEFAULT_G = new Vec3(0, -9.81, 0);

	@Getter
	private final BoundingTree<PhysicalBody> bodies = new Base2BoundingTree<>();
	@Getter
	private final Set<PhysicalBody> bodiesSet = new HashSet<>();

	@Setter
	private double tickDuration = 0.05;

	public Vec3 getG(Vec3 point) {
		return DEFAULT_G.copy();
	}

	public double getBaseAirDensity(Vec3 point) {
		return 1.2255;
	}

	public double getMach(Vec3 point) {
		return 330;
	}

	public double tickDuration() {
		return tickDuration;
	}

	public void tick() {
		double duration = tickDuration();

		for (PhysicalBody body : bodiesSet) {
			body.tick(duration);
		}

		//final Set<PhysicalBody> collisionCandidates = new HashSet<>(bodiesSet);
		final Set<CollisionPair> collisions = new HashSet<>(bodiesSet.size());
		double d = 0;
		int iteration = 0;
		do {
			iteration++;
			collisions.clear();
			double k = 1 - d;

			for (PhysicalBody body : bodiesSet) {
				if (body.isStatic()) continue;
				CollisionResult cr = checkCollisions(body, k * duration);
				if (cr == null) continue;
				if (cr.depth > 1E-6) {
					System.err.println("stuck " + cr.depth);
					body.unstuck(cr);
					continue;
				}
				collisions.add(new CollisionPair(body, cr));
			}
			var op = collisions.stream().min(CollisionPair::compareTo);
			if (op.isPresent()) {
				CollisionPair p = op.get();
				final PhysicalBody body = p.body;
				final CollisionResult cr = p.cr;

				double dd = cr.distance * k;
				d += dd;


				if (cr.distance > 0) {
					for (PhysicalBody body2 : bodiesSet) {
						body2.move(dd * duration);
					}
					for (PhysicalBody body2 : bodiesSet) {
						body2.afterMove();
					}
				}
				body.onCollide(cr);
			} else {
				for (PhysicalBody body2 : bodiesSet) {
					body2.move(k * duration);
				}
				for (PhysicalBody body2 : bodiesSet) {
					body2.afterMove();
				}
				break;
			}

		} while (d < 1 && iteration < 150000);
		System.out.println(iteration);
	}


	@SuppressWarnings("UnusedReturnValue")
	public boolean addBody(PhysicalBody body) {
		if (bodiesSet.add(body)) {
			body.setBounding(bodies.put(body.getMovingBoundingBox(), body));
			return true;
		}
		return false;
	}

	@SuppressWarnings("UnusedReturnValue")
	public boolean removeBody(PhysicalBody body) {
		if (bodiesSet.remove(body)) {
			bodies.remove(body.getBounding());
			body.setBounding(null);
			return true;
		}
		return false;
	}

	public CollisionResult checkCollisions(PhysicalBody body, double d) {
		Box searchBox = body.getBoundingBox().stretch(body.vel.copy().scale(d));

		Holders.ObjectHolder<ConvexCollision.CollisionResult> result = new Holders.ObjectHolder<>();
		Holders.ObjectHolder<PhysicalBody> resultBody = new Holders.ObjectHolder<>();

		bodies.forEachInBox(searchBox, e -> {
			PhysicalBody body2 = e.getValue();
			if (body2 == body) return;
			final Vec3 dv = body.vel.copy().sub(body2.vel).scale(d);
			//var cr = body2.getShape().collide(body.getShape(), body2.vel.copy().scale(d).sub(travel));
			var cr = ConvexCollision.collide((ConvexShape) body2.getShape(), (ConvexShape) body.getShape(), dv);
			if (cr == null) return;

			if (cr.depth <= 0) {
				if (dv.dot(cr.normal) > 0) return;
			}

			var v = result.getValue();
			if (v == null || cr.distance < v.distance || cr.depth > v.depth) {
				if (body.vel.aprEquals(body2.vel, 1E-6)) return;
				result.setValue(cr);
				resultBody.setValue(body2);
			}
		});
		var r = result.getValue();
		if (r != null) {
			return new CollisionResult(r, resultBody.getValue());
		}

		return null;
	}

	private record CollisionPair(PhysicalBody body, CollisionResult cr) implements Comparable<CollisionPair> {
		@Override
		public int hashCode() {
			return body.hashCode();
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) return true;
			if (object instanceof CollisionPair that) {
				return body.equals(that.body);
			}
			return false;
		}

		@Override
		public int compareTo(CollisionPair o) {
			return Double.compare(cr.distance, o.cr.distance);
		}
	}
}
