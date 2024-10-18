package net.skds.lib.collision;

import net.skds.lib.mat.Matrix3;
import net.skds.lib.mat.Vec3;

public interface IShape {

	Box getBoundingBox();

	void setRotation(Matrix3 m3);

	void setPos(Vec3 pos);

	void scale(double scale);

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
