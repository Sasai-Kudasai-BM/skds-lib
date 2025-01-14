package net.skds.lib2.misc.font;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class FontTriangulator {
	private List<Contur> conturs = new ArrayList<>(5);
	private ArrayList<Node> inters = new ArrayList<>(4);
	private ArrayList<Node> pidodstvo = new ArrayList<>(4);

	public boolean debug = false;

	private short[] triangs;
	private int tri;
	private int nodes = 0;
	private Node n;
	private int nodesteps;
	private int lastContur;

	public FontTriangulator() {
	}

	public void nextContur() {
		conturs.add(0, new Contur());
	}

	public void addNode(short x, short y) {
		Contur cont = conturs.get(0);
		Node node = new Node(x, y, cont);
		nodes++;
		cont.nodes++;
		if (cont.node == null) {
			cont.node = node;
			node.next = node;
			node.prev = node;
		} else {

			node.next = cont.node;
			node.prev = cont.node.prev;
			node.prev.next = node;
			node.next.prev = node;
		}
	}

	public short[] triangulate(int index) {
		long t = System.nanoTime();
		triangulateStart();
		int i = 0;
		for (; triangulateStep() && i < 999; i++)
			;
		if (i >= 999) {
			//System.out.println("pizdeс999 " + index); TODO
			return new short[0];
		} else {
			t = System.nanoTime() - t;
			if (debug)
				System.out.printf("#%s triangles:%s, time:%sus, (%s gps), %s steps\n", index, tri, t / 1000D, (long) 1E9 / t, i);
		}
		return Arrays.copyOf(triangs, tri * 6);
	}

	public void triangulateStart() {
		int k = nodes + conturs.size();
		triangs = new short[(k + k / 2) * 6];
		n = conturs.get(0).node;
		n.contur.selfMerge();
		forAllNodes(this::scanline);
	}

	public boolean triangulateStep() {

		if (tri * 6 >= triangs.length) {
			return false;
		}

		if (n.next == n.prev) {
			conturs.remove(n.contur);
			if (!next()) {
				return false;
			}
		}

		short x1 = n.x;
		short y1 = n.y;
		short x2 = n.next.x;
		short y2 = n.next.y;
		short x3 = n.next.next.x;
		short y3 = n.next.next.y;

		if (isCw(x1, y1, x2, y2, x3, y3)) {
			if (addTriangle(n, n.next, n.next.next) || pidodstvo.size() == 0) {
				n.next.remove();
				nodesteps--;
			} else {
				for (Node pidod : pidodstvo) {
					if (pidod.contur.merged == null && nonIntersectAll(n.next, pidod)) {
						n.next.merge(pidod);
						break;
					}
				}
				n = n.next;
				nodesteps++;
			}
		} else {
			n = n.next;
			nodesteps++;
		}

		if (nodesteps > n.contur.nodes) {
			next();
		}
		return true;

	}

	public static boolean isCw(short x1, short y1, short x2, short y2, short x3, short y3) {
		return cross(x2 - x1, y2 - y1, x3 - x2, y3 - y2) <= 0;
	}

	private boolean next() {
		conturs.removeIf(contur -> contur.node.next == contur.node.prev);
		for (int i = 0; i < conturs.size(); i++) {
			Contur contur = conturs.get((i + lastContur) % conturs.size());
			if (contur != n.contur) {
				n.contur.unmergeSelf();
				n = contur.node;
				contur.selfMerge();
				nodesteps = 0;
				lastContur = i;
				return true;
			}
		}
		return false;
	}

	private static int sign(Node p1, Node p2, Node p3) {
		return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
	}

	private static boolean pointInTriangle(Node pt, Node v1, Node v2, Node v3) {
		int d1, d2, d3;
		boolean hasNeg, hasPos;

		d1 = sign(pt, v1, v2);
		d2 = sign(pt, v2, v3);
		d3 = sign(pt, v3, v1);

		hasNeg = (d1 < 0) || (d2 < 0) || (d3 < 0);
		hasPos = (d1 > 0) || (d2 > 0) || (d3 > 0);

		return !(hasNeg && hasPos);
	}

	private void scanline(Node n) {
		inters.clear();
		forAllNodes(nn -> {
			if (nn.eq(n) || nn.next.eq(n)) {
				return;
			}
			short ax = nn.x;
			short ay = nn.y;
			short bx = nn.next.x;
			short by = nn.next.y;
			if (lineIntersectsSL(ax, ay, bx, by, n.x, n.y)) {
				inters.add(nn);
			}
		});
		//*
		if (inters.size() == 1) {
			n.isInside = true;
		} else if (inters.size() > 1) {
			inters.sort((a, b) -> sorter(a, b, n.y));
			Node last = inters.get(0);
			n.isInside = last.y < last.next.y;
		}

		if (n.isInside) {
			n.isInside = isCw(n);
		}
		// */
	}

	private static int sorter(Node a, Node b, short y) {
		float ax = a.x;
		float ay = a.y;
		float bx = a.next.x;
		float by = a.next.y;

		float x0 = (-ax * y - ax * by - bx * y + bx * ay) / (ay - by);
		ax = b.x;
		ay = b.y;
		bx = b.next.x;
		by = b.next.y;
		float x1 = (-ax * y - ax * by - bx * y + bx * ay) / (ay - by);

		return (int) (x0 - x1);
	}

	private boolean nonIntersectAll(Node a, Node b) {
		for (Contur cont : conturs) {
			if (!nonIntersect0(a, b, cont.node)) {
				return false;
			}
		}
		return true;
	}

	private boolean nonIntersect0(Node a, Node b, Node conturStart) {
		return nonIntersect0(a.x, a.y, b.x, b.y, conturStart);
	}

	private boolean nonIntersect0(short ax1, short ay1, short bx1, short by1, Node conturStart) {

		Node n = conturStart;
		short lx1 = n.x;
		short ly1 = n.y;
		short lx2 = n.next.x;
		short ly2 = n.next.y;
		if (linesIntersect(ax1, ay1, bx1, by1, lx1, ly1, lx2, ly2)) {
			return false;
		}
		while ((n = n.next) != conturStart) {
			lx1 = n.x;
			ly1 = n.y;
			lx2 = n.next.x;
			ly2 = n.next.y;
			if (linesIntersect(ax1, ay1, bx1, by1, lx1, ly1, lx2, ly2)) {
				return false;
			}
		}
		return true;
	}

	private static boolean fromDifferentSides(int dx, int dy, int vx1, int vy1, int vx2, int vy2) {
		float product1 = cross(dx, dy, vx1, vy1);
		float product2 = cross(dx, dy, vx2, vy2);
		return (product1 > 0 && product2 < 0) || (product1 < 0 && product2 > 0);
	}

	private static boolean linesIntersect(short ax1, short ay1, short ax2, short ay2, short bx1, short by1, short bx2,
										  short by2) {
		if (fromDifferentSides(ax2 - ax1, ay2 - ay1, ax2 - bx1, ay2 - by1, ax2 - bx2, ay2 - by2)) {
			return fromDifferentSides(bx2 - bx1, by2 - by1, bx2 - ax1, by2 - ay1, bx2 - ax2, by2 - ay2);
		}
		return false;
	}

	private boolean addTriangle(Node a, Node b, Node c) {
		pidodstvo.clear();
		if (a.eq(b) || b.eq(c) || c.eq(a)) {
			return false;
		}
		if ((c.x * (b.y - a.y) - c.y * (b.x - a.x) == a.x * b.y - b.x * a.y)) {
			return false;
		}

		forAllNodes(n -> {
			if (n.eq(a) || n.eq(b) || n.eq(c)) {
				return;
			}
			if (!n.isInside && pointInTriangle(n, a, b, c)) {
				pidodstvo.add(n);
			}
		});

		if (pidodstvo.size() > 0) {
			return false;
		}

		triangs[tri * 6] = a.x;
		triangs[tri * 6 + 1] = a.y;
		triangs[tri * 6 + 2] = b.x;
		triangs[tri * 6 + 3] = b.y;
		triangs[tri * 6 + 4] = c.x;
		triangs[tri * 6 + 5] = c.y;
		tri++;
		return true;
	}

	private static boolean isCw(Node n) {
		return isCw(n.prev.x, n.prev.y, n.x, n.y, n.next.x, n.next.y);
	}

	private void forAllNodes(Consumer<Node> nodeConsumar) {
		for (Contur contur : conturs) {
			forNodes(contur, nodeConsumar);
		}
	}

	private void forNodes(Contur start, Consumer<Node> nodeConsumar) {
		Node n = start.node;
		nodeConsumar.accept(n);
		while ((n = n.next) != start.node) {
			nodeConsumar.accept(n);
		}
	}

	private static int cross(int x1, int y1, int x2, int y2) {
		return x1 * y2 - y1 * x2;
	}

	private static boolean lineIntersectsSL(short lx1, short ly1, short lx2, short ly2, short px, short py) {
		if (lx1 > px && lx2 > px) {
			return false;
		}
		if (ly1 > py && ly2 > py) {
			return false;
		}
		if (ly1 < py && ly2 < py) {
			return false;
		}
		int cross = cross(lx2 - lx1, ly2 - ly1, px - lx2, py - ly2);
		return ly2 < ly1 ? cross >= 0 : cross <= 0;
	}

	private class Contur {
		int nodes;
		Node node;
		Contur merged = null;

		private boolean setMerged(Contur merge) {
			if (this.merged == null && merge != this) {
				conturs.remove(this);
				this.nodes += merge.nodes;
				merge.nodes = this.nodes;
				this.merged = merge;
				return true;
			}
			return false;
		}

		private void unmergeSelf() {
			if (this.merged == this) {
				this.merged = null;
			}
		}

		private void selfMerge() {
			if (this.merged == null) {
				this.merged = this;
			}
		}
	}

	private class Node {
		short x, y;
		Node next;
		Node prev;
		boolean isInside = false;
		final Contur contur;

		private Node(short x, short y, Contur contur) {
			this.x = x;
			this.y = y;
			this.contur = contur;
		}

		private void remove() {
			contur.nodes--;

			for (Contur cnt : conturs) {
				if (cnt.node == this) {
					cnt.node = next;
				}
				if (cnt.merged != null && cnt.merged.node == this) {
					cnt.merged.node = next;
				}
			}
			if (this == n) {
				n = next;
			}
			next.prev = prev;
			prev.next = next;

			//this.next = this;
			//this.prev = this;
		}

		private void merge(Node incertion) {
			if (!incertion.contur.setMerged(contur)) {
				throw new RuntimeException("pisis");
			}

			Node nn = new Node(x, y, contur);
			Node ne = new Node(incertion.x, incertion.y, contur);
			nodes += 2;

			ne.next = nn;
			nn.prev = ne;
			nn.next = this.next;
			ne.prev = incertion.prev;
			this.next.prev = nn;
			this.next = incertion;
			incertion.prev.next = ne;
			incertion.prev = this;
		}

		private boolean eq(Node n) {
			if (n == this) {
				return true;
			} else {
				return x == n.x && y == n.y;
			}
		}
	}
}
