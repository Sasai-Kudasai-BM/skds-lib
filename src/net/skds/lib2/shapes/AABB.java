package net.skds.lib2.shapes;


import net.skds.lib2.mat.Matrix3;
import net.skds.lib2.mat.Vec3;
import net.skds.lib2.utils.Holders.DoubleHolder;

import java.util.Collection;

@SuppressWarnings("unused")
public record AABB(double minX, double minY, double minZ, double maxX, double maxY,
				   double maxZ) implements ConvexShape {

	public static final AABB EMPTY = new AABB(0, 0, 0, 0, 0, 0);
	public static final AABB ONE = new AABB(0, 0, 0, 1, 1, 1);

	private static final Vec3[] normals = {Vec3.XP, Vec3.YP, Vec3.ZP};

	private Vec3[] pointsCache;


	public AABB(Vec3 pos1, Vec3 pos2) {
		this(pos1.x(), pos1.y(), pos1.z(), pos2.x(), pos2.y(), pos2.z());
	}

	public AABB(float[] array, int offset) {
		this(array[offset], array[offset + 1], array[offset + 2], array[offset + 3], array[offset + 4],
				array[offset + 5]);
	}

	public AABB(double[] array, int offset) {
		this(array[offset], array[offset + 1], array[offset + 2], array[offset + 3], array[offset + 4],
				array[offset + 5]);
	}

	public static AABB from(Vec3 pos) {
		return new AABB(pos.x, pos.y, pos.z, pos.x + 1.0, pos.y + 1.0, pos.z + 1.0);
	}

	public static AABB fromPosSize(IVec3 pos, IVec3 size) {
		double dx = size.x() / 2;
		double dy = size.y() / 2;
		double dz = size.z() / 2;
		return new AABB(pos.x() - dx, pos.y() - dy, pos.z() - dz, pos.x() + dx, pos.y() + dy, pos.z() + dz);
	}

	public static AABB fromPosSize(IVec3 pos, double size) {
		size /= 2;
		return new AABB(pos.x() - size, pos.y() - size, pos.z() - size, pos.x() + size, pos.y() + size, pos.z() + size);
	}

	public static AABB fromNormal(Vec3 n) {
		return new AABB(n.x < 0 ? n.x : 0, n.y < 0 ? n.y : 0, n.z < 0 ? n.z : 0, n.x > 0 ? n.x : 0, n.y > 0 ? n.y : 0, n.z > 0 ? n.z : 0);
	}

	public static AABB fromNormal(double x, double y, double z) {
		return new AABB(x < 0 ? x : 0, y < 0 ? y : 0, z < 0 ? z : 0, x > 0 ? x : 0, y > 0 ? y : 0, z > 0 ? z : 0);
	}

	public static AABB from(BlockPos pos) {
		return ONE.offset(pos);
	}

	public static AABB from(int x, int y, int z) {
		return new AABB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
	}

	public Axis minTerminator() {
		double sx = maxX - minX;
		double sy = maxY - minY;
		double sz = maxZ - minZ;
		if (sx < sy) {
			if (sx < sz) {
				return Axis.X;
			} else {
				return Axis.Z;
			}
		} else if (sy < sz) {
			return Axis.Y;
		} else {
			return Axis.Z;
		}
	}

	public double surfaceArea() {
		double x = maxX - minX;
		double y = maxY - minY;
		double z = maxZ - minZ;

		return 2 * (x * y + x * z + y * z);
	}

	public double sizeX() {
		return maxX - minX;
	}

	public Vec3 dimensions() {
		return new Vec3(sizeX(), sizeY(), sizeZ());
	}

	public double sizeY() {
		return maxY - minY;
	}

	public double sizeZ() {
		return maxZ - minZ;
	}

	public Vec3 minTerminator(AABB box) {

		double minX = Math.max(this.minX, box.minX);
		double minY = Math.max(this.minY, box.minY);
		double minZ = Math.max(this.minZ, box.minZ);
		double maxX = Math.min(this.maxX, box.maxX);
		double maxY = Math.min(this.maxY, box.maxY);
		double maxZ = Math.min(this.maxZ, box.maxZ);

		double sx = maxX - minX;
		double sy = maxY - minY;
		double sz = maxZ - minZ;

		double asx = Math.abs(sx);
		double asy = Math.abs(sy);
		double asz = Math.abs(sz);

		if (asx > asy) {
			if (asx > asz) {
				sy = sz = 0;
			} else {
				sy = sx = 0;
			}
		} else if (asy > asz) {
			sz = sx = 0;
		} else {
			sy = sz = 0;
		}
		return new Vec3(sx, sy, sz);
	}

	public AABB withMinX(double minX) {
		return new AABB(minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	/**
	 * Creates a box with the minimum Y provided and all other coordinates
	 * of this box.
	 */
	public AABB withMinY(double minY) {
		return new AABB(this.minX, minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	/**
	 * Creates a box with the minimum Z provided and all other coordinates
	 * of this box.
	 */
	public AABB withMinZ(double minZ) {
		return new AABB(this.minX, this.minY, minZ, this.maxX, this.maxY, this.maxZ);
	}

	/**
	 * Creates a box with the maximum X provided and all other coordinates
	 * of this box.
	 */
	public AABB withMaxX(double maxX) {
		return new AABB(this.minX, this.minY, this.minZ, maxX, this.maxY, this.maxZ);
	}

	/**
	 * Creates a box with the maximum Y provided and all other coordinates
	 * of this box.
	 */
	public AABB withMaxY(double maxY) {
		return new AABB(this.minX, this.minY, this.minZ, this.maxX, maxY, this.maxZ);
	}

	/**
	 * Creates a box with the maximum Z provided and all other coordinates
	 * of this box.
	 */
	public AABB withMaxZ(double maxZ) {
		return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, maxZ);
	}

	public double getMaxWidth() {
		return Math.max(this.maxX - this.minX, this.maxZ - this.minZ);
	}

	public double getMaxDemension() {
		return Math.max(getMaxWidth(), getHeight());
	}

	public double getHeight() {
		return this.maxY - this.minY;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AABB box)) {
			return false;
		}
		if (box.minX != this.minX) {
			return false;
		}
		if (box.minY != this.minY) {
			return false;
		}
		if (box.minZ != this.minZ) {
			return false;
		}
		if (box.maxX != this.maxX) {
			return false;
		}
		if (box.maxY != this.maxY) {
			return false;
		}
		return box.maxZ == this.maxZ;
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.minX);
		int i = (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.minY);
		i = 31 * i + (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.minZ);
		i = 31 * i + (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxX);
		i = 31 * i + (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxY);
		i = 31 * i + (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxZ);
		i = 31 * i + (int) (l ^ l >>> 32);
		return i;
	}

	public AABB shrink(double x, double y, double z) {
		double d = this.minX;
		double e = this.minY;
		double f = this.minZ;
		double g = this.maxX;
		double h = this.maxY;
		double i = this.maxZ;
		if (x < 0.0) {
			d -= x;
		} else if (x > 0.0) {
			g -= x;
		}
		if (y < 0.0) {
			e -= y;
		} else if (y > 0.0) {
			h -= y;
		}
		if (z < 0.0) {
			f -= z;
		} else if (z > 0.0) {
			i -= z;
		}
		return new AABB(d, e, f, g, h, i);
	}

	public AABB stretch(Vec3 scale) {
		return this.stretch(scale.x, scale.y, scale.z);
	}

	public AABB stretch(double x, double y, double z) {
		double d = this.minX;
		double e = this.minY;
		double f = this.minZ;
		double g = this.maxX;
		double h = this.maxY;
		double i = this.maxZ;
		if (x < 0.0) {
			d += x;
		} else if (x > 0.0) {
			g += x;
		}
		if (y < 0.0) {
			e += y;
		} else if (y > 0.0) {
			h += y;
		}
		if (z < 0.0) {
			f += z;
		} else if (z > 0.0) {
			i += z;
		}
		return new AABB(d, e, f, g, h, i);
	}

	/**
	 * @see #contract(double, double, double)
	 */
	public AABB expand(double x, double y, double z) {
		double d = this.minX - x;
		double e = this.minY - y;
		double f = this.minZ - z;
		double g = this.maxX + x;
		double h = this.maxY + y;
		double i = this.maxZ + z;
		return new AABB(d, e, f, g, h, i);
	}

	/**
	 * @see #contract(double)
	 */
	public AABB expand(double value) {
		return this.expand(value, value, value);
	}

	/**
	 * Creates the maximum box that this box and the given box contain.
	 */
	public AABB intersection(AABB box) {
		double d = Math.max(this.minX, box.minX);
		double e = Math.max(this.minY, box.minY);
		double f = Math.max(this.minZ, box.minZ);
		double g = Math.min(this.maxX, box.maxX);
		double h = Math.min(this.maxY, box.maxY);
		double i = Math.min(this.maxZ, box.maxZ);
		return new AABB(d, e, f, g, h, i);
	}

	/**
	 * Creates the minimum box that contains this box and the given box.
	 */
	public AABB union(AABB box) {
		double d = Math.min(this.minX, box.minX);
		double e = Math.min(this.minY, box.minY);
		double f = Math.min(this.minZ, box.minZ);
		double g = Math.max(this.maxX, box.maxX);
		double h = Math.max(this.maxY, box.maxY);
		double i = Math.max(this.maxZ, box.maxZ);
		return new AABB(d, e, f, g, h, i);
	}

	/**
	 * Creates a box that is translated by {@code xf}, {@code yf}, {@code zf} on
	 * each axis from this box.
	 */
	public AABB offset(double x, double y, double z) {
		return new AABB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
	}

	/**
	 * Creates a box that is translated by {@code blockPos.getX()}, {@code
	 * blockPos.getY()}, {@code blockPos.getZ()} on each axis from this box.
	 *
	 * @see #offset(double, double, double)
	 * <p>
	 * public Box offset(BlockPos blockPos) {
	 * return new Box(this.minX + (double) blockPos.getX(), this.minY + (double) blockPos.getY(),
	 * this.minZ + (double) blockPos.getZ(), this.maxX + (double) blockPos.getX(),
	 * this.maxY + (double) blockPos.getY(), this.maxZ + (double) blockPos.getZ());
	 * }
	 * <p>
	 * /**
	 * Creates a box that is translated by {@code vec.xf}, {@code vec.yf}, {@code
	 * vec.zf} on each axis from this box.
	 * @see #offset(double, double, double)
	 */
	public AABB offset(Vec3 vec) {
		return this.offset(vec.x, vec.y, vec.z);
	}

	public AABB offset(BlockPos vec) {
		return this.offset(vec.x, vec.y, vec.z);
	}

	public AABB offset(IVec3 vec) {
		return this.offset(vec.x(), vec.y(), vec.z());
	}

	/**
	 * Checks if this box intersects the given box.
	 */
	public boolean intersects(AABB box) {
		return this.intersects(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	public boolean contains(AABB box) {
		return this.contains(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	public boolean intersects(AABB box, IVec3 offset) {
		return this.minX + offset.x() < box.maxX && this.maxX + offset.x() > box.minX
				&& this.minY + offset.y() < box.maxY
				&& this.maxY + offset.y() > box.minY && this.minZ + offset.z() < box.maxZ
				&& this.maxZ + offset.z() > box.minZ;
	}

	/**
	 * Checks if this box intersects the box of the given coordinates.
	 */
	public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ
				&& this.maxZ > minZ;
	}

	public boolean contains(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return this.minX <= minX && this.maxX >= maxX && this.minY <= minY && this.maxY >= maxY && this.minZ <= minZ
				&& this.maxZ >= maxZ;
	}

	/**
	 * Checks if this box intersects the box of the given positions as
	 * corners.
	 */
	public boolean intersects(Vec3 pos1, Vec3 pos2) {
		return this.intersects(Math.min(pos1.x, pos2.x), Math.min(pos1.y, pos2.y), Math.min(pos1.z, pos2.z),
				Math.max(pos1.x, pos2.x), Math.max(pos1.y, pos2.y), Math.max(pos1.z, pos2.z));
	}

	/**
	 * Checks if the given position is in this box.
	 */
	public boolean contains(Vec3 pos) {
		return this.contains(pos.x, pos.y, pos.z);
	}

	/**
	 * Checks if the given position is in this box.
	 */
	public boolean contains(double x, double y, double z) {
		return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
	}

	public double getAverageSideLength() {
		double d = this.getXLength();
		double e = this.getYLength();
		double f = this.getZLength();
		return (d + e + f) / 3.0;
	}

	public double getXLength() {
		return this.maxX - this.minX;
	}

	public double getYLength() {
		return this.maxY - this.minY;
	}

	public double getZLength() {
		return this.maxZ - this.minZ;
	}

	/**
	 * @see #expand(double, double, double)
	 */
	public AABB contract(double x, double y, double z) {
		return this.expand(-x, -y, -z);
	}

	/**
	 * @see #expand(double)
	 */
	public AABB contract(double value) {
		return this.expand(-value);
	}

	@Override
	public double getProjectionMax(Axis axis) {
		return switch (axis) {
			case X -> maxX;
			case Y -> maxY;
			case Z -> maxZ;
		};
	}


	public double getProjection(Axis axis) {
		return switch (axis) {
			case X -> maxX - minX;
			case Y -> maxY - minY;
			case Z -> maxZ - minZ;
		};
	}

	@Override
	public double getProjectionMin(Axis axis) {
		return switch (axis) {
			case X -> minX;
			case Y -> minY;
			case Z -> minZ;
		};
	}

	@Override
	public ConvexCollision.SimpleCollisionResult raytrace(Vec3 from, Vec3 to) {
		/*
		double pMax = 1;
		double pMin = 0;
		double len = Double.MAX_VALUE;
		Axis axis = null;
		final Vec3 vel = to.copy().sub(from);
		for (int i = 0; i < Axis.VALUES.length; i++) {
			final Axis ax = Axis.VALUES[i];
			double fp = ax.choose(from);
			double v = ax.choose(vel);

			double aMin = getProjectionMin(ax);
			double aMax = getProjectionMax(ax);

			double tMin = (aMin - fp) / v;
			double tMax = (aMax - fp) / v;

			if (v < 0) {
				double d = tMin;
				tMin = tMax;
				tMax = d;
			}

			if (tMin > pMin) {
				pMin = tMin;
			}
			if (tMax < pMax) {
				pMax = tMax;
			}
			if (pMax < pMin) {
				return null;
			}
			double len2 = tMax - tMin;
			if (len2 < len) {
				axis = ax;
				len = len2;
			}
		}
		 */
		if (contains(from)) {
			return new ConvexCollision.SimpleCollisionResult(0, from.copy().sub(to).normalize(), null, this);
		}
		final DoubleHolder depth = new DoubleHolder(1);
		final Direction dir = traceCollisionSide(from, depth, null, to.x - from.x, to.y - from.y, to.z - from.z);

		if (dir == null) {
			return null;
		}

		return new ConvexCollision.SimpleCollisionResult(depth.getValue(), dir.createVector3D(), dir, this);
	}

	private Direction traceCollisionSide(Vec3 intersectingVector, DoubleHolder distance, Direction direction, double dx, double dy, double dz) {
		if (dx > 1.0E-7) {
			direction = traceCollisionSide(distance, direction, dx, dy, dz,
					minX, minY, maxY, minZ, maxZ, Direction.WEST, intersectingVector.x,
					intersectingVector.y, intersectingVector.z);
		} else if (dx < -1.0E-7) {
			direction = traceCollisionSide(distance, direction, dx, dy, dz,
					maxX, minY, maxY, minZ, maxZ, Direction.EAST, intersectingVector.x,
					intersectingVector.y, intersectingVector.z);
		}
		if (dy > 1.0E-7) {
			direction = traceCollisionSide(distance, direction, dy, dz, dx,
					minY, minZ, maxZ, minX, maxX, Direction.DOWN, intersectingVector.y,
					intersectingVector.z, intersectingVector.x);
		} else if (dy < -1.0E-7) {
			direction = traceCollisionSide(distance, direction, dy, dz, dx,
					maxY, minZ, maxZ, minX, maxX, Direction.UP, intersectingVector.y,
					intersectingVector.z, intersectingVector.x);
		}
		if (dz > 1.0E-7) {
			direction = traceCollisionSide(distance, direction, dz, dx, dy,
					minZ, minX, maxX, minY, maxY, Direction.NORTH, intersectingVector.z,
					intersectingVector.x, intersectingVector.y);
		} else if (dz < -1.0E-7) {
			direction = traceCollisionSide(distance, direction, dz, dx, dy,
					maxZ, minX, maxX, minY, maxY, Direction.SOUTH, intersectingVector.z,
					intersectingVector.x, intersectingVector.y);
		}
		return direction;
	}

	private static Direction traceCollisionSide(DoubleHolder traceDistanceResult, Direction approachDirection,
												double deltaX, double deltaY, double deltaZ, double begin, double minA, double maxA, double minB,
												double maxZ, Direction resultDirection, double startX, double startY, double startZ) {
		double d = (begin - startX) / deltaX;
		double e = startY + d * deltaY;
		double f = startZ + d * deltaZ;
		if (0.0 < d && d < traceDistanceResult.getValue() && minA - 1.0E-7 < e && e < maxA + 1.0E-7 && minB - 1.0E-7 < f
				&& f < maxZ + 1.0E-7) {
			traceDistanceResult.setValue(d);
			return resultDirection;
		}
		return approachDirection;
	}

	public String toString() {
		return "AABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY
				+ ", " + this.maxZ + "]";
	}

	/**
	 * Checks if any of the coordinates of this box is {@linkplain
	 * Double#isNaN(double) not a number}.
	 */
	public boolean isValid() {
		return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX)
				|| Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
	}

	@Override
	public Vec3 getCenter() {
		return new Vec3((this.minX + this.maxX) / 2, (this.minY + this.maxY) / 2, (this.minZ + this.maxZ) / 2);
	}

	public static AABB of(IVec3 center, double dx, double dy, double dz) {
		return new AABB(center.x() - dx / 2.0, center.y() - dy / 2.0, center.z() - dz / 2.0, center.x() + dx / 2.0,
				center.y() + dy / 2.0, center.z() + dz / 2.0);
	}

	public static AABB of(IVec3 center, double radius) {
		return new AABB(center.x() - radius, center.y() - radius, center.z() - radius, center.x() + radius,
				center.y() + radius, center.z() + radius);
	}


	public static AABB of(double centerX, double centerY, double centerZ, double dx, double dy, double dz) {
		return new AABB(centerX - dx / 2.0, centerY - dy / 2.0, centerZ - dz / 2.0, centerX + dx / 2.0,
				centerY + dy / 2.0, centerZ + dz / 2.0);
	}

	public static AABB of(IVec3 size) {
		double dx = size.x() / 2;
		double dy = size.y() / 2;
		double dz = size.z() / 2;
		return new AABB(-dx, -dy, -dz, dx, dy, dz);
	}

	public static AABB of(double size) {
		size /= 2;
		return new AABB(-size, -size, -size, size, size, size);
	}

	public static AABB of(double dx, double dy, double dz) {
		dx /= 2;
		dy /= 2;
		dz /= 2;
		return new AABB(-dx, -dy, -dz, dx, dy, dz);
	}

	public Vec3 getDimensions() {
		return new Vec3(maxX - minX, maxY - minY, maxZ - minZ);
	}

	public double getMin(Axis axis) {
		return switch (axis) {
			case X -> minX;
			case Y -> minY;
			case Z -> minZ;
		};
	}

	public double getMax(Axis axis) {
		return switch (axis) {
			case X -> maxX;
			case Y -> maxY;
			case Z -> maxZ;
		};
	}

	@Override
	public Vec3[] getNormals() {
		return normals;
	}

	@Override
	public AABB getBoundingBox() {
		return this;
	}

	@Override
	public void setRotation(Matrix3 m3) {
		throw new UnsupportedOperationException("setRotation");
	}

	@Override
	public void setPos(Vec3 pos) {
		throw new UnsupportedOperationException("setPos");
	}

	@Override
	public void scale(double scale) {
		throw new UnsupportedOperationException("scale");
	}

	@Override
	public Vec3[] getPoints() {
		if (pointsCache == null) {
			pointsCache = new Vec3[]{
					new Vec3(minX, minY, minZ),
					new Vec3(minX, minY, maxZ),
					new Vec3(minX, maxY, minZ),
					new Vec3(minX, maxY, maxZ),
					new Vec3(maxX, minY, minZ),
					new Vec3(maxX, minY, maxZ),
					new Vec3(maxX, maxY, minZ),
					new Vec3(maxX, maxY, maxZ)
			};
		}
		return pointsCache;
	}

	@Override
	public AABB copy() {
		return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public static AABB unionOf(Collection<AABB> boxes) {

		if (boxes.isEmpty()) {
			return EMPTY;
		}

		var iter = boxes.iterator();
		AABB b = iter.next();
		while (iter.hasNext()) {
			b = b.union(iter.next());
		}
		return b;
	}
}
