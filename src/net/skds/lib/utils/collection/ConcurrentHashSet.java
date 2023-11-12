package net.skds.lib.utils.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<T> implements Set<T> {

	final ConcurrentHashMap<T, Boolean> map = new ConcurrentHashMap<>();

	@Override
	public boolean remove(Object key) {
		return map.remove(key);
	}

	@Override
	public Iterator<T> iterator() {
		return map.keys().asIterator();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException("Unimplemented method 'toArray'");
	}

	@Override
	public <T2> T2[] toArray(T2[] a) {
		throw new UnsupportedOperationException("Unimplemented method 'toArray'");
	}

	@Override
	public boolean add(T e) {
		return map.put(e, true) == null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException("Unimplemented method 'containsAll'");
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;
		for (T v : c) {
			if (map.put(v, true) == null) {
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Unimplemented method 'retainAll'");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object v : c) {
			if (map.remove(v) != null) {
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return map.get(o) != null;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		var en = map.keys();
		boolean first = true;
		while (en.hasMoreElements()) {
			if (!first) {
				sb.append(", ");
			}
			first = false;
			sb.append(en.nextElement());
		}
		return sb.append(']').toString();
	}

}
