package net.skds.lib.collision;

import net.skds.lib.mat.IVec3;
import net.skds.lib.mat.Vec3;

public class VoxelShape {

	public static final VoxelShape EMPTY = new VoxelShape(new Box[0]) {
		@Override
		public boolean isEmpty() {
			return true;
		}
	};

	protected Box[] boxes;

	private VoxelShape(Box[] boxes) {
		this.boxes = boxes;
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

	public VoxelShape offset(Vec3 offset) {
		Box[] list = new Box[boxes.length];
		for (int i = 0; i < list.length; i++) {
			list[i] = boxes[i].offset(offset);
		}
		VoxelShape shape2 = new VoxelShape(list);
		return shape2;
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

	public Box boundingBox() {
		if (isEmpty()) {
			return Box.EMPTY;
		}
		Box b = boxes[0];
		for (int i = 1; i < boxes.length; i++) {
			b = b.union(boxes[i]);
		}
		return b;
	}
}
