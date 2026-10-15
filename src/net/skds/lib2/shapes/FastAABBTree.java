package net.skds.lib2.shapes;

import java.util.Arrays;
import java.util.function.IntConsumer;
import java.util.function.ObjIntConsumer;

public class FastAABBTree<T> {

	private static final float COST_RATIO = 0.5f;
	public static final int NULL = -1;

	private int[] parent;
	private int[] left;
	private int[] right;
	private Object[] values;
	private double[] bounding;

	private int freeIndex;
	private int[] free;

	private int root = NULL;

	private int[] queryStack;

	private final int initialSize;

	public FastAABBTree(int initialSize) {
		this.initialSize = initialSize;
		this.queryStack = new int[initialSize];
		this.parent = new int[initialSize];
		this.left = new int[initialSize];
		this.right = new int[initialSize];
		this.values = new Object[initialSize];
		this.bounding = new double[initialSize * 6];
		this.free = new int[initialSize];
		for (int i = 0; i < initialSize; i++) {
			this.free[i] = i;
		}
		this.freeIndex = initialSize - 1;

		Arrays.fill(parent, NULL);
		Arrays.fill(left, NULL);
		Arrays.fill(right, NULL);
	}

	private int allocNode() {
		int i = this.freeIndex;
		if (i >= 0) {
			int id = this.free[i];
			this.freeIndex--;
			parent[id] = NULL;
			left[id] = NULL;
			right[id] = NULL;
			values[id] = null;
			return id;
		}
		int oldSize = this.parent.length;
		int newSize = oldSize * 2;

		this.parent = Arrays.copyOf(this.parent, newSize);
		this.left = Arrays.copyOf(this.left, newSize);
		this.right = Arrays.copyOf(this.right, newSize);
		this.values = Arrays.copyOf(this.values, newSize);
		this.bounding = Arrays.copyOf(this.bounding, newSize * 6);

		this.free = new int[oldSize];
		for (int j = 0; j < oldSize; j++) {
			this.free[j] = oldSize + j;
		}

		Arrays.fill(parent, oldSize, newSize, NULL);
		Arrays.fill(left, oldSize, newSize, NULL);
		Arrays.fill(right, oldSize, newSize, NULL);

		this.freeIndex = oldSize - 2;
		return this.free[oldSize - 1];
	}

	private void freeNode(int nodeId) {
		parent[nodeId] = NULL;
		left[nodeId] = NULL;
		right[nodeId] = NULL;
		values[nodeId] = null;
		freeIndex++;
		if (freeIndex >= free.length) {
			free = Arrays.copyOf(free, free.length * 2);
		}
		free[freeIndex] = nodeId;
	}

	public int put(AABB box, T value) {
		return put(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, value);
	}

	public int put(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, T value) {
		checkValid(minX, minY, minZ, maxX, maxY, maxZ);
		int leafNodeId = allocNode();

		int bIdx = leafNodeId * 6;
		bounding[bIdx] = minX;
		bounding[bIdx + 1] = minY;
		bounding[bIdx + 2] = minZ;
		bounding[bIdx + 3] = maxX;
		bounding[bIdx + 4] = maxY;
		bounding[bIdx + 5] = maxZ;
		values[leafNodeId] = value;

		put0(leafNodeId);
		return leafNodeId;
	}

	private void put0(int leafId) {
		if (root == NULL) {
			root = leafId;
			return;
		}
		double lMinX = bounding[leafId * 6], lMinY = bounding[leafId * 6 + 1], lMinZ = bounding[leafId * 6 + 2];
		double lMaxX = bounding[leafId * 6 + 3], lMaxY = bounding[leafId * 6 + 4], lMaxZ = bounding[leafId * 6 + 5];

		int current = root;
		while (right[current] != NULL) {
			int l = left[current];
			int r = right[current];

			float costCurrent = unionCost(
					bounding[current * 6], bounding[current * 6 + 1], bounding[current * 6 + 2],
					bounding[current * 6 + 3], bounding[current * 6 + 4], bounding[current * 6 + 5],
					lMinX, lMinY, lMinZ,
					lMaxX, lMaxY, lMaxZ
			);
			float costLeft = unionCost(
					bounding[l * 6], bounding[l * 6 + 1], bounding[l * 6 + 2],
					bounding[l * 6 + 3], bounding[l * 6 + 4], bounding[l * 6 + 5],
					lMinX, lMinY, lMinZ,
					lMaxX, lMaxY, lMaxZ
			);
			if (right[l] == NULL) {
				costLeft += cost(lMinX, lMinY, lMinZ, lMaxX, lMaxY, lMaxZ);
			}
			float costRight = unionCost(
					bounding[r * 6], bounding[r * 6 + 1], bounding[r * 6 + 2],
					bounding[r * 6 + 3], bounding[r * 6 + 4], bounding[r * 6 + 5],
					lMinX, lMinY, lMinZ,
					lMaxX, lMaxY, lMaxZ
			);
			if (right[r] == NULL) {
				costRight += cost(lMinX, lMinY, lMinZ, lMaxX, lMaxY, lMaxZ);
			}
			if (costCurrent < costLeft && costCurrent < costRight) {
				break;
			}
			current = (costLeft < costRight) ? l : r;
		}

		int sibling = current;
		int oldParent = parent[sibling];
		int newParent = allocNode();

		parent[newParent] = oldParent;
		values[newParent] = null;

		int npIdx = newParent * 6;
		int sIdx = sibling * 6;
		bounding[npIdx] = Math.min(bounding[sIdx], lMinX);
		bounding[npIdx + 1] = Math.min(bounding[sIdx + 1], lMinY);
		bounding[npIdx + 2] = Math.min(bounding[sIdx + 2], lMinZ);
		bounding[npIdx + 3] = Math.max(bounding[sIdx + 3], lMaxX);
		bounding[npIdx + 4] = Math.max(bounding[sIdx + 4], lMaxY);
		bounding[npIdx + 5] = Math.max(bounding[sIdx + 5], lMaxZ);

		if (oldParent != NULL) {
			if (left[oldParent] == sibling) left[oldParent] = newParent;
			else right[oldParent] = newParent;

			left[newParent] = sibling;
			right[newParent] = leafId;
			parent[sibling] = newParent;
			parent[leafId] = newParent;
		} else {
			left[newParent] = sibling;
			right[newParent] = leafId;
			parent[sibling] = newParent;
			parent[leafId] = newParent;
			root = newParent;
		}
		normalize(parent[leafId]);
	}

	public void remove(int leafId) {
		if (leafId == NULL) {
			throw new IllegalStateException("Already removed or invalid node ID");
		}
		if (leafId == root) {
			root = NULL;
			freeNode(leafId);
			return;
		}
		int p = parent[leafId];
		if (p == NULL) {
			throw new IllegalStateException("Already removed or invalid node ID");
		}
		int grandParent = parent[p];
		int sibling = (left[p] == leafId) ? right[p] : left[p];

		if (grandParent != NULL) {
			if (left[grandParent] == p) left[grandParent] = sibling;
			else right[grandParent] = sibling;

			parent[sibling] = grandParent;
			freeNode(p);
			freeNode(leafId);
			normalize(grandParent);
		} else {
			root = sibling;
			parent[sibling] = NULL;
			freeNode(p);
			freeNode(leafId);
		}
	}

	public void move(int leafId, AABB newBounding) {
		move(leafId, newBounding.minX, newBounding.minY, newBounding.minZ, newBounding.maxX, newBounding.maxY, newBounding.maxZ);
	}

	public void move(int leafId, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		if (leafId == NULL) {
			throw new IllegalStateException("Cannot move a null or removed node");
		}
		checkValid(minX, minY, minZ, maxX, maxY, maxZ);

		int idx = leafId * 6;
		if (bounding[idx] == minX && bounding[idx + 1] == minY && bounding[idx + 2] == minZ &&
				bounding[idx + 3] == maxX && bounding[idx + 4] == maxY && bounding[idx + 5] == maxZ) {
			return;
		}
		removeKeepNode(leafId);

		bounding[idx] = minX;
		bounding[idx + 1] = minY;
		bounding[idx + 2] = minZ;
		bounding[idx + 3] = maxX;
		bounding[idx + 4] = maxY;
		bounding[idx + 5] = maxZ;

		put0(leafId);
	}

	private void removeKeepNode(int leafId) {
		if (leafId == root) {
			root = NULL;
			return;
		}
		int p = parent[leafId];
		if (p == NULL) {
			throw new IllegalStateException("Already removed or invalid node ID");
		}

		int grandParent = parent[p];
		int sibling = (left[p] == leafId) ? right[p] : left[p];

		if (grandParent != NULL) {
			if (left[grandParent] == p) left[grandParent] = sibling;
			else right[grandParent] = sibling;

			parent[sibling] = grandParent;
			freeNode(p);
			normalize(grandParent);
		} else {
			root = sibling;
			parent[sibling] = NULL;
			freeNode(p);
		}
	}


	public void forEachCollision(AABB box, ObjIntConsumer<T> action) {
		forEachCollision(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, action);
	}

	@SuppressWarnings("unchecked")
	public void forEachCollision(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, ObjIntConsumer<T> action) {
		if (root == NULL) return;

		int bIdx = root * 6;
		if (maxX < bounding[bIdx] || minX > bounding[bIdx + 3] ||
				maxY < bounding[bIdx + 1] || minY > bounding[bIdx + 4] ||
				maxZ < bounding[bIdx + 2] || minZ > bounding[bIdx + 5]) {
			return;
		}

		if (right[root] == NULL) {
			action.accept((T) values[root], root);
			return;
		}

		ensureStackCapacity(parent.length);

		int stackPtr = 0;
		queryStack[stackPtr++] = root;

		while (stackPtr > 0) {
			int n = queryStack[--stackPtr];
			int l = left[n];
			int r = right[n];

			if (l != NULL) {
				int idx = l * 6;
				if (!(maxX < bounding[idx] || minX > bounding[idx + 3] ||
						maxY < bounding[idx + 1] || minY > bounding[idx + 4] ||
						maxZ < bounding[idx + 2] || minZ > bounding[idx + 5])) {
					if (right[l] == NULL) {
						action.accept((T) values[l], l);
					} else {
						queryStack[stackPtr++] = l;
					}
				}
			}
			if (r != NULL) {
				int idx = r * 6;
				if (!(maxX < bounding[idx] || minX > bounding[idx + 3] ||
						maxY < bounding[idx + 1] || minY > bounding[idx + 4] ||
						maxZ < bounding[idx + 2] || minZ > bounding[idx + 5])) {
					if (right[r] == NULL) {
						action.accept((T) values[r], r);
					} else {
						queryStack[stackPtr++] = r;
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void forEach(ObjIntConsumer<T> action) {
		if (root == NULL) return;

		if (right[root] == NULL) {
			action.accept((T) values[root], root);
			return;
		}

		ensureStackCapacity(parent.length);
		int stackPtr = 0;
		queryStack[stackPtr++] = root;

		while (stackPtr > 0) {
			int n = queryStack[--stackPtr];
			if (n != NULL && right[n] == NULL) {
				action.accept((T) values[n], n);
			}
			if (right[n] != NULL) {
				queryStack[stackPtr++] = left[n];
				queryStack[stackPtr++] = right[n];
			}
		}
	}

	private void ensureStackCapacity(int capacity) {
		if (queryStack.length < capacity) {
			queryStack = new int[capacity * 2];
		}
	}

	private void normalize(int n) {
		while (n != NULL) {
			int l = left[n];
			int r = right[n];

			int nIdx = n * 6;
			int lIdx = l * 6;
			int rIdx = r * 6;

			bounding[nIdx] = Math.min(bounding[lIdx], bounding[rIdx]);
			bounding[nIdx + 1] = Math.min(bounding[lIdx + 1], bounding[rIdx + 1]);
			bounding[nIdx + 2] = Math.min(bounding[lIdx + 2], bounding[rIdx + 2]);
			bounding[nIdx + 3] = Math.max(bounding[lIdx + 3], bounding[rIdx + 3]);
			bounding[nIdx + 4] = Math.max(bounding[lIdx + 4], bounding[rIdx + 4]);
			bounding[nIdx + 5] = Math.max(bounding[lIdx + 5], bounding[rIdx + 5]);

			n = parent[n];
		}
	}

	public void clear() {
		this.root = NULL;
		this.freeIndex = parent.length - 1;
		for (int i = 0; i < parent.length; i++) {
			this.free[i] = i;
		}
		Arrays.fill(parent, NULL);
		Arrays.fill(left, NULL);
		Arrays.fill(right, NULL);
		Arrays.fill(values, null);
	}

	public void clearTrim() {
		this.root = NULL;
		if (parent.length > this.initialSize) {
			this.queryStack = new int[initialSize];
			this.parent = new int[initialSize];
			this.left = new int[initialSize];
			this.right = new int[initialSize];
			this.values = new Object[initialSize];
			this.bounding = new double[initialSize * 6];
			this.free = new int[initialSize];
			Arrays.fill(parent, NULL);
			Arrays.fill(left, NULL);
			Arrays.fill(right, NULL);
		} else {
			Arrays.fill(parent, NULL);
			Arrays.fill(left, NULL);
			Arrays.fill(right, NULL);
			Arrays.fill(values, null);
		}
		this.freeIndex = parent.length - 1;
		for (int i = 0; i < parent.length; i++) {
			this.free[i] = i;
		}
	}

	private float unionCost(double minX1, double minY1, double minZ1, double maxX1, double maxY1, double maxZ1,
	                        double minX2, double minY2, double minZ2, double maxX2, double maxY2, double maxZ2) {
		return (float) ((Math.max(maxX1, maxX2) - Math.min(minX1, minX2)) +
				(Math.max(maxY1, maxY2) - Math.min(minY1, minY2)) +
				(Math.max(maxZ1, maxZ2) - Math.min(minZ1, minZ2)));
	}

	private float cost(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return (float) ((maxX - minX) + (maxY - minY) + (maxZ - minZ)) * COST_RATIO;
	}

	private void checkValid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		if (Double.isNaN(minX) || Double.isNaN(minY) || Double.isNaN(minZ) ||
				Double.isNaN(maxX) || Double.isNaN(maxY) || Double.isNaN(maxZ) ||
				minX > maxX || minY > maxY || minZ > maxZ) {
			throw new IllegalArgumentException("Invalid Bounds");
		}
	}

	public void getBounds(int nodeId, double[] dst) {
		System.arraycopy(bounding, nodeId * 6, dst, 0, 6);
	}

	public AABB getBounds(int nodeId) {
		int i = nodeId * 6;
		return new AABB(
				bounding[i],
				bounding[i + 1],
				bounding[i + 2],
				bounding[i + 3],
				bounding[i + 4],
				bounding[i + 5]
		);
	}

	public void forEachNode(IntConsumer action) {
		if (root == NULL) return;

		ensureStackCapacity(parent.length);
		int stackPtr = 0;
		queryStack[stackPtr++] = root;

		while (stackPtr > 0) {
			int n = queryStack[--stackPtr];
			action.accept(n);
			if (right[n] != NULL) {
				queryStack[stackPtr++] = left[n];
				queryStack[stackPtr++] = right[n];
			}
		}
	}

	public boolean isInternalNode(int nodeId) {
		return nodeId != NULL && right[nodeId] != NULL;
	}
}