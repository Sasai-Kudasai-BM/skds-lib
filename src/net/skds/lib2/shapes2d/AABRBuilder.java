package net.skds.lib2.shapes2d;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.skds.lib2.mat.vec2.Vec2;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AABRBuilder {
	private double minX;
	private double minY;
	private double maxX;
	private double maxY;

	public AABRBuilder(Shape2D[] shapes) {
		this(shapes[0].getBoundingRect());
		for (int i = 1; i < shapes.length; i++) {
			expand(shapes[i].getBoundingRect());
		}
	}

	public AABRBuilder(Vec2[] points) {
		this(points[0]);
		for (int i = 1; i < points.length; i++) {
			expand(points[i]);
		}
	}

	public AABRBuilder(AABR initial) {
		this.minX = initial.minX;
		this.minY = initial.minY;
		this.maxX = initial.maxX;
		this.maxY = initial.maxY;
	}

	public AABRBuilder(Vec2 initial) {
		this.minX = initial.x();
		this.minY = initial.y();
		this.maxX = initial.x();
		this.maxY = initial.y();
	}

	public AABR build() {
		return new AABR(minX, minY, maxX, maxY);
	}

	public AABRBuilder expand(Vec2 point) {
		if (point.x() > maxX) {
			maxX = point.x();
		}
		if (point.x() < minX) {
			minX = point.x();
		}
		if (point.y() > maxY) {
			maxY = point.y();
		}
		if (point.y() < minY) {
			minY = point.y();
		}

		return this;
	}

	public AABRBuilder expand(Vec2... points) {
		for (int i = 0; i < points.length; i++) {
			Vec2 point = points[i];

			if (point.x() > maxX) {
				maxX = point.x();
			}
			if (point.x() < minX) {
				minX = point.x();
			}
			if (point.y() > maxY) {
				maxY = point.y();
			}
			if (point.y() < minY) {
				minY = point.y();
			}
		}
		return this;
	}

	public AABRBuilder expand(AABR box) {
		if (box.maxX > maxX) {
			maxX = box.maxX;
		}
		if (box.minX < minX) {
			minX = box.minX;
		}
		if (box.maxY > maxY) {
			maxY = box.maxY;
		}
		if (box.minY < minY) {
			minY = box.minY;
		}

		return this;
	}
}
