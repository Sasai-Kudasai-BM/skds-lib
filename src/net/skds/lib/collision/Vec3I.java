package net.skds.lib.collision;

import net.skds.lib.mat.FastMath;
import net.skds.lib.mat.IVec3;
import net.skds.lib.mat.Vec3;

public class Vec3I implements IVec3 {

	public static final Vec3I ZERO = new Vec3I(0, 0, 0);

	public final int x, y, z;

	public Vec3I(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3I(Vec3 vec) {
		this.x = (int) vec.x;
		this.y = (int) vec.y;
		this.z = (int) vec.z;
	}

	public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
	}

	public static Vec3I ofFloored(double x, double y, double z) {
		return new Vec3I(FastMath.floor(x), FastMath.floor(y), FastMath.floor(z));
	}

	public Vec3I add(int i, int j, int k) {
		if (i == 0 && j == 0 && k == 0) {
			return this;
		}
		return new Vec3I(this.x + i, this.y + j, this.z + k);
	}

	public Vec3I add(Vec3I BlockPos) {
		return this.add(BlockPos.x, BlockPos.y, BlockPos.z);
	}

	public Vec3I subtract(Vec3I BlockPos) {
		return this.add(-BlockPos.x, -BlockPos.y, -BlockPos.z);
	}

	public Vec3I multiply(int i) {
		if (i == 1) {
			return this;
		}
		if (i == 0) {
			return ZERO;
		}
		return new Vec3I(this.x * i, this.y * i, this.z * i);
	}

	public Vec3I multiply(Vec3I vec) {
		if (this.isZero() || vec.isZero()) {
			return ZERO;
		}
		return new Vec3I(this.x * vec.x, this.y * vec.y, this.z * vec.z);
	}

	public Vec3I crossProduct(Vec3I pos) {
		return new Vec3I(this.y * pos.z - this.z * pos.y,
				this.z * pos.x - this.x * pos.z,
				this.x * pos.y - this.y * pos.x);
	}

	public Vec3I withY(int y) {
		return new Vec3I(this.x, y, this.z);
	}

	@Override
	public int hashCode() {
		return ((y << 7 ^ z) << 7) ^ x;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof Vec3I vec3I) {
			return x == vec3I.x && y == vec3I.y && z == vec3I.z;
		}
		return false;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}

	@Override
	public double x() {
		return x;
	}

	@Override
	public double y() {
		return y;
	}

	@Override
	public double z() {
		return z;
	}
}
