package net.skds.lib.collision.tree;

import net.skds.lib.collision.Box;

public interface BoundingTreeEntry<T> {

	T getValue();

	Box getBox();

}
