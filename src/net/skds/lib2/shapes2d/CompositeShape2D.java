package net.skds.lib2.shapes2d;

import net.skds.lib2.mat.matrix2.Matrix2;
import net.skds.lib2.mat.vec2.Vec2;

import java.util.function.Consumer;

public non-sealed interface CompositeShape2D extends Shape2D {

	ConvexShape2D[] simplify(AABR bounding);

	@Override
	CompositeShape2D scale(double scale);

	@Override
	CompositeShape2D rotate(Matrix2 m);

	@Override
	CompositeShape2D move(Vec2 delta);

	@Override
	CompositeShape2D moveRotScale(Vec2 pos, Matrix2 m, double scale);

	@Override
	AABR getBoundingRect();

	Shape2D[] getAllShapes();

	default void forEachShapeRecursive(Consumer<Shape2D> consumer) {
		for (Shape2D shape : getAllShapes()) {
			consumer.accept(shape);
			if (shape instanceof CompositeShape2D compositeShape) {
				compositeShape.forEachShapeRecursive(consumer);
			}
		}
	}

	@Override
	default Collision2D raytrace(Vec2 from, Vec2 to, CollisionContext2D context) {
		if (!getBoundingRect().intersectsRay(from, to)) return null;
		ConvexShape2D[] shapes = simplify(AABR.fromToNormalized(from, to));
		if (shapes.length == 0) return null;
		Collision2D nearest = null;
		Vec2 velocity = to.sub(from);
		for (int i = 0; i < shapes.length; i++) {
			final ConvexShape2D subShape = shapes[i];
			Collision2D c = subShape.raytrace(from, to, context);
			if (c != null && context.compare(c, nearest, velocity) < 0) {
				nearest = c;
			}
		}
		return nearest;
	}

	@Override
	default boolean intersectsRay(Vec2 from, Vec2 to) {
		if (!getBoundingRect().intersectsRay(from, to)) return false;
		ConvexShape2D[] shapes = simplify(AABR.fromToNormalized(from, to));
		if (shapes.length == 0) return false;
		Vec2 velocity = to.sub(from);
		for (int i = 0; i < shapes.length; i++) {
			final ConvexShape2D subShape = shapes[i];
			if (subShape.intersectsRay(from, to)) return true;
		}
		return false;
	}

	// TODO почему при вызове a и b меняются местами и velocity инверсируется
	static Collision2D collideConvex(CompositeShape2D composite, ConvexShape2D convex, Vec2 velocityBA, CollisionContext2D context) {
		AABR convexAABB = convex.getBoundingRect().stretch(velocityBA);
		ConvexShape2D[] shapes = composite.simplify(convexAABB);
		if (shapes.length == 0) return null;
		Collision2D nearest = null;
		for (int i = 0; i < shapes.length; i++) {
			final ConvexShape2D subShape = shapes[i];
			Collision2D c = subShape.collide(convex, velocityBA, context);
			if (c != null && context.compare(c, nearest, velocityBA) < 0) {
				nearest = c;
			}
		}
		return nearest;
	}

	static Collision2D collideComposite(CompositeShape2D shapeA, CompositeShape2D shapeB, Vec2 velocityBA, CollisionContext2D context) {
		final AABR bAABB = shapeB.getBoundingRect();
		final ConvexShape2D[] shapesA = shapeA.simplify(bAABB);
		if (shapesA.length == 0) return null;
		final AABR aAABB = shapeA.getBoundingRect();
		final ConvexShape2D[] shapesB = shapeB.simplify(aAABB);
		if (shapesB.length == 0) return null;

		Collision2D nearest = null;
		for (int i = 0; i < shapesA.length; i++) {
			final ConvexShape2D subShapeA = shapesA[i];
			final AABR subShapeAAABB = subShapeA.getBoundingRect();
			for (int j = 0; j < shapesB.length; j++) {
				final ConvexShape2D subShapeB = shapesB[i];
				if (subShapeAAABB.intersects(subShapeB.getBoundingRect())) {
					final Collision2D c = subShapeA.collide(subShapeB, velocityBA, context);
					if (c != null && context.compare(c, nearest, velocityBA) < 0) {
						nearest = c;
					}
				}
			}
		}
		return nearest;
	}

	@Override
	default Collision2D collide(Shape2D shapeB, Vec2 velocityBA, CollisionContext2D context) {
		if (shapeB instanceof ConvexShape2D convex) {
			return collideConvex(this, convex, velocityBA, context);
		} else if (shapeB instanceof CompositeShape2D composite) {
			return collideComposite(this, composite, velocityBA.inverse(), context);
		}
		throw new UnsupportedOperationException("Unable to collide \"%s\" with \"%s\"".formatted(this, shapeB));

	}

	default AABR createBounding() {
		return new AABRBuilder(simplify(null)).build();
	}
}
