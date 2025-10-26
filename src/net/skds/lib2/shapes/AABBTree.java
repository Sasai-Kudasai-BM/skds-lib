package net.skds.lib2.shapes;

import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.utils.Removable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AABBTree<T> {

	private static final boolean DEBUG = Boolean.getBoolean("skds.bounding_tree.debug");
	private static final float COST_RATIO = .25f;

	private Node root;

	public TreeNode<T> put(AABB bounding, T value) {
		checkValid(bounding);
		Node newNode = new Node(bounding, value);
		put0(bounding, newNode);
		return newNode;
	}

	private void put0(AABB bounding, Node newNode) {
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

	public Iterable<TreeNode<T>> getCollisions(AABB box) {
		Node root = this.root;
		if (root == null || !root.bounding.intersects(box)) {
			return List.of();
		}
		ArrayList<TreeNode<T>> intersections = new ArrayList<>();
		ArrayList<Node> stack = new ArrayList<>();
		stack.add(root);
		//int c = 0;
		do {
			//c++;
			Node n = stack.removeLast();
			Node l = n.left;
			Node r = n.right;
			if (l != null && l.bounding.intersects(box)) {
				if (l.isLeaf()) {
					intersections.add(l);
				} else {
					stack.add(l);
				}
			}
			if (r != null && r.bounding.intersects(box)) {
				if (r.isLeaf()) {
					intersections.add(r);
				} else {
					stack.add(r);
				}
			}
		} while (!stack.isEmpty());
		//System.out.println("Iterations " + c);
		return intersections;
	}


	public NodeRayCollision<T> rayTrace(Vec3 start, Vec3 end, CollisionContext context) {
		Node root = this.root;
		if (root == null || !root.bounding.intersectsRay(start, end)) {
			return null;
		}
		TreeNode<T> nearestNode = null;
		Collision nearest = null;
		ArrayList<Node> stack = new ArrayList<>();
		stack.add(root);
		//int c = 0;
		do {
			//c++;
			Node n = stack.removeLast();
			Node l = n.left;
			Node r = n.right;
			if (l != null && l.bounding.intersectsRay(start, end)) {
				if (l.isLeaf()) {
					Node chosen = l;
					if (context.canCollide(chosen.bounding)) {
						Collision collision = chosen.bounding.raytrace(start, end, context);
						if (collision != null) {
							if (nearest == null || collision.compareTo(nearest) < 0) {
								nearest = collision;
								nearestNode = chosen;
							}
						}
					}
				} else {
					stack.add(l);
				}
			}
			if (r != null && r.bounding.intersectsRay(start, end)) {
				if (r.isLeaf()) {
					Node chosen = r;
					if (context.canCollide(chosen.bounding)) {
						Collision collision = chosen.bounding.raytrace(start, end, context);
						if (collision != null) {
							if (nearest == null || collision.compareTo(nearest) < 0) {
								nearest = collision;
								nearestNode = chosen;
							}
						}
					}
				} else {
					stack.add(r);
				}
			}
		} while (!stack.isEmpty());
		//System.out.println("Iterations " + c);
		if (nearestNode == null) return null;
		return new NodeRayCollision<>(nearest.distance(), nearest.depth(), nearest.point(), nearest.normal(), nearest.direction(), nearestNode);
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

	private float unionCost(AABB a, AABB b) {
		double minX = Math.min(a.minX, b.minX);
		double minY = Math.min(a.minY, b.minY);
		double minZ = Math.min(a.minZ, b.minZ);
		double maxX = Math.max(a.maxX, b.maxX);
		double maxY = Math.max(a.maxY, b.maxY);
		double maxZ = Math.max(a.maxZ, b.maxZ);

		return (float) ((maxX - minX) + (maxY - minY) + (maxZ - minZ));
	}

	private float cost(AABB a) {
		return (float) ((a.maxX - a.minX) + (a.maxY - a.minY) + (a.maxZ - a.minZ)) * COST_RATIO;
	}

	private void checkValid(AABB a) {
		if (!a.isValid() || !a.isNormal()) {
			throw new IllegalArgumentException("Invalid " + a);
		}
	}

	private class Node implements TreeNode<T> {
		Node parent;
		Node left;
		Node right;
		AABB bounding;
		final T value;

		Node(AABB bounding, T value) {
			this.bounding = bounding;
			this.value = value;
		}

		@Override
		public boolean isLeaf() {
			return value != null;
		}

		private Node chooseNode(AABB newBounding, Node newNode) {
			float c = unionCost(newBounding, this.bounding) - cost(this.bounding);
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
			Node p = this.parent;

			double minX;
			double minY;
			double minZ;
			double maxX;
			double maxY;
			double maxZ;

			if (right == null) {
				if (left == null) {
					throw new IllegalStateException();
				}
				if (p != null) {
					if (p.right == this) {
						p.right = left;
						left.parent = p;
						return p;
					} else if (p.left == this) {
						p.left = left;
						left.parent = p;
						return p;
					}
				}
				AABB rect = left.bounding;
				minX = rect.minX;
				minY = rect.minY;
				minZ = rect.minZ;
				maxX = rect.maxX;
				maxY = rect.maxY;
				maxZ = rect.maxZ;
			} else if (left == null) {
				if (p != null) {
					if (p.right == this) {
						p.right = right;
						right.parent = p;
						return p;
					} else if (p.left == this) {
						p.left = right;
						right.parent = p;
						return p;
					}
				}
				AABB rect = right.bounding;
				minX = rect.minX;
				minY = rect.minY;
				minZ = rect.minZ;
				maxX = rect.maxX;
				maxY = rect.maxY;
				maxZ = rect.maxZ;
			} else {
				AABB a = left.bounding;
				AABB b = right.bounding;
				minX = Math.min(a.minX, b.minX);
				minY = Math.min(a.minY, b.minY);
				minZ = Math.min(a.minZ, b.minZ);
				maxX = Math.max(a.maxX, b.maxX);
				maxY = Math.max(a.maxY, b.maxY);
				maxZ = Math.max(a.maxZ, b.maxZ);
			}

			AABB union = bounding;

			if (!union.fullyContains(minX, minY, minZ, maxX, maxY, maxZ)) {
				this.bounding = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
				return p;
			}

			float up = cost(union);
			float np = (float) ((maxX - minX) + (maxY - minY) + (maxZ - minZ));
			if (up > np) {
				this.bounding = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
				return p;
			}
			return null;
		}

		@Override
		public void remove() {
			Node n = null;
			if (isLeaf()) {
				if (parent.left == this) {
					parent.left = null;
				} else if (parent.right == this) {
					parent.right = null;
				} else {
					throw new IllegalStateException("already removed");
				}
				if (parent.left == null && parent.right == null) {
					n = parent;
				} else {
					AABBTree.this.normalize(parent);
				}
			} else {
				n = this;
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
				AABBTree.this.normalize(p);
				return null;
			}
		}

		@Override
		public AABB getBounding() {
			return bounding;
		}

		@Override
		public void move(AABB newBounding) { // TODO
			checkValid(newBounding);
			if (this.bounding.equals(newBounding)) return;
			remove();
			this.bounding = newBounding;
			put0(newBounding, this);
		}

		@Override
		public T getValue() {
			return value;
		}
	}

	public interface TreeNode<T> extends Removable {

		boolean isLeaf();

		T getValue();

		AABB getBounding();

		void move(AABB newBounding);
	}

	public record NodeRayCollision<T>(double distance,
									  double depth,
									  Vec3 point,
									  Vec3 normal,
									  Direction direction,
									  AABBTree.TreeNode<T> node
	) {
		public Collision asStandardCollision() {
			return new Collision(distance, depth, normal, point, direction, node.getBounding(), null);
		}
	}
}
