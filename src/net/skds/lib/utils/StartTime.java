package net.skds.lib.utils;

public class StartTime {

	private final long start;

	StartTime(long start) {
		this.start = start;
	}

	public double query() {
		return (System.nanoTime() - start) / 1_000_000.0;
	}
}
