package net.skds.lib2.misc.fields;

import net.skds.lib2.mat.vec3.Vec3;

public interface FloatField3D extends Field3D {

	float getValue(int x, int y, int z);

	void setValue(float value, int x, int y, int z);

	static FloatField3D ofSize(int width, int height, int depth) {
		return new FloatField3DImpl(width, height, depth);
	}

	static FloatField3D ofSize(Vec3 size) {
		return new FloatField3DImpl(size.xi(), size.yi(), size.zi());
	}
}
