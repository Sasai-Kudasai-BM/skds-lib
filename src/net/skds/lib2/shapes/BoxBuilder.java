package net.skds.lib2.shapes;

import net.skds.lib.mat.Vec3;

public class BoxBuilder {
	private double minX;
	private double minY;
	private double minZ;
	private double maxX;
	private double maxY;
	private double maxZ;


	public BoxBuilder(IShape[] shapes) {
		this(shapes[0].getBoundingBox());
		for (int i = 1; i < shapes.length; i++) {
			expand(shapes[i].getBoundingBox());
		}
	}

	public BoxBuilder(Vec3[] points) {
		this(points[0]);
		for (int i = 1; i < points.length; i++) {
			expand(points[i]);
		}
	}

	public BoxBuilder(AABB initial) {
		this.minX = initial.minX;
		this.minY = initial.minY;
		this.minZ = initial.minZ;
		this.maxX = initial.maxX;
		this.maxY = initial.maxY;
		this.maxZ = initial.maxZ;
	}

	public BoxBuilder(Vec3 initial) {
		this.minX = initial.x;
		this.minY = initial.y;
		this.minZ = initial.z;
		this.maxX = initial.x;
		this.maxY = initial.y;
		this.maxZ = initial.z;
	}

	public AABB build() {
		return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public BoxBuilder expand(Vec3 point) {
		if (point.x > maxX) {
			maxX = point.x;
		}
		if (point.x < minX) {
			minX = point.x;
		}
		if (point.y > maxY) {
			maxY = point.y;
		}
		if (point.y < minY) {
			minY = point.y;
		}
		if (point.z > maxZ) {
			maxZ = point.z;
		}
		if (point.z < minZ) {
			minZ = point.z;
		}

		return this;
	}

	public BoxBuilder expand(AABB box) {
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
		if (box.maxZ > maxZ) {
			maxZ = box.maxZ;
		}
		if (box.minZ < minZ) {
			minZ = box.minZ;
		}

		return this;
	}
}
