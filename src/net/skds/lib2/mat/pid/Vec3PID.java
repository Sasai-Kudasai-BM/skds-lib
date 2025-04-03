package net.skds.lib2.mat.pid;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.UnsupportedJsonCodec;
import net.skds.lib2.mat.vec3.Vec3;

@DefaultJsonCodec(UnsupportedJsonCodec.class)
public final class Vec3PID extends AbstractPID {

	private transient Vec3 lastD = Vec3.ZERO;
	private transient Vec3 sumI = Vec3.ZERO;

	public Vec3PID(double p, double i, double d) {
		super(p, i, d);
	}

	public Vec3PID() {
	}

	@Override
	public void reset() {
		this.lastD = Vec3.ZERO;
		this.sumI = Vec3.ZERO;
	}

	public Vec3 loop(Vec3 in) {
		Vec3 d = in.sub(this.lastD).scale(this.d);
		this.lastD = in;
		this.sumI = this.sumI.add(in);
		Vec3 i = this.sumI.scale(this.i);
		return in.scale(p).add(d).add(i);
	}
}
