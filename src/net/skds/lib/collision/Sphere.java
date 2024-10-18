package net.skds.lib.collision;

import lombok.Getter;
import net.skds.lib.mat.Matrix3;
import net.skds.lib.mat.Vec3;

public class Sphere implements ConvexShape {

	private static final Vec3[] empty = {};

	private final Vec3 pos = new Vec3();

	@Getter
	private double radius = .5;

	private Object attachment;
	private Box boxCache;

	public Sphere() {
	}

	public Sphere(Vec3 pos, double radius) {
		this.pos.set(pos);
		this.radius = radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
		invalidate();
	}

	private void invalidate() {
		boxCache = null;
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
	public Box getBoundingBox() {
		if (boxCache == null) {
			boxCache = Box.of(pos, radius);
		}
		return boxCache;
	}

	@Override
	public Box createBounding() {
		return Box.of(pos, radius);
	}

	@Override
	public void setRotation(Matrix3 m3) {
	}

	@Override
	public void setPos(Vec3 pos) {
		pos.set(pos);
		invalidate();
	}

	@Override
	public void scale(double scale) {
		radius *= scale;
		invalidate();
	}

	@Override
	public Vec3 getCenter() {
		return pos;
	}

	@Override
	public Sphere copy() {
		return new Sphere(pos, radius);
	}

	@Override
	public ProjPair getProjection(Vec3 axis) {
		return ConvexShape.super.getProjection(axis);//TODO
	}

	@Override
	public double getProjectionMin(Vec3 axis) {
		Vec3 tmp = axis.copy().normalize().scale(-radius);
		return tmp.add(pos).dot(axis);
	}

	@Override
	public double getProjectionMax(Vec3 axis) {
		Vec3 tmp = axis.copy().normalize().scale(radius);
		return tmp.add(pos).dot(axis);
	}

	@Override
	public double getProjectionMin(Direction.Axis axis) {
		return axis.choose(pos) - radius;
	}

	@Override
	public double getProjectionMax(Direction.Axis axis) {
		return axis.choose(pos) + radius;
	}

	@Override
	public Object getAttachment() {
		return attachment;
	}

	@Override
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	@Override
	public ConvexCollision.SimpleCollisionResult raytrace(Vec3 from, Vec3 to) {

		double dist = from.distanceTo(pos);
		double dirL = from.distanceTo(to);
		if (dist <= radius) {
			Vec3 normal = from.copy().sub(pos).normalize();
			Vec3 point = normal.copy().scale(dist).add(pos);
			return new ConvexCollision.CollisionResult(dist / dirL, normal, point, null, this);
		}
		Vec3 dir = to.copy().sub(from);

		double proj = pos.copy().sub(from).projOn(dir);
		if (proj > dirL + radius) {
			return null;
		}
		Vec3 pp = from.copy().add(dir.copy().normalize().scale(proj));
		double r2 = radius * radius;
		double k2 = pp.squareDistanceTo(pos);
		double delta = Math.sqrt(r2 - k2);
		if (proj > dirL + delta) {
			return null;
		}
		Vec3 point = pp.sub(dir.copy().normalize().scale(delta));

		return new ConvexCollision.CollisionResult(0, point.copy().sub(pos).normalize().scale(radius), point, null, this);
	}
}
