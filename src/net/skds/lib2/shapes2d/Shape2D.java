package net.skds.lib2.shapes2d;

import net.skds.lib2.mat.matrix2.Matrix2;
import net.skds.lib2.mat.vec2.Vec2;


public sealed interface Shape2D permits ConvexShape2D, CompositeShape2D {

	Shape2D move(Vec2 delta);

	Shape2D rotate(Matrix2 m3);

	Shape2D scale(double scale);

	Shape2D moveRotScale(Vec2 pos, Matrix2 m, double scale);

	Vec2 getCenter();

	AABR getBoundingRect();

	Collision2D raytrace(Vec2 from, Vec2 to, CollisionContext2D context);

	boolean intersectsRay(Vec2 from, Vec2 to);

	default boolean isConvex() {
		return false;
	}

	Collision2D collide(Shape2D shapeB, Vec2 velocityBA, CollisionContext2D context);

	Object getAttachment();

	/**
	 * Don't use it
	 *
	 * @see Shape2D#withAttachment
	 */
	void setAttachment(Object object);

	Shape2D withAttachment(Object attachment);

}
