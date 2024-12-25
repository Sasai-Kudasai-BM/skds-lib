package net.skds.lib2.mat;

@SuppressWarnings("unused")
public interface Vector {

	int dimension();

	double get(int i);

	int getI(int i);

	float getF(int i);

	int floor(int i);

	int ceil(int i);

	int round(int i);

	Vector getAsIntVec();

	Vector getAsFloatVec();

	Vector getAsDoubleVec();
}
