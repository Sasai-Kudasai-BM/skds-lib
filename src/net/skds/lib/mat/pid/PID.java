package net.skds.lib.mat.pid;


public final class PID extends AbstractPID {

	private transient double lastD;
	private transient double sumI;

	public PID(double p, double i, double d) {
		super(p, i, d);
	}

	public PID() {
	}

	public double loop(double in) {
		double d = (in - this.lastD) * this.d;
		this.lastD = in;
		double i = (this.sumI += in) * this.i;
		return this.p * in + d + i;
	}

	@Override
	public PID clone() {
		return new PID(p, i, d);
	}
}
