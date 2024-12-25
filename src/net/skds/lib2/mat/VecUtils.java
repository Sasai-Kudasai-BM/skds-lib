package net.skds.lib2.mat;

import lombok.experimental.UtilityClass;

@SuppressWarnings("unused")
@UtilityClass
public final class VecUtils {

	public static double dist2Line(Vec3 start, Vec3 direction, Vec3 point) {
		return perpendicularPoint(start, direction, point).distanceTo(point);
	}

	public static double dist2LineLimited(Vec3 start, Vec3 direction, Vec3 point) {
		Vec3D p = perpendicularPointLimited(start, direction, point);
		if (p == null) {
			return -1;
		}
		return p.distanceTo(point);
	}

	public static Vec3D perpendicularPoint(Vec3 start, Vec3 direction, Vec3 point) {
		double dot = point.sub(start).projOn(direction);
		return start.add(direction.normalizeScale(dot));
	}

	public static Vec3D perpendicularPointLimited(Vec3 start, Vec3 direction, Vec3 point) {
		double proj = point.sub(start).projOn(direction);
		if (proj > direction.length()) {
			return null;
		}
		return start.add(direction.normalizeScale(proj));
	}

	public static Vec3D sphereContactPointLimited(Vec3 start, Vec3 direction, Vec3 center, double radius) {
		double proj = center.sub(start).projOn(direction);
		if (proj > direction.length() + radius) {
			return null;
		}
		Vec3D pp = start.add(direction.normalizeScale(proj));
		double r2 = radius * radius;
		double k2 = pp.squareDistanceTo(center);
		double delta = Math.sqrt(r2 - k2);
		if (proj > direction.length() + delta) {
			return null;
		}
		return pp.sub(direction.normalizeScale(delta));
	}


}
