package net.skds.lib2.shapes2d;

import net.skds.lib2.mat.vec2.Vec2;
import net.skds.lib2.shapes.Shape;

public interface CollisionContext2D {
	int compare(Collision2D a, Collision2D b, Vec2 velocity);

	default boolean canCollide(Shape shape) {
		return true;
	}

	CollisionContext2D DEFAULT = (a, b, v) -> a.compareTo(b);
}
