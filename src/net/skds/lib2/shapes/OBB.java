package net.skds.lib2.shapes;

import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec4.Quat;
import net.skds.lib2.utils.AutoString;

public class OBB implements ConvexShape {

	public final Matrix3 normals;
	public final Vec3 center;
	public final Vec3 dimensions;

	private Vec3[] vertexCache;
	private AABB boundingCache;

	private Object attachment;

	public OBB(Vec3 center, Vec3 dimensions, Quat q) {
		this.center = center;
		this.dimensions = dimensions;
		this.normals = Matrix3.fromQuatNS(q, dimensions.x(), dimensions.y(), dimensions.z());
	}

	public OBB(Vec3 center, Vec3 dimensions, Matrix3 normals) {
		this.center = center;
		this.dimensions = dimensions;
		this.normals = normals;
	}

	private OBB(Vec3 center, Vec3 dimensions, Matrix3 normals, Object attachment) {
		this.center = center;
		this.dimensions = dimensions;
		this.normals = normals;
		this.attachment = attachment;
	}

	@Override
	public OBB rotate(Matrix3 m3) {
		return new OBB(center, this.dimensions, m3.multiply(normals), attachment);
	}

	@Override
	public OBB move(Vec3 delta) {
		return new OBB(center.add(delta), this.dimensions, normals, attachment);
	}

	@Override
	public OBB scale(double scale) {
		return new OBB(center, this.dimensions.scale(scale), normals, attachment);
	}

	@Override
	public OBB moveRotScale(Vec3 pos, Matrix3 m3, double scale) {
		return new OBB(center.add(pos), this.dimensions.scale(scale), m3.multiply(normals), attachment);
	}

	@Override
	public Object getAttachment() {
		return attachment;
	}

	/**
	 * Don't use it
	 *
	 * @see OBB#withAttachment
	 */
	@Override
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	@Override
	public OBB withAttachment(Object attachment) {
		return new OBB(center, this.dimensions, normals, attachment);
	}

	@Override
	public Vec3 getCenter() {
		return center;
	}

	@Override
	public AABB getBoundingBox() {
		AABB bb = boundingCache;
		if (bb == null) {
			bb = new AABBBuilder(center).expand(getPoints()).build();
			boundingCache = bb;
		}
		return bb;
	}

	@Override
	public Vec3[] getPoints() {
		Vec3[] vc = vertexCache;
		Vec3 l = normals.left().scale(this.dimensions.x() * 0.5);
		Vec3 u = normals.up().scale(this.dimensions.y() * 0.5);
		Vec3 f = normals.forward().scale(this.dimensions.z() * 0.5);
		Vec3 v0 = l.invSub(u);
		Vec3 v1 = u.sub(l);
		Vec3 v3 = l.sub(u);
		Vec3 v4 = l.add(u);
		if (vc == null) {
			vc = new Vec3[]{
					center.add(v0.sub(f)),
					center.add(v0.add(f)),
					center.add(v1.sub(f)),
					center.add(v1.add(f)),
					center.add(v3.sub(f)),
					center.add(v3.add(f)),
					center.add(v4.sub(f)),
					center.add(v4.add(f))
			};
			vertexCache = vc;
		}
		return vc;
	}

	@Override
	public double surfaceArea() {
		double l = this.dimensions.x();
		double u = this.dimensions.y();
		double f = this.dimensions.z();
		return 2 * (l * u + l * f + u * f);
	}

	@Override
	public Vec3[] getNormals() {
		return new Vec3[]{
				normals.left(),
				normals.up(),
				normals.forward()
		};
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\"normals\":").append(this.normals);
		builder.append(",\"dimensions\":").append(this.dimensions);
		builder.append(",\"center\":").append(this.center);
		if (this.attachment != null) {
			builder.append(",\"attachment\":").append(this.attachment);
		}
		return AutoString.build(this, builder.toString());
	}

}