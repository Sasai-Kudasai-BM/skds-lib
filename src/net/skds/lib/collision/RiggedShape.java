package net.skds.lib.collision;

import net.skds.lib.mat.Matrix3;
import net.skds.lib.mat.Vec3;

import java.util.function.Consumer;

public class RiggedShape extends CompositeSuperShape {
	protected final String name;

	public final Matrix3 boneRot = new Matrix3();
	public final Vec3 boneOffset = new Vec3();


	public RiggedShape(Vec3 pos, IShape[] shapes, Vec3[] offsets, Vec3[] pivots, Matrix3[] rotations, String name) {
		super(pos, shapes, offsets, pivots, rotations);
		this.name = name;
	}

	public RiggedShape(RiggedShape shape) {
		super(shape);
		this.name = shape.name;
	}

	@Override
	public void setAttachment(Object attachment) {
		for (int i = 0; i < shapes.length; i++) {
			shapes[i].setAttachment(attachment);
		}
	}

	@Override
	public RiggedShape copy() {
		return new RiggedShape(this);
	}

	@Override
	public String getName() {
		return name;
	}

	public RiggedShape getBoneByName(String name) {
		for (int i = 0; i < shapes.length; i++) {
			IShape shape = shapes[i];
			if (shape instanceof RiggedShape rs) {
				if (rs.name.equals(name)) {
					return rs;
				}
				RiggedShape b = rs.getBoneByName(name);
				if (b != null) {
					return b;
				}
			}
		}
		return null;
	}

	public void forEachBone(Consumer<RiggedShape> action) {
		for (int i = 0; i < shapes.length; i++) {
			IShape shape = shapes[i];
			if (shape instanceof RiggedShape rs) {
				action.accept(rs);
				rs.forEachBone(action);
			}
		}
	}

	@Override
	public void update() {
		boundingBox = null;
		Vec3 tmpVo = new Vec3();
		Vec3 tmpVp = new Vec3();
		Matrix3 tmpM = new Matrix3();
		for (int i = 0; i < shapes.length; i++) {
			IShape shape = shapes[i];
			tmpM.set(rotation);
			tmpVp.set(pivots[i]);
			if (shape instanceof RiggedShape rs) {
				tmpM.mul(rs.boneRot);
				tmpVp.add(rs.boneOffset);
			}
			tmpM.mul(rotations[i]);
			tmpVp.transform(rotation);
			tmpVo.set(offsets[i]).transform(tmpM).add(tmpVp).add(pos);

			shape.setPosAndRotation(tmpVo, tmpM);
		}
	}
}
