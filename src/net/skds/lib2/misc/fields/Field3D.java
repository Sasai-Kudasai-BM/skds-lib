package net.skds.lib2.misc.fields;

public interface Field3D {

	int width();

	int height();

	int depth();

	static int index(int x, int y, int z, int width, int height) {
		return width * height * z + width * y + x;
	}
	
	static int indexFast(int x, int y, int z, int w, int wh) {
		return wh * z + w * y + x;
	}
}
