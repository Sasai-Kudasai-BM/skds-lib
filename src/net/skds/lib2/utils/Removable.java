package net.skds.lib2.utils;

public interface Removable {
	void remove();

	interface ValueRemovable<T> extends Removable {
		T getValue();
	}

	interface BiValueRemovable<A, B> extends Removable {
		A getValueA();

		B getValueB();
	}
}
