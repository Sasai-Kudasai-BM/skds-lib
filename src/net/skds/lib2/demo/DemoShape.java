package net.skds.lib2.demo;

import java.util.ArrayList;
import java.util.List;

import lombok.CustomLog;
import net.skds.lib2.mat.Quat;
import net.skds.lib2.mat.Vec3;
import net.skds.lib2.shapes.AABB;
import net.skds.lib2.shapes.CompositeSuperShape;
import net.skds.lib2.shapes.CompositeSuperShape.PoseCallback;
import net.skds.lib2.shapes.CompositeSuperShape.PoseFunction;
import net.skds.lib2.shapes.Shape;
import net.skds.lib2.utils.logger.SKDSLogger;

@CustomLog
public class DemoShape {

	private static final List<String> BONES_KEYS_FOUND = new ArrayList<>();

	private static final PoseFunction FUNCTION = (Shape s, Vec3 pos, Quat rot, double scale, PoseCallback callback) -> {
		BONES_KEYS_FOUND.add(s.getAttachment().toString());
	};

	private static final Vec3 OFFSET = Vec3.of(0, 10, 0);

	public static void main(String[] args) {
		SKDSLogger.replaceOuts();
		CompositeSuperShape shape = CompositeSuperShape.of(new Shape[]{
				CompositeSuperShape.of(new Shape[]{
						AABB.fromSize(1.0).withAttachment("child/1"),
						AABB.fromSize(0.5).withAttachment("child/2"),
						AABB.fromSize(0.1).withAttachment("child/3"),
				}, Vec3.ZERO, "root"),
				AABB.fromSize(2).withAttachment("box")
		}, Vec3.ZERO, null);
		log.info("Base box: " +	shape.getBoundingBox());

		log.warn("shape1");
		print(shape, 1);
		log.warn("shape2");
		print(shape, 0.5f);

		log.warn("shape rot");
		CompositeSuperShape shape3 = shape.setPose(FUNCTION, OFFSET, Quat.fromAxisDegrees(Vec3.XP, 90), 1);
		CompositeSuperShape shape4 = shape.setPose(FUNCTION, OFFSET, Quat.fromAxisDegrees(Vec3.XP, 180), 1);
		System.out.println("base: " + shape.getBoundingBox());
		System.out.println("90: " + shape3.getBoundingBox());
		System.out.println("180: " + shape4.getBoundingBox());
	}

	private static void print(CompositeSuperShape shape, float scale) {
		BONES_KEYS_FOUND.clear();
		CompositeSuperShape shape1 = shape.setPose(FUNCTION, OFFSET, Quat.ONE, scale);
		log.debug("apply function for bones: " + BONES_KEYS_FOUND);

		AABB box = shape1.getBoundingBox();
		AABB expectedBox = shape.move(OFFSET).getBoundingBox().scale(scale);
		Vec3 size = box.dimensions();
		Vec3 expectedSize = shape.getBoundingBox().dimensions().scale(scale);

		if (box.equals(expectedBox)) {
			System.out.println("new bounding box: " + box);
		} else {
			log.error("Expected shape AABB and real AABB is not compared.\nExpected: " + expectedBox + "\nReal:     " + box);
		}
		if (size.equals(expectedSize)) {
			System.out.println("expected size is compared, " + size + " * " + scale + " == " + expectedSize);
		} else {
			log.error("Expected size and real size is not compared.\nExpected: " + expectedSize + "\nReal:     " + size);
		}
	}
}
