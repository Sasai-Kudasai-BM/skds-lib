package net.skds.lib.physics;

import net.skds.lib.collision.IShape;
import net.skds.lib.mat.Quat;
import net.skds.lib.mat.Vec3;

public class StaticBody extends PhysicalBody {

	public StaticBody(PhysicalSpace space, Vec3 pos, Quat rot, IShape shape) {
		super(space, pos, rot, shape);
		this.mass = Double.POSITIVE_INFINITY;
	}

	@Override
	public void tick(double duration) {
	}

	@Override
	public void applyImpulse(Vec3 imp, Vec3 point) {
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	protected void afterMove() {
	}


	@Override
	protected void move(double dd) {
	}

	@Override
	protected void move(Vec3 move) {
	}
}
