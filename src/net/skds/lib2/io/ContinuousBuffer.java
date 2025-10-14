package net.skds.lib2.io;

import net.skds.lib2.utils.SKDSUtils;
import net.skds.lib2.utils.collection.ReusableReferenceCollection;

import java.nio.ByteBuffer;
import java.util.Objects;

public class ContinuousBuffer {


	private final int nodeSize;
	private final int maxNodes;

	//private final ReentrantLock lock = new ReentrantLock();

	private Node readNode;
	private Node writeNode;

	private long available;

	private final ReusableReferenceCollection<Node> freeNodes = new ReusableReferenceCollection<>();

	public ContinuousBuffer() {
		this(SKDSUtils.DEFAULT_BUFFER_SIZE, 0);
	}

	public ContinuousBuffer(int nodeSize) {
		this(nodeSize, 0);
	}

	public ContinuousBuffer(int nodeSize, int maxNodes) {
		this.nodeSize = nodeSize;
		this.maxNodes = maxNodes;
	}

	private Node getNewNode() {
		Node n = freeNodes.take();
		if (n != null) return n;
		return new Node();
	}

	public int putData(ByteBuffer buffer) {
		int bp = buffer.position();
		int put = putData(buffer, bp, buffer.remaining());
		buffer.position(bp + put);
		return put;
	}

	public int takeData(ByteBuffer buffer) {
		int bp = buffer.position();
		int take = takeData(buffer, bp, buffer.remaining());
		buffer.position(bp + take);
		return take;
	}

	public int putData(byte[] b, int offset, int count) {
		Objects.checkFromIndexSize(offset, count, b.length);
		int putCount = 0;
		Node n = this.writeNode;
		if (n == null) {
			n = getNewNode();
			this.writeNode = n;
			this.readNode = n;
		}
		while (putCount < count) {
			int wp = n.writePosition;
			int space = nodeSize - wp;
			int toPut = Math.min(space, count - putCount);
			if (toPut == 0) break; // limit
			System.arraycopy(b, offset + putCount, n.buffer, wp, toPut);
			n.writePosition = wp += toPut;
			putCount += toPut;
			if (wp == nodeSize) {
				Node rn = this.readNode;
				if (maxNodes > 0 && rn != null && n.index - rn.index >= maxNodes) {
					break; // limit
				}
				Node n2 = getNewNode();
				n.next = n2;
				n2.index = n.index + 1;
				n = n2;
				this.writeNode = n;
			}
		}
		this.available += putCount;
		return putCount;
	}

	public int putData(ByteBuffer buffer, int offset, int count) {
		Objects.checkFromIndexSize(offset, count, buffer.limit());
		int putCount = 0;
		Node n = this.writeNode;
		if (n == null) {
			n = getNewNode();
			this.writeNode = n;
			this.readNode = n;
		}
		while (putCount < count) {
			int wp = n.writePosition;
			int space = nodeSize - wp;
			int toPut = Math.min(space, count - putCount);
			if (toPut == 0)
				break; // limit
			buffer.get(offset + putCount, n.buffer, wp, toPut);
			n.writePosition = wp += toPut;
			putCount += toPut;
			if (wp == nodeSize) {
				Node rn = this.readNode;
				if (maxNodes > 0 && rn != null && n.index - rn.index >= maxNodes) {
					break; // limit
				}
				Node n2 = getNewNode();
				n.next = n2;
				n2.index = n.index + 1;
				n = n2;
				this.writeNode = n;
			}
		}
		this.available += putCount;
		return putCount;
	}

	private Node retireNode(Node n) {
		Node next = n.next;
		if (next == null) throw new IllegalStateException();
		n.index = 0;
		n.readPosition = 0;
		n.writePosition = 0;
		n.next = null;
		freeNodes.put(n);
		this.readNode = next;
		return next;
	}

	public int takeData(byte[] b, int offset, int count) {
		Objects.checkFromIndexSize(offset, count, b.length);
		Node n = this.readNode;
		if (n == null) return 0;
		int readCount = 0;
		while (readCount < count) {
			int rp = n.readPosition;
			if (rp == nodeSize) {
				// end of node
				n = retireNode(n);
				continue;
			}
			int available = n.writePosition - rp;
			if (available == 0) {
				break;
			}
			int bufferPos = offset + readCount;
			available = Math.min(b.length - bufferPos, available);
			if (available == 0) {
				break; // no more space in buffer
			}
			System.arraycopy(n.buffer, rp, b, offset + readCount, available);
			n.readPosition = rp + available;
			readCount += available;
		}
		this.available -= readCount;
		return readCount;
	}

	public int takeData(ByteBuffer b, int offset, int count) {
		Objects.checkFromIndexSize(offset, count, b.limit());
		Node n = this.readNode;
		if (n == null) return 0;
		int readCount = 0;
		while (readCount < count) {
			int rp = n.readPosition;
			if (rp == nodeSize) {
				// end of node
				n = retireNode(n);
				continue;
			}
			int available = n.writePosition - rp;
			if (available == 0) {
				break;
			}
			int bufferPos = offset + readCount;
			available = Math.min(b.limit() - bufferPos, available);
			if (available == 0) {
				break; // no more space in buffer
			}
			b.put(offset + readCount, n.buffer, rp, available);
			n.readPosition = rp + available;
			readCount += available;
		}
		this.available -= readCount;
		return readCount;
	}

	public long skipData(long count) {
		Node n = this.readNode;
		if (n == null) return 0;
		int readCount = 0;
		while (readCount < count) {
			int rp = n.readPosition;
			if (rp == nodeSize) {
				// end of node
				n = retireNode(n);
				continue;
			}
			int available = n.writePosition - rp;
			if (available == 0) {
				break;
			}
			n.readPosition = rp + available;
			readCount += available;
		}
		this.available -= readCount;
		return readCount;
	}

	public long available() {
		return available;
	}


	private class Node {
		final byte[] buffer;
		private int readPosition;
		private int writePosition;
		long index;
		Node next;

		private Node() {
			this.buffer = new byte[nodeSize];
		}
	}
}
