package net.skds.lib2.mat;

public enum AxisDirection {
	POSITIVE(1), NEGATIVE(-1);
	private final int offset;

	public int offset() {
		return this.offset;
	}

	public AxisDirection getOpposite() {
		return this == POSITIVE ? NEGATIVE : POSITIVE;
	}

	private AxisDirection(final int offset) {
		this.offset = offset;
	}
}
