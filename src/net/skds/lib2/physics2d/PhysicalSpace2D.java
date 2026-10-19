package net.skds.lib2.physics2d;

import net.skds.lib2.mat.vec2.Vec2;
import net.skds.lib2.mat.vec2.Vec2F;
import net.skds.lib2.shapes2d.AABRTree;
import net.skds.lib2.utils.Tickable;

public class PhysicalSpace2D implements Tickable {
	private float dt = 0.1F;
	private Vec2 g = new Vec2F(0, -9.81F / (dt * dt));
	private final AABRTree<PhysicalBody2D> bodies = new AABRTree<>();

	@Override
	public void tick() {
	}

	public Vec2 getTickG(Vec2 point) {
		return g;
	}

	public AABRTree.TreeNode<PhysicalBody2D> addBody(PhysicalBody2D body) {
		var e = bodies.put(body.getBounding(), body);
		body.setSpace(this, e);
		return e;
	}

	public AABRTree<PhysicalBody2D> getBodies() {
		return this.bodies;
	}
}
