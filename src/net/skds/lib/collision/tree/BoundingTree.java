package net.skds.lib.collision.tree;

import net.skds.lib.collision.Box;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface BoundingTree<T> {

	void forEach(BiConsumer<Box, T> action);

	void forEachEntry(Consumer<BoundingTreeEntry<T>> action);

	void forEachValue(Consumer<T> action);

	void forEachInBox(Box box, Consumer<BoundingTreeEntry<T>> action);

	Collection<BoundingTreeEntry<T>> getEntries(Box box);

	Collection<T> getValues(Box box);

	BoundingTreeEntry<T> put(Box box, T value);

	boolean remove(BoundingTreeEntry<T> entry);

	int putAll(Iterable<BoundingTreeEntry<T>> values);

	void removeAll(Box box);


	void update(BoundingTreeEntry<T> node, Box newBox);

}
