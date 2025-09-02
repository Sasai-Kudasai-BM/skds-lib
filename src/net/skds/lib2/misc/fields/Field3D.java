package net.skds.lib2.misc.fields;

public interface Field3D {

	int width();

	int height();

	int depth();

	static int index(int x, int y, int z, int width, int height) {
		int wh = width * height;
		return wh * z + width * y + x;
	}
}
