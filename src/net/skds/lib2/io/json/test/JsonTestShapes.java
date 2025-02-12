package net.skds.lib2.io.json.test;

import lombok.CustomLog;
import net.skds.lib2.demo.demo3d.Demo3dFrameExample;
import net.skds.lib2.demo.demo3d.Demo3dShapeCollector.Demo3dShapeCollectorImpl;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.test.JsonTest.JsonTestRegistry;

@CustomLog
public class JsonTestShapes extends Demo3dShapeCollectorImpl {

	public static void test(JsonTestRegistry registry) {
		JsonTestShapes shapes = new JsonTestShapes();
		Demo3dFrameExample.initDefault(shapes);

		//registry.addTypedAdapter(Shape.class, ShapeType.class);
		JsonCodec<JsonTestShapes> codec = registry.getCodec(JsonTestShapes.class);

		String json = codec.toJson(shapes);
		System.out.println(json);
		JsonTestShapes read = codec.parse(json);
		assert shapes.array.equals(read.array);
	}
}
