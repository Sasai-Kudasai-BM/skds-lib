package net.skds.lib2.shapes;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.typed.TypedEnumAdapter;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.quat.Quat;
import net.skds.lib2.mat.vec3.Vec3;

import java.lang.reflect.Type;

@DefaultJsonCodec(Shape.JCodec.class)
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

	default boolean isConvex() {
		return false;
	}

	Collision collide(Shape shapeB, Vec3 velocityBA, CollisionContext context);

	Object getAttachment();

	/**
	 * Don't use it
	 *
	 * @see Shape#withAttachment
	 */
	void setAttachment(Object object);

	Shape withAttachment(Object attachment);

	static final class JCodec extends TypedEnumAdapter<Shape, ShapeType> {
		public JCodec(Type type, JsonCodecRegistry registry) {
			super(type, ShapeType.class, registry);
		}
	}
}
