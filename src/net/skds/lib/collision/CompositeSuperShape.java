package net.skds.lib.collision;

import net.skds.lib.mat.Matrix3;
import net.skds.lib.mat.Vec3;
import net.skds.lib.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class CompositeSuperShape implements CompositeShape {

	protected Box boundingBox;
	protected final Matrix3 rotation;
	protected final Vec3 pos;
	protected final Matrix3[] rotations;
	protected final Vec3[] offsets;
	protected final Vec3[] pivots;
	public final IShape[] shapes;

	protected final ConvexShape[] simplex;

	public CompositeSuperShape(Vec3 pos, IShape[] shapes, Vec3[] offsets, Vec3[] pivots, Matrix3[] rotations) {
		ArrayList<ConvexShape> list = new ArrayList<>();
		this.rotation = new Matrix3();
		this.pos = pos.copy();
		this.shapes = new IShape[shapes.length];
		this.rotations = new Matrix3[shapes.length];
		this.offsets = new Vec3[shapes.length];
		this.pivots = new Vec3[shapes.length];
		for (int i = 0; i < shapes.length; i++) {
			IShape shape = shapes[i].copy();
			this.shapes[i] = shape;
			if (shape instanceof CompositeShape composite) {
				list.addAll(Arrays.asList(composite.simplify()));
			} else {
				list.add((ConvexShape) shape);
			}
			this.rotations[i] = rotations[i].copy();
			this.offsets[i] = offsets[i].copy();
			this.pivots[i] = pivots[i].copy();
		}
		this.simplex = ArrayUtils.toArray(list, ConvexShape.class);
	}

	public CompositeSuperShape(CompositeSuperShape other) {
		this(other.pos, other.shapes, other.offsets, other.pivots, other.rotations);
		this.boundingBox = other.boundingBox;
		this.rotation.set(other.rotation);
	}

	@Override
	public CompositeSuperShape copy() {
		return new CompositeSuperShape(this);
	}

	public void update() {
		boundingBox = null;
		Vec3 tmpVo = new Vec3();
		Vec3 tmpVp = new Vec3();
		Matrix3 tmpM = new Matrix3();
		for (int i = 0; i < shapes.length; i++) {
			tmpM.set(rotation).mul(rotations[i]);
			tmpVp.set(pivots[i]).transform(rotation);
			tmpVo.set(offsets[i]).transform(tmpM).add(tmpVp).add(pos);

			shapes[i].setPosAndRotation(tmpVo, tmpM);
		}
	}

	@Override
	public ConvexShape[] simplify() {
		return simplex;
	}

	@Override
	public void setPos(Vec3 pos) {
		this.pos.set(pos);
		update();
	}

	@Override
	public void scale(double scale) {
		for (int i = 0; i < shapes.length; i++) {
			offsets[i].scale(scale);
			pivots[i].scale(scale);

			shapes[i].scale(scale);
		}
		update();
	}

	@Override
	public Vec3 getCenter() {
		return pos;
	}

	@Override
	public void setRotation(Matrix3 m3) {
		this.rotation.set(m3);
		update();
	}

	@Override
	public void setPosAndRotation(Vec3 pos, Matrix3 m3) {
		this.pos.set(pos);
		this.rotation.set(m3);
		update();
	}

	@Override
	public Box getBoundingBox() {
		if (boundingBox == null) {
			boundingBox = createBounding();
		}
		return boundingBox;
	}
}
