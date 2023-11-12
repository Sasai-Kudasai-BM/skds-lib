package net.skds.lib.collision;

import net.skds.lib.mat.Vec3;

public class ProjPair {
	public double max = 0;
	public double min = 0;

	public ProjPair(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public ProjPair() {
	}

	public static ProjPair R() {
		return new ProjPair(-Double.MAX_VALUE, Double.MAX_VALUE);
	}

	public static ProjPair IR() {
		return new ProjPair(Double.MAX_VALUE, -Double.MAX_VALUE);
	}

	@Deprecated
	public static ProjPair of(Vec3 axis, Box box) {
		double s = Vec3.staticDot(box.minX, box.minY, box.minZ, axis);
		double m = Vec3.staticDot(box.maxX, box.maxY, box.maxZ, axis);
		ProjPair pp = new ProjPair(s, m).normalize();
		s = Vec3.staticDot(box.maxX, box.minY, box.minZ, axis);
		m = Vec3.staticDot(box.maxX, box.maxY, box.maxZ, axis);
		pp.union(new ProjPair(s, m).normalize());
		s = Vec3.staticDot(box.maxX, box.maxY, box.minZ, axis);
		m = Vec3.staticDot(box.maxX, box.maxY, box.maxZ, axis);
		pp.union(new ProjPair(s, m).normalize());
		s = Vec3.staticDot(box.minX, box.maxY, box.minZ, axis);
		m = Vec3.staticDot(box.maxX, box.maxY, box.maxZ, axis);
		pp.union(new ProjPair(s, m).normalize());
		return pp;
	}

	public double distTo(ProjPair pp) {
		return -pp.copy().intersect(this).len();
	}

	public double len() {
		return max - min;
	}

	public double mid() {
		return (max + min) / 2;
	}

	public boolean isNormal() {
		return max >= min;
	}

	public boolean between(double d) {
		return isNormal() ? (d <= max && d >= min) : (d <= min && d >= max);
	}

	public boolean isFullSingle() {
		return max == 1 && min == 0;
	}

	public ProjPair normalize() {
		if (!isNormal()) {
			double d = min;
			min = max;
			max = d;
		}
		return this;
	}

	public ProjPair intersect(ProjPair proj) {
		this.min = Math.max(proj.min, this.min);
		this.max = Math.min(proj.max, this.max);
		return this;
	}

	public ProjPair intersect(double min2, double max2) {
		this.min = Math.max(min2, this.min);
		this.max = Math.min(max2, this.max);
		return this;
	}

	public ProjPair intersectNonNeg(double a1, double a2) {
		double min2 = Math.min(a1, a2);
		double max2 = Math.max(a1, a2);
		if (this.min >= this.max) {
			return this;
		}
		double s = Math.max(min2, this.min);
		double x = Math.min(max2, this.max);

		if (s > x) {
			s = mid();
			this.min = s;
			this.max = s;
			return this;
		}
		this.min = s;
		this.max = x;
		return this;
	}

	public ProjPair union(ProjPair proj) {
		this.min = Math.min(proj.min, this.min);
		this.max = Math.max(proj.max, this.max);
		return this;
	}

	public ProjPair move(double d) {
		min += d;
		max += d;
		return this;
	}

	public ProjPair copy() {
		return new ProjPair(min, max);
	}

	@Override
	public String toString() {
		return "from " + min + "  to " + max;
	}
}
