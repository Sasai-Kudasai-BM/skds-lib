package net.skds.lib2.utils.collection;

import net.skds.lib2.annotations.NotNull;
import net.skds.lib2.annotations.Nullable;

import java.util.Objects;
import java.util.WeakHashMap;

public class ReusableReferenceCollection<T> {

	private final WeakHashMap<T, Object> freeObjects = new WeakHashMap<>();

	public void put(@NotNull T value) {
		Objects.requireNonNull(value);
		freeObjects.put(value, null);
	}

	@Nullable
	public T take() {
		var itr = freeObjects.keySet().iterator();
		if (itr.hasNext()) {
			T n = itr.next();
			itr.remove();
			return n;
		}
		return null;
	}
}

