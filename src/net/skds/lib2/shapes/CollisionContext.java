package net.skds.lib2.shapes;

import net.skds.lib2.mat.vec3.Vec3;

@FunctionalInterface
public interface CollisionContext extends CollisionPredicate {
	int compare(Collision a, Collision b, Vec3 velocity);

	@Override
	default boolean canCollide(Shape shape) {
		return true;
	}

	CollisionContext DEFAULT = (a, b, _) -> a.compareTo(b);
}
