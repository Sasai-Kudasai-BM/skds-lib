package net.skds.lib2.utils.collection;

import lombok.AllArgsConstructor;

import java.util.*;

public final class ImmutableArrayHashMap<K, V> extends AbstractImmutableMap<K, V> {

	final Object[] table;
	final int size;

	private static final int EXPAND_FACTOR = 2;

	public ImmutableArrayHashMap(Object... input) {
		if ((input.length & 1) != 0) {
			throw new IllegalArgumentException("length is odd");
		}
		size = input.length >> 1;

		int len = EXPAND_FACTOR * input.length;
		len = (len + 1) & ~1; // ensure table is even length
		table = new Object[len];

		for (int i = 0; i < input.length; i += 2) {
			@SuppressWarnings("unchecked")
			K k = Objects.requireNonNull((K) input[i]);
			@SuppressWarnings("unchecked")
			V v = Objects.requireNonNull((V) input[i + 1]);
			int idx = probe(k);
			if (idx >= 0) {
				throw new IllegalArgumentException("duplicate key: " + k);
			} else {
				int dst = -(idx + 1);
				table[dst] = k;
				table[dst + 1] = v;
			}
		}
	}

	@Override
	public boolean containsKey(Object o) {
		Objects.requireNonNull(o);
		return size > 0 && probe(o) >= 0;
	}

	@Override
	public boolean containsValue(Object o) {
		Objects.requireNonNull(o);
		for (int i = 1; i < table.length; i += 2) {
			Object v = table[i];
			if (o.equals(v)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0; i < table.length; i += 2) {
			Object k = table[i];
			if (k != null) {
				hash += k.hashCode() ^ table[i + 1].hashCode();
			}
		}
		return hash;
	}

	@Override
	@SuppressWarnings("unchecked")
	public V get(Object o) {
		if (size == 0) {
			Objects.requireNonNull(o);
			return null;
		}
		int i = probe(o);
		if (i >= 0) {
			return (V) table[i + 1];
		} else {
			return null;
		}
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	class Itr implements Iterator<Map.Entry<K, V>> {

		private int remaining;
		private int idx;

		Itr() {
			remaining = size;
			idx = 0;
		}

		@Override
		public boolean hasNext() {
			return remaining > 0;
		}

		@Override
		public Map.Entry<K, V> next() {
			if (remaining > 0) {
				int idx = this.idx;
				while (table[idx += 2] == null) {
				}
				this.idx = idx;
				@SuppressWarnings("unchecked")
				Map.Entry<K, V> e = new Entry((K) table[idx], (V) table[idx + 1]);
				remaining--;
				return e;
			} else {
				throw new NoSuchElementException();
			}
		}
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return new AbstractSet<>() {
			@Override
			public int size() {
				return size;
			}

			@Override
			public Iterator<Map.Entry<K, V>> iterator() {
				return new Itr();
			}
		};
	}

	private int probe(Object pk) {
		int idx = Math.floorMod(pk.hashCode(), table.length >> 1) << 1;
		while (true) {
			@SuppressWarnings("unchecked")
			K ek = (K) table[idx];
			if (ek == null) {
				return -idx - 1;
			} else if (pk.equals(ek)) {
				return idx;
			} else if ((idx += 2) == table.length) {
				idx = 0;
			}
		}
	}

	@AllArgsConstructor
	private class Entry implements Map.Entry<K, V> {
		final K k;
		final V v;

		@Override
		public K getKey() {
			return k;
		}

		@Override
		public V getValue() {
			return v;
		}

		@Override
		public V setValue(V value) {
			throw uoe();
		}
	}
}
