package net.w3e.lib.utils.collection;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {
	public static <T> List<List<T>> partition(List<T> collection, int partitionSize, boolean modifiable) {
		List<List<T>> partitions = new ArrayList<>();

		for (int i = 0; i < collection.size(); i += partitionSize) {
			List<T> subList = collection.subList(i, Math.min(i + partitionSize, collection.size()));
			if (modifiable) {
				subList = new ArrayList<>(subList);
			}
			partitions.add(subList);
		}

		return partitions;
	}
}
