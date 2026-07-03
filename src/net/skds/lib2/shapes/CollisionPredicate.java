package net.skds.lib2.shapes;

@FunctionalInterface
public interface CollisionPredicate {

	/// shapeA is considered static and shapeB is moving relative to it
	boolean canCollide(Shape shapeA, Shape shapeB);
}
