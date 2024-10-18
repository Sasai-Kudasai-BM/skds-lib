package net.skds.lib2.shapes;

import net.skds.lib.collision.ConvexCollision;
import net.skds.lib.mat.Matrix3;
import net.skds.lib.mat.Vec3;

public interface CompositeShape extends IShape {

	ConvexShape[] simplify();

	void setPos(Vec3 pos);

	void scale(double scale);

	void setRotation(Matrix3 m3);

	@Override
	default ConvexCollision.SimpleCollisionResult raytrace(Vec3 from, Vec3 to) {

		ConvexCollision.SimpleCollisionResult min = null;
		final ConvexShape[] simple = simplify();
		for (int i = 0; i < simple.length; i++) {
			ConvexShape cs = simple[i];
			ConvexCollision.SimpleCollisionResult result = cs.raytrace(from, to);
			if (result != null && (min == null || result.distance < min.distance)) {
				min = result;
			}
		}
		return min;
	}

	@Override
	default ConvexCollision.CollisionResult collide(IShape shape, Vec3 relativeVelocity) {
		AABB ext = getBoundingBox();
		if (relativeVelocity != null) {
			ext = ext.stretch(relativeVelocity);
		}
		if (!ext.intersects(shape.getBoundingBox())) {
			return null;
		}

		ConvexCollision.CollisionResult cc = null;
		final ConvexShape[] shapes = shape instanceof CompositeShape cs ? cs.simplify() : new ConvexShape[]{(ConvexShape) shape};
		for (int i = 0; i < shapes.length; i++) {
			final ConvexShape convexShape = shapes[i];
			final AABB convexBound = convexShape.getBoundingBox();
			if (!convexBound.intersects(ext)) {
				continue;
			}
			final ConvexShape[] simples = simplify();
			for (int j = 0; j < simples.length; j++) {
				final ConvexShape myBox = simples[j];
				if (myBox.getBoundingBox().intersects(convexBound)) {
					ConvexCollision.CollisionResult cc2 = ConvexCollision.collide(convexShape, myBox, relativeVelocity);
					if (cc2 != null) {
						if (cc == null) {
							cc = cc2;
							cc.setShape(myBox);
						} else if (cc2.distance < cc.distance) {
							cc = cc2;
							cc.setShape(myBox);
						}
					}
				}
			}
		}
		return cc;
	}

	@Deprecated
	default ConvexCollision.CollisionResult collideWithMoving(ConvexShape shape, Vec3 relativeVelocity) {
		AABB ext = shape.getBoundingBox();
		if (relativeVelocity != null) {
			ext = ext.stretch(relativeVelocity);
		}
		if (!ext.intersects(getBoundingBox())) {
			return null;
		}

		ConvexCollision.CollisionResult cc = null;
		final ConvexShape[] simples = simplify();
		for (int j = 0; j < simples.length; j++) {
			final ConvexShape myBox = simples[j];
			if (myBox.getBoundingBox().intersects(ext)) {
				ConvexCollision.CollisionResult cc2 = ConvexCollision.collide(myBox, shape, relativeVelocity);
				if (cc2 != null) {
					if (cc == null) {
						cc = cc2;
						cc.setShape(myBox);
					} else if (cc2.distance < cc.distance) {
						cc = cc2;
						cc.setShape(myBox);
					}
				}
			}
		}

		return cc;
	}

	default AABB createBounding() {
		return new BoxBuilder(simplify()).build();
	}
}
