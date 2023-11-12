package net.skds.lib.storage;

import net.skds.lib.utils.SKDSUtils.Indexed;

public interface Palettable<T extends Palettable<T>> extends Indexed, Comparable<T> {

	@Override
	default int compareTo(T o) {
		return getIndex() - o.getIndex();
	}

	public static <T extends Palettable<T>> int compare(T o1, T o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			}
			return 1;
		}
		return o1.getIndex() - o2.getIndex();
	}

}
