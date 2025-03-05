package net.skds.lib2.shapes;

import net.skds.lib2.mat.vec3.Vec3;

public interface CollisionContext {
	int compare(Collision a, Collision b, Vec3 velocity);

	default boolean canCollide(Shape shape) {
		return true;
	}

	public static final CollisionContext DEFAULT = (a, b, v) -> {
		return a.compareTo(b);
	};
}
