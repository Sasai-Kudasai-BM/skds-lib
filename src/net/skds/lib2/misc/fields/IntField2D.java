package net.skds.lib2.misc.fields;

public interface IntField2D extends Field2D {

	int[] toArray();

	int getValue(int x, int y);

	void setValue(int value, int x, int y);

	void increment(int value, int x, int y);

	void multiply(int value, int x, int y);
}
