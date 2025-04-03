package net.skds.lib2.shapes2d;

import net.skds.lib2.mat.vec2.Direction2D;
import net.skds.lib2.mat.vec2.Vec2;

public record Collision2D(double distance,
						  double depth,
						  Vec2 normal,
						  Vec2 point,
						  Direction2D direction,
						  Shape2D shapeA,
						  Shape2D shapeB
) implements Comparable<Collision2D> {

	public Collision2D(double distance, double depth, Vec2 normal, Shape2D shapeA, Shape2D shapeB) {
		this(distance, depth, normal, null, null, shapeA, shapeB);
	}

	public Collision2D(double distance, double depth, Vec2 normal) {
		this(distance, depth, normal, null, null, null, null);
	}

	@Override
	public int compareTo(Collision2D o) {
		if (o == null) return -1;
		int d = Double.compare(o.depth, depth);
		if (d == 0) {
			return Double.compare(distance, o.distance);
		}
		return d;
	}
}
