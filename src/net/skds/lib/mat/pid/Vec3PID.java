package net.skds.lib.mat.pid;


import net.skds.lib.mat.Vec3;

public final class Vec3PID extends AbstractPID {

	private transient final Vec3 lastD = new Vec3();
	private transient final Vec3 sumI = new Vec3();

	public Vec3PID(double p, double i, double d) {
		super(p, i, d);
	}

	public Vec3PID() {
	}

	public Vec3 loop(Vec3 in) {
		Vec3 d = in.copy().sub(this.lastD).scale(this.d);
		this.lastD.set(in);
		Vec3 i = this.sumI.add(in).copy().scale(this.i);

		return in.copy().scale(p).add(d).add(i);
	}

	@Override
	public Vec3PID clone() {
		return new Vec3PID(p, i, d);
	}
}
