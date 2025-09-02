package net.skds.lib2.mat;

import lombok.experimental.UtilityClass;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec3.Vec3D;

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

	public static Vec3 sphereContactPointLimited(Vec3 start, Vec3 direction, Vec3 center, double radius) {
		
		if (start.distanceTo(center) <= radius) return start;
		double dirL = direction.length();
		Vec3 dir = direction.scale(1 / dirL);

		double proj = center.sub(start).dot(dir);
		if (proj < 0 || proj > dirL + radius) return null;
		Vec3 pp = start.addScale(dir, proj);
		double r2 = radius * radius;
		double k2 = pp.squareDistanceTo(center);
		if (r2 < k2) return null;
		double delta = Math.sqrt(r2 - k2);
		if (proj > dirL + delta) return null;
		return start.addScale(dir, proj - delta);
	}

}
