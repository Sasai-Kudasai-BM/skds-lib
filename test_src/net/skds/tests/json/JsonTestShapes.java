package net.skds.tests.json;

import lombok.CustomLog;
import net.skds.lib2.demo.demo3d.Demo3dExample;
import net.skds.lib2.demo.demo3d.Demo3dShapeCollector.Demo3dShapeCollectorImpl;
import net.skds.lib2.io.codec.UniversalCodec;
import net.skds.tests.json.JsonTest.JsonTestRegistry;

@CustomLog
public class JsonTestShapes extends Demo3dShapeCollectorImpl {

	public static void test(JsonTestRegistry registry) {
		JsonTestShapes shapes = new JsonTestShapes();
		Demo3dExample.initDefault(shapes);

		//registry.addTypedAdapter(Shape.class, ShapeType.class);
		UniversalCodec<JsonTestShapes> codec = registry.getCodec(JsonTestShapes.class);

		String json = codec.toJson(shapes);
		System.out.println(json);
		JsonTestShapes read = codec.parse(json);
		assert shapes.array.equals(read.array);
	}
}
