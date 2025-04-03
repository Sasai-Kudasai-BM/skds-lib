package net.skds.lib2.shapes2d;

import net.skds.lib2.utils.Removable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AABRTree<T> {

	private Node root;

	public TreeNode<T> put(AABR bounding, T value) {
		checkValid(bounding);
		Node newNode = new Node(bounding, value);
		put0(bounding, newNode);
		return newNode;
	}

	private void put0(AABR bounding, Node newNode) {
		Node root = this.root;
		if (root == null) {
			root = new Node(bounding, null);
			this.root = root;
			root.left = newNode;
			newNode.parent = root;
			return;
		}
		Node nextNode = root;
		do {
			nextNode = nextNode.chooseNode(bounding, newNode);
		} while (nextNode != null);

		normalize(newNode.parent);
	}

	public Iterable<TreeNode<T>> getCollisions(AABR rect) {
		Node root = this.root;
		if (root == null || !root.bounding.intersects(rect)) {
			return List.of();
		}
		ArrayList<TreeNode<T>> intersections = new ArrayList<>();
		ArrayList<Node> stack = new ArrayList<>();
		stack.add(root);
		do {
			Node n = stack.removeLast();
			Node l = n.left;
			Node r = n.right;
			if (l != null && l.bounding.intersects(rect)) {
				if (l.isLeaf()) {
					intersections.add(l);
				} else {
					stack.add(l);
				}
			}
			if (r != null && r.bounding.intersects(rect)) {
				if (r.isLeaf()) {
					intersections.add(r);
				} else {
					stack.add(r);
				}
			}
		} while (!stack.isEmpty());

		return intersections;
	}

	private void normalize(Node n) {
		do {
			n = n.normalize();
		} while (n != null);
	}

	public void foreach(Consumer<TreeNode<T>> action) {
		Node node = this.root;
		if (node != null) {
			foreach0(action, node);
		}

	}

	private void foreach0(Consumer<TreeNode<T>> action, Node n) {
		if (n.value != null) {
			action.accept(n);
		}
		Node left = n.left;
		if (left != null) {
			foreach0(action, left);
		}
		Node right = n.right;
		if (right != null) {
			foreach0(action, right);
		}
	}

	public void foreachNode(Consumer<TreeNode<T>> action) {
		Node node = this.root;
		if (node != null) {
			foreachNode0(action, node);
		}
	}

	private void foreachNode0(Consumer<TreeNode<T>> action, Node n) {

		action.accept(n);

		Node left = n.left;
		if (left != null) {
			foreachNode0(action, left);
		}
		Node right = n.right;
		if (right != null) {
			foreachNode0(action, right);
		}
	}

	private float unionCost(AABR a, AABR b) {
		double minX = Math.min(a.minX, b.minX);
		double minY = Math.min(a.minY, b.minY);
		double maxX = Math.max(a.maxX, b.maxX);
		double maxY = Math.max(a.maxY, b.maxY);

		return (float) ((maxX - minX) + (maxY - minY));
	}

	private float cost(AABR a) {
		return (float) ((a.maxX - a.minX) + (a.maxY - a.minY));
	}

	private void checkValid(AABR a) {
		if (!a.isValid() || !a.isNormal()) {
			throw new IllegalArgumentException("Invalid " + a);
		}
	}

	private class Node implements TreeNode<T> {
		Node parent;
		Node left;
		Node right;
		AABR bounding;
		final T value;

		Node(AABR bounding, T value) {
			this.bounding = bounding;
			this.value = value;
		}

		private boolean isLeaf() {
			return value != null;
		}

		private Node chooseNode(AABR newBounding, Node newNode) {
			float c0 = cost(this.bounding);
			float c = unionCost(newBounding, this.bounding) - c0;
			float cl;
			float cr;
			Node left = this.left;
			Node right = this.right;
			if (right == null) {
				cr = c;
			} else {
				cr = unionCost(newBounding, right.bounding) - cost(right.bounding);
			}
			if (left == null) {
				cl = c;
			} else {
				cl = unionCost(newBounding, left.bounding) - cost(left.bounding);
			}

			if (cr < cl) { // right
				//System.out.println("right " + cr + "/" + cl);
				if (right == null) {
					newNode.parent = this;
					this.right = newNode;
					return null;
				}
				if (right.isLeaf()) {
					Node n = new Node(right.bounding.union(newBounding), null);
					n.left = right;
					n.parent = this;
					right.parent = n;
					this.right = n;
					return n;
				}
				return right;
			} else { // left
				//System.out.println("left " + cr + "/" + cl);
				if (left == null) {
					newNode.parent = this;
					this.left = newNode;
					return null;
				}
				if (left.isLeaf()) {
					Node n = new Node(left.bounding.union(newBounding), null);
					n.right = left;
					n.parent = this;
					left.parent = n;
					this.left = n;
					return n;
				}
				return left;
			}
		}

		private Node normalize() {
			Node right = this.right;
			Node left = this.left;

			double minX;
			double minY;
			double maxX;
			double maxY;

			if (right == null) {
				if (left == null) {
					throw new IllegalStateException();
				}
				AABR rect = left.bounding;
				minX = rect.minX;
				minY = rect.minY;
				maxX = rect.maxX;
				maxY = rect.maxY;
			} else if (left == null) {
				AABR rect = right.bounding;
				minX = rect.minX;
				minY = rect.minY;
				maxX = rect.maxX;
				maxY = rect.maxY;
			} else {
				AABR a = left.bounding;
				AABR b = right.bounding;
				minX = Math.min(a.minX, b.minX);
				minY = Math.min(a.minY, b.minY);
				maxX = Math.max(a.maxX, b.maxX);
				maxY = Math.max(a.maxY, b.maxY);
			}

			AABR union = bounding;

			if (!union.fullyContains(minX, minY, maxX, maxY)) {
				this.bounding = new AABR(minX, minY, maxX, maxY);
				return parent;
			}

			float up = cost(union);
			float np = (float) ((maxX - minX) + (maxY - minY));
			if (up > np * 1.5f) {
				this.bounding = new AABR(minX, minY, maxX, maxY);
				return parent;
			}
			return null;
		}

		@Override
		public void remove() {
			Node n = null;
			if (isLeaf()) {
				if (parent.left == this) {
					parent.left = null;
				} else {
					parent.right = null;
				}
				if (parent.left == null && parent.right == null) {
					n = parent;
				} else {
					parent.normalize();
				}
			} else {
				n = parent;
			}
			while (n != null) {
				n = n.remove0();
			}
		}

		private Node remove0() {
			Node p = parent;
			if (left != null || right != null || p == null) {
				return null;
			}
			if (p.left == this) {
				p.left = null;
			} else {
				p.right = null;
			}
			if (p.left == null && p.right == null) {
				return p;
			} else {
				AABRTree.this.normalize(p);
				return null;
			}
		}

		@Override
		public AABR getBounding() {
			return bounding;
		}

		@Override
		public void move(AABR newBounding) { // TODO
			checkValid(newBounding);
			remove();
			put0(bounding, this);
		}

		@Override
		public T getValue() {
			return value;
		}
	}

	public interface TreeNode<T> extends Removable {
		T getValue();

		AABR getBounding();

		void move(AABR newBounding);
	}
}
