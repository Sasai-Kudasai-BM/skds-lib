package net.skds.lib.mat.pid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractPID implements Cloneable {
	protected double p;
	protected double i;
	protected double d;

	public AbstractPID() {
		p = 1;
	}

	public abstract void reset();

	@Override
	public abstract AbstractPID clone();
}
