package net.skds.lib2.shapes2d;

@FunctionalInterface
public interface CollisionPredicate2D {
	boolean canCollide(Shape2D shape);
}
