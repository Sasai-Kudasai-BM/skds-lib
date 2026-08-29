package net.skds.lib2.shapes;

import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.quat.Quat;
import net.skds.lib2.mat.vec2.Vec2D;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.utils.exception.TODOException;

public class Ellipsoid implements ConvexShape {

	private static final Vec3[] empty = {};

	public final Vec3 center;
	public final Quat orientation;
	public final double radiusX;
	public final double radiusY;
	public final double radiusZ;

	private AABB boxCache;
	private Object attachment;

	public Ellipsoid(Vec3 center, double radiusX, double radiusY, double radiusZ, Quat orientation) {
		this.center = center;
		this.radiusX = radiusX;
		this.radiusY = radiusY;
		this.radiusZ = radiusZ;
		this.orientation = orientation;
	}

	public Ellipsoid(Vec3 center, double radiusX, double radiusY, double radiusZ, Quat orientation, Object attachment) {
		this.center = center;
		this.radiusX = radiusX;
		this.radiusY = radiusY;
		this.radiusZ = radiusZ;
		this.orientation = orientation;
		this.attachment = attachment;
	}

	@Override
	public Object getAttachment() {
		return attachment;
	}

	/**
	 * Don't use it
	 *
	 * @see Ellipsoid#withAttachment
	 */
	@Override
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	@Override
	public Ellipsoid withAttachment(Object attachment) {
		return new Ellipsoid(this.center, this.radiusX, this.radiusY, this.radiusZ, this.orientation, attachment);
	}

	@Override
	public Vec3[] getNormals() {
		return empty;
	}

	@Override
	public Vec3[] getPoints() {
		return empty;
	}

	@Override
	public double surfaceArea() {
		throw new TODOException();
	}

	@Override
	public AABB getBoundingBox() {
		AABB bc = boxCache;
		if (bc == null) {
			bc = AABB.fromRadius(this.center, this.radiusX, this.radiusY, this.radiusZ);
			boxCache = bc;
		}
		return bc;
	}

	@Override
	public ConvexShape move(Vec3 delta) {
		return new Ellipsoid(this.center.add(delta), this.radiusX, this.radiusY, this.radiusZ, this.orientation, this.attachment);
	}

	@Override
	public ConvexShape move(double dx, double dy, double dz) {
		return new Ellipsoid(this.center.add(dx, dy, dz), this.radiusX, this.radiusY, this.radiusZ, this.orientation, this.attachment);
	}

	@Override
	public Ellipsoid rotate(Matrix3 m3) {
		return new Ellipsoid(this.center, this.radiusX, this.radiusY, this.radiusZ, Quat.fromMatrix(m3).multiply(this.orientation), attachment);
	}

	@Override
	public Ellipsoid rotate(Quat q) {
		return new Ellipsoid(this.center, this.radiusX, this.radiusY, this.radiusZ, q.multiply(this.orientation), attachment);
	}

	@Override
	public Ellipsoid scale(double scale) {
		return new Ellipsoid(this.center, this.radiusX * scale, this.radiusY * scale, this.radiusZ * scale, this.orientation, this.attachment);
	}

	@Override
	public Ellipsoid scale(Vec3 scale) {
		return new Ellipsoid(this.center, this.radiusX * scale.x(), this.radiusY * scale.y(), this.radiusZ * scale.z(), this.orientation, this.attachment);
	}

	@Override
	public Ellipsoid scale(double scaleX, double scaleY, double scaleZ) {
		return new Ellipsoid(this.center, this.radiusX * scaleX, this.radiusY * scaleY, this.radiusZ * scaleZ, this.orientation, this.attachment);
	}

	@Override
	public Ellipsoid moveRotScale(Vec3 pos, Matrix3 m3, double scale) {
		return new Ellipsoid(this.center.add(pos), this.radiusX * scale, this.radiusY * scale, this.radiusZ * scale, Quat.fromMatrix(m3).multiply(this.orientation), this.attachment);
	}

	@Override
	public Ellipsoid moveRotScale(Vec3 pos, Quat q, double scale) {
		return new Ellipsoid(this.center.add(pos), this.radiusX * scale, this.radiusY * scale, this.radiusZ * scale, q.multiply(this.orientation), this.attachment);
	}

	@Override
	public Vec3 getCenter() {
		return this.center;
	}

	@Override
	public Vec2D getProjection(Vec3 axis) {
		throw new TODOException();
	}

	@Override
	public double getProjectionMin(Vec3 axis) {
		throw new TODOException();
	}

	@Override
	public double getProjectionMax(Direction.Axis axis) {
		throw new TODOException();
	}

	@Override
	public double getProjectionMin(Direction.Axis axis) {
		throw new TODOException();
	}

	@Override
	public double getProjectionMax(Vec3 axis) {
		throw new TODOException();
	}

	@Override
	public Collision raytrace(Vec3 from, Vec3 to, CollisionContext context) {
		throw new TODOException();
	}

	@Override
	public boolean intersectsRay(Vec3 from, Vec3 to) {
		throw new TODOException();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		return switch (obj) {
			case Ellipsoid s -> equals(s);
			case ConvexShape s -> ConvexShape.equals(this, s);
			case null, default -> false;
		};
	}

	public boolean equals(Ellipsoid other) {
		if (this == other) return true;
		if (other == null) return false;
		return this.radiusX == other.radiusX
				&& this.radiusY == other.radiusY
				&& this.radiusZ == other.radiusZ
				&& this.orientation.equals(other.orientation)
				&& this.center.equals(other.center);
	}

	@Override
	public int hashCode() {
		return getBoundingBox().hashCode();
	}
}
