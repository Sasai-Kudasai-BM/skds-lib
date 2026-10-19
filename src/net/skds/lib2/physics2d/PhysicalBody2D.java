package net.skds.lib2.physics2d;

import net.skds.lib2.mat.matrix2.Matrix2;
import net.skds.lib2.mat.vec2.Vec2;
import net.skds.lib2.shapes2d.AABR;
import net.skds.lib2.shapes2d.AABRTree;
import net.skds.lib2.shapes2d.Shape2D;
import net.skds.lib2.utils.Tickable;

public class PhysicalBody2D implements Tickable {
	private PhysicalSpace2D space;
	private AABRTree.TreeNode<PhysicalBody2D> entry;
	private Vec2 pos;
	private Vec2 vel = Vec2.ZERO;
	private float spin = 0;
	private Matrix2 rotation = Matrix2.SINGLE;
	private Shape2D baseShape;
	private Shape2D shape;
	private AABR bounding;

	public PhysicalBody2D(Vec2 pos, Shape2D shape) {
		this.pos = pos;
		this.baseShape = shape;
		this.shape = shape.move(pos);
		this.bounding = this.shape.getBoundingRect();
	}

	void setSpace(PhysicalSpace2D space, AABRTree.TreeNode<PhysicalBody2D> entry) {
		this.space = space;
		this.entry = entry;
	}

	public void updateShape() {
		var ns = this.baseShape.moveRotScale(this.pos, this.rotation, 1);
		var nb = ns.getBoundingRect().stretch(this.vel);
		this.bounding = nb;
		this.shape = ns;
		this.entry.move(nb);
	}

	public void setPos(Vec2 pos) {
		this.pos = pos;
		updateShape();
	}

	public void setVel(Vec2 vel) {
		this.vel = vel;
		this.bounding = this.shape.getBoundingRect().stretch(this.vel);
		var nb = this.shape.getBoundingRect().stretch(this.vel);
		this.bounding = nb;
		this.entry.move(nb);
	}

	public void addVel(Vec2 dv) {
		setVel(this.vel.add(dv));
	}

	public void setRotation(Matrix2 rotation) {
		this.rotation = rotation;
		updateShape();
	}

	public void rotate(float angle) {
		//this.rotation = Matrix2.fromRotationDegrF(angle).multiply(this.rotation);
		this.rotation = this.rotation.rotateDegrF(angle);
		updateShape();
	}

	public void setBaseShape(Shape2D baseShape) {
		this.baseShape = baseShape;
		updateShape();
	}

	@Override
	public void tick() {
		addVel(space.getTickG(pos));
	}

	public PhysicalSpace2D getSpace() {
		return this.space;
	}

	public Vec2 getPos() {
		return this.pos;
	}

	public Vec2 getVel() {
		return this.vel;
	}

	public float getSpin() {
		return this.spin;
	}

	public void setSpin(final float spin) {
		this.spin = spin;
	}

	public Matrix2 getRotation() {
		return this.rotation;
	}

	public Shape2D getBaseShape() {
		return this.baseShape;
	}

	public Shape2D getShape() {
		return this.shape;
	}

	public AABR getBounding() {
		return this.bounding;
	}
}
