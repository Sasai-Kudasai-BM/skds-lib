package net.skds.lib2.misc.fields;

public interface Field2D {

	int width();

	int height();


	static int index(int x, int y, int width) {
		return width * (y / width) + x;
	}
}
