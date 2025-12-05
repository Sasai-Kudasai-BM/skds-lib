package net.skds.lib2.utils;

public interface DirtyAble {

	boolean isDirty();

	void markDirty();

	void unmarkDirty();
}
