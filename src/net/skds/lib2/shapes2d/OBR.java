package net.skds.lib2.shapes2d;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonToStringSerialiser;
import net.skds.lib2.mat.matrix2.Matrix2;
import net.skds.lib2.mat.vec2.Vec2;
import net.skds.lib2.utils.AutoString;


public class OBR implements ConvexShape2D {

	public final Matrix2 normals;
	public final Vec2 center;
	public final Vec2 dimensions;

	private transient Vec2[] vertexCache;
	private transient AABR boundingCache;

	@DefaultJsonCodec(JsonToStringSerialiser.class)
	private Object attachment;


	public OBR(Vec2 center, Vec2 dimensions, Matrix2 normals) {
		this.center = center;
		this.dimensions = dimensions;
		this.normals = normals;
	}

	private OBR(Vec2 center, Vec2 dimensions, Matrix2 normals, Object attachment) {
		this.center = center;
		this.dimensions = dimensions;
		this.normals = normals;
		this.attachment = attachment;
	}

	@Override
	public OBR move(Vec2 delta) {
		return new OBR(center.add(delta), this.dimensions, normals, attachment);
	}

	@Override
	public OBR rotate(Matrix2 m) {
		return new OBR(center, this.dimensions, m.multiply(normals), attachment);
	}

	@Override
	public OBR scale(double scale) {
		return new OBR(center, this.dimensions.scale(scale), normals, attachment);
	}

	@Override
	public OBR moveRotScale(Vec2 pos, Matrix2 m, double scale) {
		return new OBR(center.add(pos), this.dimensions.scale(scale), m.multiply(normals), attachment);
	}


	@Override
	public Object getAttachment() {
		return attachment;
	}

	/**
	 * Don't use it
	 *
	 * @see OBR#withAttachment
	 */
	@Override
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	@Override
	public OBR withAttachment(Object attachment) {
		return new OBR(center, this.dimensions, normals, attachment);
	}

	@Override
	public Vec2 getCenter() {
		return center;
	}

	@Override
	public AABR getBoundingRect() {
		AABR bb = boundingCache;
		if (bb == null) {
			bb = new AABRBuilder(center).expand(getPoints()).build();
			boundingCache = bb;
		}
		return bb;
	}

	@Override
	public Vec2[] getPoints() {
		Vec2[] vc = vertexCache;
		Vec2 r = normals.right().scale(this.dimensions.x() * 0.5);
		Vec2 u = normals.up().scale(this.dimensions.y() * 0.5);
		if (vc == null) {
			vc = new Vec2[]{
					center.add(r.sub(u)),
					center.add(r.add(u)),
					center.add(r.invSub(u)),
					center.add(u.sub(r))
			};
			vertexCache = vc;
		}
		return vc;
	}

	@Override
	public double surfaceArea() {
		return this.dimensions.x() * this.dimensions.y() * normals.det();
	}

	@Override
	public Vec2[] getNormals() {
		return new Vec2[]{
				normals.right(),
				normals.up()
		};
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof OBR obb) {
			return center.equals(obb.center) && dimensions.equals(obb.dimensions) && normals.equals(obb.normals);
		}
		return false;
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