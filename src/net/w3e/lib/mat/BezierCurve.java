package net.w3e.lib.mat;

import lombok.experimental.UtilityClass;
import net.skds.lib2.mat.Vec2;
import net.skds.lib2.mat.Vec2D;
import net.skds.lib2.mat.Vec3;
import net.skds.lib2.mat.Vec3D;

//https://ru.wikipedia.org/wiki/Кривая_Безье
@UtilityClass
public class BezierCurve {

	/**
	 * (1-t)^2*P0 + 2t(1 - t)P1 + t^2*P2
	 */
	public static Vec3D curve1(float t, Vec3 start, Vec3 point1, Vec3 end) {
		float a = 1f - t;
		return 
			start.scale(a * a)
			.add(point1.scale(2 * t * a))
			.add(end.scale(t * t))
		;
	}

	/**
	 * (1-t)^3*P0 + 3t(1 - t)^2*P1 + 3t^2*(1 - t)*P2 + t^3*P3
	 */
	public static Vec3D curve2(float t, Vec3 start, Vec3 point1, Vec3 point2, Vec3 end) {
		float a = 1f - t;
		return 
			start.scale(a * a * a)
			.add(point1.scale(3 *  t * a * a))
			.add(point2.scale(3 * t * t * a))
			.add(end.scale(t * t * t))
		;
	}

	
	/**
	 * (1-t)^2*P0 + 2t(1 - t)P1 + t^2*P2
	 */
	public static Vec2D curve1(float t, Vec2 start, Vec2 point1, Vec2 end) {
		float a = 1f - t;
		return 
			start.scale(a * a)
			.add(point1.scale(2 * t * a))
			.add(end.scale(t * t))
		;
	}

	/**
	 * (1-t)^3*P0 + 3t(1 - t)^2*P1 + 3t^2*(1 - t)*P2 + t^3*P3
	 */
	public static Vec2D curve2(float t, Vec2 start, Vec2 point1, Vec2 point2, Vec2 end) {
		float a = 1f - t;
		return 
			start.scale(a * a * a)
			.add(point1.scale(3 *  t * a * a))
			.add(point2.scale(3 * t * t * a))
			.add(end.scale(t * t * t))
		;
	}

	/**
	 * (1-t)^2*P0 + 2t(1 - t)P1 + t^2*P2
	 */
	public static float curve1(float t, float start, float point1, float end) {
		float a = 1f - t;
		return start * a * a + point1 * 2 * t * a + end * t * t;
	}

	/**
	 * (1-t)^3*P0 + 3t(1 - t)^2*P1 + 3t^2*(1 - t)*P2 + t^3*P3
	 */
	public static float curve2(float t, float start, float point1, float point2, float end) {
		float a = 1f - t;
		return start * (a * a * a)
				+ (point1 * (3 * t * a * a))
				+ (point2 * (3 * t * t * a))
				+ (end * (t * t * t));
	}
}
