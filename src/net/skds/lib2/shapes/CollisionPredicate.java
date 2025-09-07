package net.skds.lib2.shapes;

@FunctionalInterface
public interface CollisionPredicate {
	boolean canCollide(Shape shape);
}
