package net.skds.lib2.shapes;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.codec.typed.TypedEnumAdapter;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.quat.Quat;
import net.skds.lib2.mat.vec3.Vec3;

import java.lang.reflect.Type;

@DefaultCodec(Shape.JCodec.class)
public sealed interface Shape permits ConvexShape, CompositeShape {

	Shape move(Vec3 delta);

	Shape rotate(Matrix3 m3);

	default Shape rotate(Quat q) {
		return rotate(Matrix3.fromQuat(q));
	}

	Shape scale(double scale);

	Shape moveRotScale(Vec3 pos, Matrix3 m3, double scale);

	default Shape moveRotScale(Vec3 pos, Quat q, double scale) {
		return moveRotScale(pos, Matrix3.fromQuat(q), scale);
	}

	Vec3 getCenter();

	AABB getBoundingBox();

	Collision raytrace(Vec3 from, Vec3 to, CollisionContext context);

	boolean intersectsRay(Vec3 from, Vec3 to);

	default boolean isConvex() {
		return false;
	}

	Collision collide(Shape shapeB, Vec3 velocityBA, CollisionContext context);

	boolean intersects(Shape shapeB);

	Object getAttachment();

	/**
	 * Don't use it
	 *
	 * @see Shape#withAttachment
	 */
	void setAttachment(Object object);

	Shape withAttachment(Object attachment);

	default double raytraceExit(Vec3 from, Vec3 to, CollisionContext context) {
		Collision c = raytrace(to, from, context);
		if (c != null) return from.distanceTo(to) * (1 - c.distance());
		return -1;
	}

	final class JCodec extends TypedEnumAdapter<Shape, ShapeType> {
		public JCodec(Type type, CodecRegistry registry) {
			super(type, ShapeType.class, registry);
		}
	}
}
