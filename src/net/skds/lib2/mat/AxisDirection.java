package net.skds.lib2.mat;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AxisDirection {
	POSITIVE(1),
	NEGATIVE(-1);

	private final int offset;

	public int offset() {
		return this.offset;
	}

	public AxisDirection getOpposite() {
		return this == POSITIVE ? NEGATIVE : POSITIVE;
	}
}
