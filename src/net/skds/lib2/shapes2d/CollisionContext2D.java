package net.skds.lib2.shapes2d;

import net.skds.lib2.mat.vec2.Vec2;

@FunctionalInterface
public interface CollisionContext2D extends CollisionPredicate2D {
	int compare(Collision2D a, Collision2D b, Vec2 velocity);

	@Override
	default boolean canCollide(Shape2D shape) {
		return true;
	}

	CollisionContext2D DEFAULT = (a, b, _) -> a.compareTo(b);
}
