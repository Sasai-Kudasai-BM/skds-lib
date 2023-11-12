package net.skds.lib.collision;

import net.skds.lib.mat.Matrix3;
import net.skds.lib.mat.Vec3;

public class OBB implements ConvexShape {

	public final Matrix3 baseMatrix;
	public final Vec3 pos;
	public final Vec3 dimensions;

	private Vec3[] vertexCash = null;
	private Box boundingCash = null;

	private Object attachment = null;

	public OBB(Vec3 dimensions, Matrix3 baseMatrix) {
		this.pos = new Vec3();
		this.baseMatrix = baseMatrix.copy();
		this.dimensions = dimensions.copy();
		update();
	}

	public OBB(OBB obb) {
		this.pos = obb.pos.copy();
		this.baseMatrix = obb.baseMatrix.copy();
		this.dimensions = obb.dimensions.copy();
		this.vertexCash = obb.vertexCash;
		this.boundingCash = obb.boundingCash;
	}

	public OBB(Vec3 dimensions) {
		this(dimensions, new Matrix3());
	}

	private OBB(Vec3 pos, Vec3 dim) {
		this.pos = pos;
		this.baseMatrix = new Matrix3();
		this.dimensions = dim;
		update();
	}

	public static OBB fromTo(Vec3 from, Vec3 to) {
		return new OBB(from.copy().add(to).scale(.5), to.copy().sub(from));
	}

	@Override
	public OBB copy() {
		return new OBB(this);
	}

	@Override
	public void scale(double scale) {
		dimensions.scale(scale);
		update();
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
	public Vec3 getCenter() {
		return pos;
	}

	@Override
	public void setPos(Vec3 pos) {
		this.pos.set(pos);
		update();
	}

	@Override
	public void setRotation(Matrix3 m3) {
		this.baseMatrix.set(m3);
		update();
	}

	@Override
	public void setPosAndRotation(Vec3 pos, Matrix3 m3) {
		this.pos.set(pos);
		this.baseMatrix.set(m3);
		update();
	}

	public void update() {
		this.vertexCash = null;
		this.boundingCash = null;
	}

	@Override
	public Box getBoundingBox() {
		if (boundingCash == null) {
			boundingCash = createBounding();
		}
		return boundingCash;
	}

	@Override
	public Vec3[] getPoints() {
		if (vertexCash == null) {
			Vec3 hd = dimensions.copy().transform(baseMatrix).scale(.5);
			vertexCash = new Vec3[8];
			vertexCash[0] = pos.copy().sub(hd);
			vertexCash[1] = baseMatrix.left().scale(dimensions.x).add(vertexCash[0]);
			vertexCash[2] = baseMatrix.up().scale(dimensions.y).add(vertexCash[0]);
			vertexCash[3] = baseMatrix.forward().scale(dimensions.z).add(vertexCash[0]);
			vertexCash[4] = pos.copy().add(hd);
			vertexCash[5] = baseMatrix.left().scale(-dimensions.x).add(vertexCash[4]);
			vertexCash[6] = baseMatrix.up().scale(-dimensions.y).add(vertexCash[4]);
			vertexCash[7] = baseMatrix.forward().scale(-dimensions.z).add(vertexCash[4]);
		}

		return vertexCash;
	}

	public Vec3[] getLines() {
		return new Vec3[]{
				pos.copy().add(baseMatrix.left().scale(dimensions.x / +2))
						.add(baseMatrix.up().scale(dimensions.y / +2))
						.add(baseMatrix.forward().scale(dimensions.z / +2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / +2))
						.add(baseMatrix.up().scale(dimensions.y / +2))
						.add(baseMatrix.forward().scale(dimensions.z / -2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / +2))
						.add(baseMatrix.up().scale(dimensions.y / -2))
						.add(baseMatrix.forward().scale(dimensions.z / +2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / +2))
						.add(baseMatrix.up().scale(dimensions.y / -2))
						.add(baseMatrix.forward().scale(dimensions.z / -2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / -2))
						.add(baseMatrix.up().scale(dimensions.y / +2))
						.add(baseMatrix.forward().scale(dimensions.z / +2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / -2))
						.add(baseMatrix.up().scale(dimensions.y / +2))
						.add(baseMatrix.forward().scale(dimensions.z / -2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / -2))
						.add(baseMatrix.up().scale(dimensions.y / -2))
						.add(baseMatrix.forward().scale(dimensions.z / +2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / -2))
						.add(baseMatrix.up().scale(dimensions.y / -2))
						.add(baseMatrix.forward().scale(dimensions.z / -2)),
				//==========
				pos.copy().add(baseMatrix.left().scale(dimensions.x / +2))
						.add(baseMatrix.forward().scale(dimensions.z / +2))
						.add(baseMatrix.up().scale(dimensions.y / +2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / +2))
						.add(baseMatrix.forward().scale(dimensions.z / +2))
						.add(baseMatrix.up().scale(dimensions.y / -2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / +2))
						.add(baseMatrix.forward().scale(dimensions.z / -2))
						.add(baseMatrix.up().scale(dimensions.y / +2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / +2))
						.add(baseMatrix.forward().scale(dimensions.z / -2))
						.add(baseMatrix.up().scale(dimensions.y / -2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / -2))
						.add(baseMatrix.forward().scale(dimensions.z / +2))
						.add(baseMatrix.up().scale(dimensions.y / +2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / -2))
						.add(baseMatrix.forward().scale(dimensions.z / +2))
						.add(baseMatrix.up().scale(dimensions.y / -2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / -2))
						.add(baseMatrix.forward().scale(dimensions.z / -2))
						.add(baseMatrix.up().scale(dimensions.y / +2)),
				pos.copy().add(baseMatrix.left().scale(dimensions.x / -2))
						.add(baseMatrix.forward().scale(dimensions.z / -2))
						.add(baseMatrix.up().scale(dimensions.y / -2)),
				//==========
				pos.copy().add(baseMatrix.up().scale(dimensions.y / +2))
						.add(baseMatrix.forward().scale(dimensions.z / +2))
						.add(baseMatrix.left().scale(dimensions.x / +2)),
				pos.copy().add(baseMatrix.up().scale(dimensions.y / +2))
						.add(baseMatrix.forward().scale(dimensions.z / +2))
						.add(baseMatrix.left().scale(dimensions.x / -2)),
				pos.copy().add(baseMatrix.up().scale(dimensions.y / +2))
						.add(baseMatrix.forward().scale(dimensions.z / -2))
						.add(baseMatrix.left().scale(dimensions.x / +2)),
				pos.copy().add(baseMatrix.up().scale(dimensions.y / +2))
						.add(baseMatrix.forward().scale(dimensions.z / -2))
						.add(baseMatrix.left().scale(dimensions.x / -2)),
				pos.copy().add(baseMatrix.up().scale(dimensions.y / -2))
						.add(baseMatrix.forward().scale(dimensions.z / +2))
						.add(baseMatrix.left().scale(dimensions.x / +2)),
				pos.copy().add(baseMatrix.up().scale(dimensions.y / -2))
						.add(baseMatrix.forward().scale(dimensions.z / +2))
						.add(baseMatrix.left().scale(dimensions.x / -2)),
				pos.copy().add(baseMatrix.up().scale(dimensions.y / -2))
						.add(baseMatrix.forward().scale(dimensions.z / -2))
						.add(baseMatrix.left().scale(dimensions.x / +2)),
				pos.copy().add(baseMatrix.up().scale(dimensions.y / -2))
						.add(baseMatrix.forward().scale(dimensions.z / -2)).add(baseMatrix.left().scale(dimensions.x / -2))
		};
	}

	@Override
	public Vec3[] getNormals() {
		return baseMatrix.asNormals();
	}
}