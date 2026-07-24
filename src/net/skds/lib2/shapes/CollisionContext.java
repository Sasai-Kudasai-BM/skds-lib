package net.skds.lib2.shapes;

import net.skds.lib2.mat.vec3.Vec3;

@FunctionalInterface
public interface CollisionContext extends CollisionPredicate {
	int compare(Collision a, Collision b, Vec3 velocity);

	@Override
	default boolean canCollide(Shape shapeA, Shape shapeB) {
		return true;
	}

	default boolean filterCollision(Collision collision, Vec3 velocity) {
		return collision != null;
	}

	default boolean filterCollision(Collision collision, Vec3 from, Vec3 to) {
		return collision != null;
	}

	CollisionContext DEFAULT = (a, b, _) -> a.compareTo(b);
}
