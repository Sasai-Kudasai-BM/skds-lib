package net.skds.tests;

import lombok.CustomLog;
import net.skds.lib2.mat.vec3.Vec3D;
import net.skds.lib2.mat.vec3.Vec3F;
import net.skds.lib2.mat.vec3.Vec3I;
import net.skds.lib2.utils.logger.SKDSLogger;

@CustomLog
public class HashTest {

	public static void main(String[] args) {
		SKDSLogger.replaceOuts();

		test(new Vec3D(1, 1, 1), new Vec3D(1, 1, 1));
		test(new Vec3D(1, 1, 1), new Vec3F(1, 1, 1));
		test(new Vec3D(0, 0, 0), new Vec3F(0, 0, 0));
		test(new Vec3D(1, 1, 1), new Vec3I(1, 1, 1));
		test(new Vec3D(0, 0, 0), new Vec3I(0, 0, 0));

		test(1337D, 1337F);
		test(1337, (byte) 1337);
		test(1337L, (short) 1337);

	}

	static void test(Object o, Object o2) {
		log.info("%s %08X %s %08X %b".formatted(o, o.hashCode(), o2, o2.hashCode(), o.equals(o2)));
	}
}
