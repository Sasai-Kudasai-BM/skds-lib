package net.skds.lib2.shapes;

import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.DeserializeBuilder;
import net.skds.lib2.io.codec.ReflectiveBuilderCodec;
import net.skds.lib2.io.codec.ToStringSerializer;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.codec.typed.ConfigType;
import net.skds.lib2.io.codec.typed.TypedConfig;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.quat.Quat;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.utils.AutoString;

import java.lang.reflect.Type;

@DefaultCodec(OBB.JCodec.class)
public class OBB implements ConvexShape, TypedConfig {

	public final Matrix3 normals;
	public final Vec3 center;
	public final Vec3 dimensions;

	private transient Vec3[] vertexCache;
	private transient AABB boundingCache;

	@DefaultCodec(ToStringSerializer.class)
	private Object attachment;

	public OBB(Vec3 center, Vec3 dimensions, Quat q) {
		this.center = center;
		this.dimensions = dimensions;
		this.normals = Matrix3.fromQuat(q);
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
	public OBB move(Vec3 delta) {
		return new OBB(center.add(delta), this.dimensions, normals, attachment);
	}

	@Override
	public OBB rotate(Matrix3 m3) {
		return new OBB(center, this.dimensions, m3.multiply(normals), attachment);
	}

	@Override
	public OBB rotate(Quat q) {
		return new OBB(center, this.dimensions, Matrix3.fromQuat(q).multiply(normals), attachment);
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
	public OBB moveRotScale(Vec3 pos, Quat q, double scale) {
		return new OBB(center.add(pos), this.dimensions.scale(scale), Matrix3.fromQuat(q).multiply(normals), attachment);
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
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof OBB obb) {
			return equals(obb);
		} else if (obj instanceof ConvexShape convexShape) {
			return ConvexShape.equals(this, convexShape);
		} else {
			return false;
		}
	}

	boolean equals(OBB other) {
		if (this == other) return true;
		if (other == null) return false;
		if (!this.getCenter().equals(other.getCenter())) return false;
		if (!this.dimensions.equals(other.dimensions)) return false;
		return Matrix3.equals(this.normals, other.normals);
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

	static final class JCodec extends ReflectiveBuilderCodec<OBB> {

		public JCodec(Type type, CodecRegistry registry) {
			super(type, OBBBuilder.class, registry);
		}

		private static class OBBBuilder implements DeserializeBuilder<OBB> {

			public Matrix3 normals = Matrix3.SINGLE;
			public Vec3 center = Vec3.ZERO;
			public Vec3 dimensions = Vec3.SINGLE;
			public String attachment;

			@Override
			public OBB build() {
				return new OBB(this.center, this.dimensions, this.normals, this.attachment);
			}
		}
	}

	@Override
	public final ConfigType<?> getConfigType() {
		return ShapeType.OBB;
	}

	@Override
	public int hashCode() {
		return getBoundingBox().hashCode();
	}
}