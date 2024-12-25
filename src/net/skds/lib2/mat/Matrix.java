package net.skds.lib2.mat;

public interface Matrix {

	int width();

	int height();

	double get(int row, int col);

	float getF(int row, int col);

	double[] getRow(int row);

	float[] getRowF(int row);

	Vector getRowV(int row);

	double[] getCol(int row);

	float[] getColF(int row);

	Vector getColV(int row);
}
