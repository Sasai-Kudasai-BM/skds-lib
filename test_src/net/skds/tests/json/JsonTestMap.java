package net.skds.tests.json;

import java.util.HashMap;
import java.util.Map;

import lombok.ToString;
import net.skds.lib2.io.codec.SosisonUtils;
import net.skds.lib2.mat.vec3.Vec3I;

@ToString
public class JsonTestMap {

	private final Map<Vec3I, Vec3I> map = new HashMap<>();
	
	public static void test(JsonTest.JsonTestRegistry registry) {
		JsonTestMap instance = new JsonTestMap();

		instance.map.put(Vec3I.SINGLE, null);
		instance.map.put(Vec3I.ZERO, Vec3I.ZERO);
		instance.map.put(Vec3I.XP, Vec3I.XP);

		String json = SosisonUtils.toJsonCompact(instance);
		System.out.println(instance);
		System.out.println(json);
		System.out.println(SosisonUtils.parseJson(json, JsonTestMap.class));
	}
}
