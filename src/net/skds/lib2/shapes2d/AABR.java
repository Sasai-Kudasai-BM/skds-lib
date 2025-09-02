package net.skds.lib2.shapes2d;

import net.skds.lib2.io.codec.ToStringSerializer;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.mat.matrix2.Matrix2;
import net.skds.lib2.mat.vec2.Direction2D;
import net.skds.lib2.mat.vec2.Vec2;
import net.skds.lib2.mat.vec2.Vec2D;

import java.util.Collection;

@SuppressWarnings("unused")
public final class AABR implements ConvexShape2D {

	public static final AABR EMPTY = new AABR(0, 0, 0, 0);
	public static final AABR ONE = new AABR(0, 0, 1, 1);

	private static final Vec2[] normals = {Vec2.XP, Vec2.YP};

	public final double minX, minY, maxX, maxY;

	@DefaultCodec(ToStringSerializer.class)
	private Object attachment;

	private transient Vec2[] pointsCache;

	public AABR(double minX, double minY, double maxX, double maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	private AABR(double minX, double minY, double maxX, double maxY, Object attachment) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		this.attachment = attachment;
	}

	public AABR(Vec2 pos1, Vec2 pos2) {
		this(pos1.x(), pos1.y(), pos2.x(), pos2.y());
	}

	public AABR(float[] array, int offset) {
		this(array[offset], array[offset + 1], array[offset + 2], array[offset + 3]);
	}

	public AABR(double[] array, int offset) {
		this(array[offset], array[offset + 1], array[offset + 2], array[offset + 3]);
	}

	public static AABR fromTo(Vec2 from, Vec2 to) {
		return new AABR(from.x(), from.y(), to.x(), to.y());
	}

	public static AABR fromToNormalized(Vec2 from, Vec2 to) {
		double _minX = Math.min(from.x(), to.x());
		double _minY = Math.min(from.y(), to.y());
		double _maxX = Math.max(from.x(), to.x());
		double _maxY = Math.max(from.y(), to.y());
		return new AABR(_minX, _minY, _maxX, _maxY);
	}

	public static AABR fromToNormalized(double minX, double minY, double maxX, double maxY) {
		double _minX = Math.min(minX, maxX);
		double _minY = Math.min(minY, maxY);
		double _maxX = Math.max(minX, maxX);
		double _maxY = Math.max(minY, maxY);
		return new AABR(_minX, _minY, _maxX, _maxY);
	}

	public static AABR fromPos(Vec2 pos) {
		return ONE.move(pos);
	}

	public static AABR fromPos(int x, int y) {
		return ONE.move(x, y);
	}

	public static AABR fromPosSize(Vec2 pos, Vec2 size) {
		double dx = size.x();
		double dy = size.y();
		return new AABR(pos.x(), pos.y(), pos.x() + dx, pos.y() + dy);
	}

	public static AABR fromPosSize(Vec2 pos, double size) {
		return new AABR(pos.x(), pos.y(), pos.x() + size, pos.y() + size);
	}

	public static AABR fromNormal(Vec2 n) {
		return new AABR(n.x() < 0 ? n.x() : 0, n.y() < 0 ? n.y() : 0,
				n.x() > 0 ? n.x() : 0, n.y() > 0 ? n.y() : 0);
	}

	public static AABR fromNormal(double x, double y) {
		return new AABR(x < 0 ? x : 0, y < 0 ? y : 0, x > 0 ? x : 0, y > 0 ? y : 0);
	}

	public static AABR fromCenter(Vec2 center, double dx, double dy) {
		dx /= 2.0;
		dy /= 2.0;
		return new AABR(center.x() - dx, center.y() - dy, center.x() + dx, center.y() + dy);
	}

	public static AABR fromCenter(Vec2 center, double size) {
		size /= 2;
		return new AABR(center.x() - size, center.y() - size, center.x() + size, center.y() + size);
	}

	public static AABR fromCenter(Vec2 center, Vec2 size) {
		double x = size.x() / 2;
		double y = size.y() / 2;
		return new AABR(center.x() - x, center.y() - y, center.x() + x, center.y() + y);
	}

	public static AABR fromCenter(double centerX, double centerY, double dx, double dy) {
		dx /= 2.0;
		dy /= 2.0;
		return new AABR(centerX - dx, centerY - dy, centerX + dx, centerY + dy);
	}

	public static AABR fromCenter(double centerX, double centerY, double size) {
		size /= 2.0;
		return new AABR(centerX - size, centerY - size, centerX + size, centerY + size);
	}

	public static AABR fromRadius(Vec2 center, double radius) {
		return new AABR(center.x() - radius, center.y() - radius, center.x() + radius,
				center.y() + radius);
	}

	public static AABR fromSize(Vec2 size) {
		double dx = size.x() / 2;
		double dy = size.y() / 2;
		return new AABR(-dx, -dy, dx, dy);
	}

	public static AABR fromSize(double size) {
		size /= 2;
		return new AABR(-size, -size, size, size);
	}

	public static AABR fromSize(double dx, double dy) {
		dx /= 2;
		dy /= 2;
		return new AABR(-dx, -dy, dx, dy);
	}

	@Override
	public Vec2[] getNormals() {
		return normals;
	}

	@Override
	public AABR getBoundingRect() {
		return this;
	}

	@Override
	public Vec2[] getPoints() {
		Vec2[] pc = pointsCache;
		if (pc == null) {
			pc = new Vec2[]{
					new Vec2D(minX, minY),
					new Vec2D(minX, maxY),
					new Vec2D(maxX, maxY),
					new Vec2D(maxX, minY),
			};
			pointsCache = pc;
		}
		return pc;
	}

	@Override
	public double surfaceArea() {
		double x = maxX - minX;
		double y = maxY - minY;
		return x * y;
	}

	public double sizeX() {
		return maxX - minX;
	}

	public double sizeY() {
		return maxY - minY;
	}

	public Vec2 dimensions() {
		return new Vec2D(maxX - minX, maxY - minY);
	}

	public AABR withMinX(double minX) {
		return new AABR(minX, this.minY, this.maxX, this.maxY, attachment);
	}

	public AABR withMinY(double minY) {
		return new AABR(this.minX, minY, this.maxX, this.maxY, attachment);
	}

	public AABR withMaxX(double maxX) {
		return new AABR(this.minX, this.minY, maxX, this.maxY, attachment);
	}

	public AABR withMaxY(double maxY) {
		return new AABR(this.minX, this.minY, this.maxX, maxY, attachment);
	}

	public double getMaxDimension() {
		return Math.max(this.maxX - this.minX, this.maxY - this.minY);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof AABR aabr) {
			return ConvexShape2D.equals(this, aabr);
		}
		return false;
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.minX);
		int i = (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.minY);
		i = 31 * i + (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxX);
		i = 31 * i + (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxY);
		i = 31 * i + (int) (l ^ l >>> 32);
		return i;
	}

	public AABR shrink(double x, double y) {
		double d = this.minX;
		double e = this.minY;
		double g = this.maxX;
		double h = this.maxY;
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
		return new AABR(d, e, g, h, attachment);
	}

	public AABR stretch(Vec2 scale) {
		return this.stretch(scale.x(), scale.y());
	}

	public AABR stretch(double x, double y) {
		double d = this.minX;
		double e = this.minY;
		double g = this.maxX;
		double h = this.maxY;
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
		return new AABR(d, e, g, h, attachment);
	}

	public AABR expand(double x, double y) {
		double d = this.minX - x;
		double e = this.minY - y;
		double g = this.maxX + x;
		double h = this.maxY + y;
		return new AABR(d, e, g, h, attachment);
	}

	public AABR expand(double value) {
		return this.expand(value, value);
	}

	public AABR intersection(AABR box) {
		double d = Math.max(this.minX, box.minX);
		double e = Math.max(this.minY, box.minY);
		double g = Math.min(this.maxX, box.maxX);
		double h = Math.min(this.maxY, box.maxY);
		return new AABR(d, e, g, h, attachment);
	}

	public AABR union(AABR rect) {
		double d = Math.min(this.minX, rect.minX);
		double e = Math.min(this.minY, rect.minY);
		double g = Math.max(this.maxX, rect.maxX);
		double h = Math.max(this.maxY, rect.maxY);
		return new AABR(d, e, g, h, attachment);
	}

	public AABR move(double x, double y) {
		return new AABR(this.minX + x, this.minY + y, this.maxX + x, this.maxY + y, attachment);
	}

	@Override
	public AABR move(Vec2 vec) {
		return this.move(vec.x(), vec.y());
	}


	@Override
	public OBR rotate(Matrix2 m) {
		OBR obb = new OBR(getCenter(), this.dimensions(), m);
		obb.setAttachment(attachment);
		return obb;
	}

	@Override
	public AABR scale(double scale) {
		Vec2 center = getCenter();
		AABR box = fromCenter(getCenter(), sizeX() * scale, sizeY() * scale);
		box.setAttachment(attachment);
		return box;
	}

	@Override
	public OBR moveRotScale(Vec2 pos, Matrix2 m, double scale) {
		OBR obb = new OBR(getCenter().add(pos), this.dimensions().scale(scale), m);
		obb.setAttachment(attachment);
		return obb;
	}

	public boolean intersects(AABR box) {
		return this.intersects(box.minX, box.minY, box.maxX, box.maxY);
	}

	public boolean contains(AABR box) {
		return this.contains(box.minX, box.minY, box.maxX, box.maxY);
	}

	public boolean intersects(AABR box, Vec2 offset) {
		return this.minX + offset.x() < box.maxX
				&& this.maxX + offset.x() > box.minX
				&& this.minY + offset.y() < box.maxY
				&& this.maxY + offset.y() > box.minY;
	}


	public boolean intersects(double minX, double minY, double maxX, double maxY) {
		return this.minX < maxX && this.maxX > minX
				&& this.minY < maxY
				&& this.maxY > minY;
	}

	public boolean contains(double minX, double minY, double maxX, double maxY) {
		return this.minX <= minX
				&& this.maxX >= maxX
				&& this.minY <= minY
				&& this.maxY >= maxY;
	}

	public boolean intersects(Vec2 pos1, Vec2 pos2) {
		return this.intersects(Math.min(pos1.x(), pos2.x()), Math.min(pos1.y(), pos2.y()),
				Math.max(pos1.x(), pos2.x()), Math.max(pos1.y(), pos2.y()));
	}

	public boolean contains(Vec2 pos) {
		return this.contains(pos.x(), pos.y());
	}

	public boolean contains(double x, double y) {
		return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY;
	}

	public boolean fullyContains(AABR other) {
		return other.minX >= this.minX && other.maxX < this.maxX && other.minY >= this.minY && other.maxY < this.maxY;
	}

	public boolean fullyContains(double minX, double minY, double maxX, double maxY) {
		return minX >= this.minX && maxX < this.maxX && minY >= this.minY && maxY < this.maxY;
	}

	public double getAverageSideLength() {
		double d = this.sizeX();
		double e = this.sizeY();
		return (d + e) / 2.0;
	}

	public AABR contract(double x, double y) {
		return this.expand(-x, -y);
	}

	public AABR contract(double value) {
		return this.expand(-value);
	}

	@Override
	public double getProjectionMax(Direction2D.Axis axis) {
		return switch (axis) {
			case X -> maxX;
			case Y -> maxY;
		};
	}


	//public double getProjection(Direction2D.Axis axis) {
	//	return switch (axis) {
	//		case X -> maxX - minX;
	//		case Y -> maxY - minY;
	//	};
	//}

	@Override
	public double getProjectionMin(Direction2D.Axis axis) {
		return switch (axis) {
			case X -> minX;
			case Y -> minY;
		};
	}

	@Override
	public Collision2D raytrace(Vec2 from, Vec2 to, CollisionContext2D context) {
		Vec2 dir = to.sub(from);

		double tMax = Double.POSITIVE_INFINITY;
		double tMin = Double.NEGATIVE_INFINITY;
		Direction2D normal = null;
		boolean inverse = false;

		for (int i = 0; i < Direction2D.Axis.VALUES.length; i++) {
			Direction2D n = Direction2D.Axis.VALUES[i].getPositiveDirection();
			double nomLen = -n.dot(from);
			double denomLen = n.dot(dir);

			double a = (nomLen + getProjectionMax(n)) / denomLen;
			double b = (nomLen + getProjectionMin(n)) / denomLen;
			double min;
			double max;
			if (a < b) {
				min = a;
				max = b;
			} else {
				min = b;
				max = a;
			}

			if (min > tMin) {
				tMin = min;
				normal = n;
				inverse = a > b;
			}
			if (max < tMax) {
				tMax = max;
			}

			if (tMax < tMin) {
				return null;
			}

		}
		if (inverse) {
			normal = normal.getOpposite();
		}
		return new Collision2D(tMin, 0, normal, from.add(dir.normalizeScale(tMin)), normal, this, null);
	}

	@Override
	public Collision2D collide(Shape2D shapeB, Vec2 velocityBA, CollisionContext2D context) {
		if (shapeB instanceof AABR sb) {
			return ConvexShape2D.collideAABB(this, sb, velocityBA, context);
		}
		return ConvexShape2D.super.collide(shapeB, velocityBA, context);
	}

	@Override
	public Object getAttachment() {
		return attachment;
	}

	/**
	 * Don't use it
	 *
	 * @see AABR#withAttachment
	 */
	@Override
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	@Override
	public AABR withAttachment(Object attachment) {
		return new AABR(minX, minY, maxX, maxY, attachment);
	}


	@Override
	public String toString() {
		String message = "AABR[" + this.minX + ", " + this.minY + "] -> [" + this.maxX + ", " + this.maxY + "]";
		if (this.attachment != null) {
			message += "(" + this.attachment + ")";
		}
		return message;
	}

	public boolean isValid() {
		return !(Double.isNaN(this.minX)
				|| Double.isNaN(this.minY)
				|| Double.isNaN(this.maxX)
				|| Double.isNaN(this.maxY));
	}

	public boolean isNormal() {
		return this.minX <= this.maxX && this.minY <= this.maxY;
	}

	@Override
	public Vec2 getCenter() {
		return new Vec2D((this.minX + this.maxX) / 2, (this.minY + this.maxY) / 2);
	}

	//TODO use builder
	public static AABR unionOf(Collection<AABR> boxes) {
		if (boxes.isEmpty()) {
			return EMPTY;
		}
		var iter = boxes.iterator();
		AABR b = iter.next();
		while (iter.hasNext()) {
			b = b.union(iter.next());
		}
		return b;
	}

}
