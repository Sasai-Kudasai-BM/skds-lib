package net.skds.lib.utils.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Class2RefLinkedMap<T> implements Map<Class<T>, T> {

	private Link first;

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		return first == null;
	}

	@Override
	public boolean containsKey(Object key) {
		Link lnk = first;
		while (lnk != null) {
			if (lnk.c == key) {
				return true;
			}
			lnk = lnk.next;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		Link lnk = first;
		while (lnk != null) {
			if (Objects.equals(lnk.value, value)) {
				return true;
			}
			lnk = lnk.next;
		}
		return false;
	}

	@Override
	public T get(Object key) {
		Link lnk = first;
		while (lnk != null) {
			if (lnk.c == key) {
				return lnk.value;
			}
			lnk = lnk.next;
		}
		return null;
	}

	@Override
	public T put(Class<T> key, T value) {
		Link lnk = first;
		while (lnk != null) {
			if (lnk.c == key) {
				T old = lnk.value;
				lnk.value = value;
				return old;
			}
			lnk = lnk.next;
		}
		lnk = new Link(key, value);
		lnk.next = first;
		first = lnk;
		return null;
	}

	@Override
	public T remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends Class<T>, ? extends T> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		first = null;
	}

	@Override
	public Set<Class<T>> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<T> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<Class<T>, T>> entrySet() {
		throw new UnsupportedOperationException();
	}


	private class Link {
		private final Class<T> c;
		private T value;
		private Link next;

		public Link(Class<T> c, T value) {
			this.c = c;
			this.value = value;
		}
	}
}
