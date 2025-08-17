package net.skds.lib2.misc.fields;

public interface FloatField2D extends Field2D {

	float[] toArray();

	float getValue(int x, int y);

	void setValue(float value, int x, int y);

	void increment(float value, int x, int y);

	void multiply(float value, int x, int y);
}
