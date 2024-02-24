package net.skds.lib.collision;

import net.skds.lib.mat.Matrix3;
import net.skds.lib.mat.Vec3;

@Deprecated
public class OBBShape implements CompositeShape {

	public static final OBBShape EMPTY = new OBBShape(new OBB[0]) {

		@Override
		public void updateBox() {
		}

		@Override
		public Box getBoundingBox() {
			return Box.EMPTY;
		}

		@Override
		public void setRotation(Matrix3 m3) {
		}

		@Override
		public void setPos(Vec3 pos) {
		}

		@Override
		public void scale(double scale) {
		}
	};

	private final Vec3 pos;

	private final OBB[] boxes;
	private final Matrix3[] rotations;
	private final Vec3[] offsets;

	private Box boundingBox;

	public OBBShape(OBB[] boxes) {
		this(boxes, new Vec3());
	}

	public OBBShape(OBB[] boxes, Vec3 pos) {
		this.pos = pos.copy();
		this.boxes = boxes;
		this.rotations = new Matrix3[boxes.length];
		this.offsets = new Vec3[boxes.length];
		for (int i = 0; i < boxes.length; i++) {
			OBB box = boxes[i];
			rotations[i] = box.baseMatrix.copy();
			offsets[i] = box.pos.copy().sub(pos);
		}
	}

	public OBBShape(OBBShape shape) {
		this.pos = shape.pos.copy();
		this.boxes = new OBB[shape.boxes.length];
		this.rotations = new Matrix3[boxes.length];
		this.offsets = new Vec3[boxes.length];
		this.boundingBox = shape.boundingBox;
		for (int i = 0; i < boxes.length; i++) {
			rotations[i] = shape.rotations[i].copy();
			offsets[i] = shape.offsets[i].copy();
			boxes[i] = shape.boxes[i].copy();
		}
	}

	public int boxCount() {
		return boxes.length;
	}

	@Override
	public OBBShape copy() {
		return new OBBShape(this);
	}

	public void updateBox() {
		boundingBox = null;
	}

	public boolean isEmpty() {
		return boxes.length == 0;
	}

	public void setRotationAndPos(Matrix3 m3, Vec3 pos) {
		updateBox();
	}

	@Override
	public void scale(double scale) {
		Vec3 delta = new Vec3();
		for (int i = 0; i < boxes.length; i++) {
			OBB box = boxes[i];
			offsets[i].scale(scale);
			delta.set(box.pos).sub(pos);
			delta.scale(scale).add(pos);

			box.pos.set(delta);
			box.dimensions.scale(scale);
			box.update();
		}
		updateBox();
	}

	@Override
	public Vec3 getCenter() {
		return pos;
	}

	@Override
	public void setPos(Vec3 pos) {
		Vec3 delta = new Vec3();
		for (int i = 0; i < boxes.length; i++) {
			OBB box = boxes[i];
			delta.set(pos).sub(this.pos);
			this.pos.set(pos);
			box.pos.add(delta);
			box.update();
		}
		updateBox();
	}

	@Override
	public void setRotation(Matrix3 m3) {
		Vec3 delta = new Vec3();
		for (int i = 0; i < boxes.length; i++) {
			OBB box = boxes[i];
			delta.set(offsets[i]).transform(m3).add(pos);

			box.baseMatrix.set(m3).mul(rotations[i]);
			box.pos.set(delta);
			box.update();
		}
		updateBox();
	}

	@Override
	public Box getBoundingBox() {
		if (boundingBox == null) {
			boundingBox = createBounding();
		}
		return boundingBox;
	}

	@Override
	public ConvexShape[] simplify() {
		return boxes;
	}
}
