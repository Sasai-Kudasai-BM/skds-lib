package net.skds.lib.collision.tree;

import net.skds.lib.collision.Box;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Base2BoundingTree<T> implements BoundingTree<T> {

	private Node<T> root;

	public Base2BoundingTree() {
	}

	@Override
	public void forEach(BiConsumer<Box, T> action) {
		forEachEntry(e -> action.accept(e.getBox(), e.getValue()));
	}

	@Override
	public void forEachInBox(Box box, Consumer<BoundingTreeEntry<T>> action) {
		if (root != null) {
			root.forEachInBox(box, action);
		}
	}

	@Override
	public void forEachEntry(Consumer<BoundingTreeEntry<T>> action) {
		if (root != null) {
			root.forEach(action);
		}
	}

	@Override
	public void forEachValue(Consumer<T> action) {
		if (root != null) {
			root.forEach(e -> action.accept(e.getValue()));
		}
	}

	@Override
	public Collection<BoundingTreeEntry<T>> getEntries(Box box) {
		List<BoundingTreeEntry<T>> overlaps = new ArrayList<>();
		if (root != null) {
			root.forEachInBox(box, overlaps::add);
		}
		return overlaps;
	}

	@Override
	public Collection<T> getValues(Box box) {
		List<T> overlaps = new ArrayList<>();
		if (root != null) {
			root.forEachInBox(box, e -> overlaps.add(e.getValue()));
		}
		return overlaps;
	}


	@Override
	public BoundingTreeEntry<T> put(Box box, T value) {
		Node<T> leafNode = new Node<>(box, value);
		insert(leafNode);
		return leafNode;
	}

	@Override
	public void update(BoundingTreeEntry<T> node, Box newBox) {
		Node<T> n = (Node<T>) node;
		if (n.box.contains(newBox) && n.area / newBox.surfaceArea() < 2) return;
		removeLeaf(n);
		n.box = newBox;
		insert(n);
	}

	@Override
	public boolean remove(BoundingTreeEntry<T> entry) {
		return removeLeaf((Node<T>) entry);
	}

	@Override
	public int putAll(Iterable<BoundingTreeEntry<T>> values) {
		int i = 0;
		for (BoundingTreeEntry<T> e : values) {
			put(e.getBox(), e.getValue());
			i++;
		}
		return i;
	}

	@Override
	public void removeAll(Box box) {
		if (root != null) {
			root.forEachInBox(box, this::remove);
		}
	}

	private void insert(Node<T> leafNode) {
		if (root == null) {
			root = leafNode;
		}
		Node<T> node = root;
		while (!node.isLeaf()) {

			Box combinedBox = node.box.union(leafNode.box);
			float cArea = (float) combinedBox.surfaceArea();

			float newParentNodeCost = 2.0f * cArea;
			float minimumPushDownCost = 2.0f * (cArea - node.area);

			float costLeft = nodeWeight(node.area, node.left, leafNode) + minimumPushDownCost;
			float costRight = nodeWeight(node.area, node.right, leafNode) + minimumPushDownCost;

			if (newParentNodeCost < costLeft && newParentNodeCost < costRight) {
				break;
			}

			if (costLeft < costRight) {
				node = node.left;
			} else {
				node = node.right;
			}
		}

		Node<T> oldParent = node.parent;
		Node<T> newParent = new Node<>(leafNode.box.union(node.box), null);
		newParent.parent = oldParent;
		newParent.left = node;
		newParent.right = leafNode;

		node.parent = newParent;
		leafNode.parent = newParent;

		if (oldParent == null) {
			root = newParent;
		} else {
			if (oldParent.left == node) {
				oldParent.left = newParent;
			} else {
				oldParent.right = newParent;
			}
		}
		balanceTree(leafNode.parent);
	}

	private boolean removeLeaf(Node<T> leaf) {
		if (leaf == root) {
			root = null;
			return true;
		}

		Node<T> parentNode = leaf.parent;
		if (parentNode == null) return false;

		Node<T> grandParentNode = parentNode.parent;
		Node<T> siblingNode = parentNode.left == leaf ? parentNode.right : parentNode.left;
		assert (siblingNode != null);

		if (grandParentNode != null) {
			if (grandParentNode.left == parentNode) {
				grandParentNode.left = siblingNode;
			} else {
				grandParentNode.right = siblingNode;
			}
			siblingNode.parent = grandParentNode;

			balanceTree(grandParentNode);
		} else {
			root = siblingNode;
			siblingNode.parent = null;
		}
		leaf.parent = null;
		return true;
	}

	private void balanceTree(Node<T> node) {
		while (node != null) {
			assert (node.left != null && node.right != null);
			node.box = node.left.box.union(node.right.box);
			node = node.parent;
		}
	}


	private float nodeWeight(float nodeArea, Node<T> branch, Node<T> leaf) {
		if (branch.isLeaf()) {
			return (float) leaf.box.union(branch.box).surfaceArea();
		} else {
			Box newLeftAabb = leaf.box.union(branch.box);
			return (float) (newLeftAabb.surfaceArea() - nodeArea);
		}
	}


	private static final class Node<T> implements BoundingTreeEntry<T> {
		Box box;
		float area;
		Node<T> parent;
		Node<T> left;
		Node<T> right;
		T value;

		Node(Box box, T value) {
			this.box = box;
			this.value = value;
			this.area = (float) box.surfaceArea();
		}

		private void forEachInBox(Box box, Consumer<BoundingTreeEntry<T>> action) {
			if (this.box.intersects(box)) {
				if (isLeaf()) {
					action.accept(this);
				} else {
					left.forEachInBox(box, action);
					right.forEachInBox(box, action);
				}
			}
		}

		private void forEach(Consumer<BoundingTreeEntry<T>> action) {
			if (isLeaf()) {
				action.accept(this);
			} else {
				left.forEach(action);
				right.forEach(action);
			}
		}

		boolean isLeaf() {
			return left == null;
		}

		@Override
		public T getValue() {
			return value;
		}

		@Override
		public Box getBox() {
			return box;
		}

	}
}
