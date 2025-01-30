package net.skds.lib2.shapes;


import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec4.Quat;

public sealed interface Shape permits ConvexShape, CompositeShape {

	Shape rotate(Matrix3 m3);

	default Shape rotate(Quat q) {
		return rotate(Matrix3.fromQuat(q));
	}

	Shape move(Vec3 delta);

	Shape scale(double scale);

	Shape moveRotScale(Vec3 pos, Matrix3 m3, double scale);

	default Shape moveRotScale(Vec3 pos, Quat q, double scale) {
		return moveRotScale(pos, Matrix3.fromQuat(q), scale);
	}

	Vec3 getCenter();

	AABB getBoundingBox();

	Collision raytrace(Vec3 from, Vec3 to);

	default boolean isConvex() {
		return false;
	}

	Collision collide(Shape shapeB, Vec3 velocityBA);

	Object getAttachment();

	/**
	 * Don't use it
	 *
	 * @see Shape#withAttachment
	 */
	void setAttachment(Object object);

	Shape withAttachment(Object attachment);

}
