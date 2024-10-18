package net.skds.lib2.shapes;


import net.skds.lib2.mat.Matrix3;
import net.skds.lib2.mat.Vec3;

public interface IShape {

	AABB getBoundingBox();

	IShape setRotation(Matrix3 m3);

	IShape setPos(Vec3 pos);

	IShape move(Vec3 delta);

	IShape scale(double scale);

	Vec3 getCenter();

	default void setPosAndRotation(Vec3 pos, Matrix3 m3) {
		setRotation(m3);
		setPos(pos);
	}

	ConvexCollision.SimpleCollisionResult raytrace(Vec3 from, Vec3 to);

	IShape copy();

	default Object getAttachment() {
		return null;
	}

	default void setAttachment(Object attachment) {
	}

	default boolean isConvex() {
		return false;
	}

	default String getName() {
		return "Unnamed";
	}

	ConvexCollision.CollisionResult collide(IShape shape, Vec3 relativeVelocity);
}
