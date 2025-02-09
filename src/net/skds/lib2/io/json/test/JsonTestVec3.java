package net.skds.lib2.io.json.test;

import lombok.ToString;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.mat.vec2.Vec2;
import net.skds.lib2.mat.vec2.Vec2D;
import net.skds.lib2.mat.vec2.Vec2F;
import net.skds.lib2.mat.vec2.Vec2I;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec3.Vec3D;
import net.skds.lib2.mat.vec3.Vec3F;
import net.skds.lib2.mat.vec3.Vec3I;

@ToString
public class JsonTestVec3 {

	public static void test(JsonCodecRegistry registry) {
		String vecTest = JsonUtils.toJson(new JsonTestVec3());
		System.out.println(vecTest);
		System.out.println(JsonUtils.parseJson(vecTest, JsonTestVec3.class));
	}

	private Vec3I i3 = new Vec3I(1);
	private Vec3F f3 = new Vec3F(2);
	private Vec3D d3 = new Vec3D(3);
	private Vec3 vA3 = new Vec3D(4);
	private Vec3 vB3 = new Vec3I(5);

	private Vec2I i2 = new Vec2I(1);
	private Vec2F f2 = new Vec2F(2);
	private Vec2D d2 = new Vec2D(3);
	private Vec2 vA2 = new Vec2D(4);
	private Vec2 vB2 = new Vec2I(5);
}
