package net.skds.lib.collision;

import net.skds.lib.mat.IVec3;
import net.skds.lib.mat.Matrix3;
import net.skds.lib.mat.Vec3;

public class VoxelShape implements CompositeShape {

	public static final VoxelShape EMPTY = new VoxelShape(new Box[0]) {
		@Override
		public boolean isEmpty() {
			return true;
		}
	};

	protected Box[] boxes;
	protected Box bounding;

	private VoxelShape(Box[] boxes) {
		this.boxes = boxes;
		if (boxes.length == 0) {
			this.bounding = Box.EMPTY;
		} else {
			Box b = boxes[0];
			for (int i = 1; i < boxes.length; i++) {
				b = b.union(boxes[i]);
			}
			this.bounding = b;
		}
	}

	public static VoxelShape of(Box[] boxes) {
		if (boxes == null || boxes.length == 0) {
			return EMPTY;
		}
		return new VoxelShape(boxes);
	}

	public Box[] getBoxes() {
		return boxes;
	}

	public VoxelShape copy() {
		VoxelShape shape2 = new VoxelShape(boxes.clone());
		return shape2;
	}

	public static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
		Box box = new Box(x1, y1, z1, x2, y2, z2);
		return cuboid(box);
	}

	public static VoxelShape cuboid(Box box) {
		return new VoxelShape(new Box[]{box});
	}

	public boolean isEmpty() {
		return boxes.length == 0;
	}

	public boolean intersects(IVec3 shapePos, Box box) {
		if (isEmpty()) {
			return false;
		}
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].intersects(box, shapePos)) {
				//if (boxes[i].offset(shapePos.x(), shapePos.y(), shapePos.z()).intersects(box)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ConvexShape[] simplify() {
		return boxes;
	}

	@Override
	public void setPos(Vec3 pos) {
	}

	public VoxelShape offset(IVec3 offset) {
		final Box[] offBoxes = new Box[boxes.length];
		for (int i = 0; i < offBoxes.length; i++) {
			offBoxes[i] = boxes[i].offset(offset);
		}
		return new VoxelShape(offBoxes);
	}

	@Override
	public void scale(double scale) {
	}

	@Override
	public Vec3 getCenter() {
		return bounding.getCenter();
	}

	@Override
	public Box getBoundingBox() {
		return bounding;
	}

	@Override
	public void setRotation(Matrix3 m3) {
	}
}
