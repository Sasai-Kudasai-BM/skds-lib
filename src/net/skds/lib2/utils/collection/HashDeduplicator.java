package net.skds.lib2.utils.collection;

import java.util.HashMap;

public class HashDeduplicator<T> implements Deduplicator<T> {

	private final HashMap<T, T> map;

	public HashDeduplicator() {
		this.map = new HashMap<>();
	}

	@Override
	public boolean contains(T object) {
		return map.containsKey(object);
	}

	@Override
	public boolean add(T object) {
		return map.putIfAbsent(object, object) == null;
	}

	@Override
	public boolean remove(T object) {
		return map.remove(object) != null;
	}

	@Override
	public T addOrTake(T object) {
		return map.computeIfAbsent(object, o -> o);
	}

	@Override
	public void clear() {
		map.clear();
	}
}
