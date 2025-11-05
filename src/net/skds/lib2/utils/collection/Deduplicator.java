package net.skds.lib2.utils.collection;

public interface Deduplicator<T> {

	boolean contains(T object);

	boolean add(T object);

	boolean remove(T object);

	T addOrTake(T object);

	void clear();
}
