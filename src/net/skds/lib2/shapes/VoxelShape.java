package net.skds.lib2.shapes;

import net.skds.lib.mat.IVec3;
import net.skds.lib.mat.Matrix3;
import net.skds.lib.mat.Vec3;

public class VoxelShape implements CompositeShape {

	public static final VoxelShape EMPTY = new VoxelShape(new AABB[0]) {
		@Override
		public boolean isEmpty() {
			return true;
		}
	};

	protected AABB[] boxes;
	protected AABB bounding;

	private VoxelShape(AABB[] boxes) {
		this.boxes = boxes;
		if (boxes.length == 0) {
			this.bounding = AABB.EMPTY;
		} else {
			AABB b = boxes[0];
			for (int i = 1; i < boxes.length; i++) {
				b = b.union(boxes[i]);
			}
			this.bounding = b;
		}
	}

	public static VoxelShape of(AABB[] boxes) {
		if (boxes == null || boxes.length == 0) {
			return EMPTY;
		}
		return new VoxelShape(boxes);
	}

	public AABB[] getBoxes() {
		return boxes;
	}

	public VoxelShape copy() {
		VoxelShape shape2 = new VoxelShape(boxes.clone());
		return shape2;
	}

	public static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
		AABB box = new AABB(x1, y1, z1, x2, y2, z2);
		return cuboid(box);
	}

	public static VoxelShape cuboid(AABB box) {
		return new VoxelShape(new AABB[]{box});
	}

	public boolean isEmpty() {
		return boxes.length == 0;
	}

	public boolean intersects(IVec3 shapePos, AABB box) {
		if (isEmpty()) {
			return false;
		}
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].intersects(box, shapePos)) {
				//if (boxes[i].offset(shapePos.xf(), shapePos.yf(), shapePos.zf()).intersects(box)) {
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
		final AABB[] offBoxes = new AABB[boxes.length];
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
	public AABB getBoundingBox() {
		return bounding;
	}

	@Override
	public void setRotation(Matrix3 m3) {
	}
}
