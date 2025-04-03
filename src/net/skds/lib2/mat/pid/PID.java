package net.skds.lib2.mat.pid;

import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.UnsupportedJsonCodec;

@DefaultJsonCodec(UnsupportedJsonCodec.class)
public final class PID extends AbstractPID {

	private transient double lastD;
	private transient double sumI;

	public PID(double p, double i, double d) {
		super(p, i, d);
	}

	public PID() {
	}

	@Override
	public void reset() {
		this.lastD = 0;
		this.sumI = 0;
	}

	public double loop(double in) {
		double d = (in - this.lastD) * this.d;
		this.lastD = in;
		double i = (this.sumI += in) * this.i;
		return this.p * in + d + i;
	}
}
