package net.skds.lib2.utils.collection;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractImmutableMap<K, V> extends AbstractMap<K, V> implements Serializable {
	@Override
	public void clear() {
		throw uoe();
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> rf) {
		throw uoe();
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mf) {
		throw uoe();
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> rf) {
		throw uoe();
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> rf) {
		throw uoe();
	}

	@Override
	public V put(K key, V value) {
		throw uoe();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw uoe();
	}

	@Override
	public V putIfAbsent(K key, V value) {
		throw uoe();
	}

	@Override
	public V remove(Object key) {
		throw uoe();
	}

	@Override
	public boolean remove(Object key, Object value) {
		throw uoe();
	}

	@Override
	public V replace(K key, V value) {
		throw uoe();
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		throw uoe();
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> f) {
		throw uoe();
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		V v;
		return ((v = get(key)) != null)
				? v
				: defaultValue;
	}

	protected static UnsupportedOperationException uoe() {
		return new UnsupportedOperationException();
	}
}