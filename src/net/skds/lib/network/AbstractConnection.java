package net.skds.lib.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public abstract class AbstractConnection<T extends AbstractConnection<T>> {

	public final SocketChannel channel;
	protected final int bufferSize;
	protected final ByteBuffer readBuffer;
	protected final ByteBuffer writeBuffer;
	public final Selector selector;
	protected byte[] remainingIn;
	protected byte[] remainingOut;
	protected OutPacket<T, ByteBuffer> remainingOutPacket;
	//protected ServerConnection server;
	protected final ConcurrentLinkedQueue<InPacket<T>> inputPackets = new ConcurrentLinkedQueue<>();
	protected final ConcurrentLinkedQueue<OutPacket<T, ByteBuffer>> outputPackets = new ConcurrentLinkedQueue<>();

	public AbstractConnection(SocketChannel channel, int bufferSize) {
		this.bufferSize = bufferSize;
		this.channel = channel;
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.readBuffer = ByteBuffer.allocate(bufferSize);
		this.writeBuffer = ByteBuffer.allocate(bufferSize).flip();
	}

	public void sendPacket(OutPacket<T, ByteBuffer> packet) {
		outputPackets.offer(packet);
	}

	protected InPacket<T> readPacket(int id, ByteBuffer payload) {
		throw new UnsupportedOperationException();
	}

	protected void onSelect(SelectionKey key) {
		if (key.isReadable()) {
			try {
				read(key);
			} catch (IOException e) {
				key.cancel();
				try {
					channel.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				throw new RuntimeException(e);
			}
		} else {
			key.cancel();
		}

	}

	@SuppressWarnings("unchecked")
	protected boolean flushPacket(OutPacket<T, ByteBuffer> packet) {
		writeBuffer.mark();
		try {
			int pos = writeBuffer.position();
			writeBuffer.position(pos + 4);
			writeBuffer.putInt(packet.getId());
			packet.write((T) this, writeBuffer);
			writeBuffer.putInt(pos, writeBuffer.position() - pos - 4);
			return true;
		} catch (BufferOverflowException e) {
			remainingOutPacket = packet;
			writeBuffer.reset();
		}
		return false;
	}

	protected void flushPackets() throws IOException {
		if (writeBuffer.remaining() == 0) {
			writeBuffer.clear();
			if (remainingOutPacket != null && !flushPacket(remainingOutPacket)) {
				throw new RuntimeException("packet" + remainingOutPacket.getId() + " is too big");
			}
			remainingOutPacket = null;
			OutPacket<T, ByteBuffer> packet;
			while ((packet = outputPackets.poll()) != null) {
				if (!flushPacket(packet)) {
					break;
				}
			}
			writeBuffer.flip();
		}
		channel.write(writeBuffer);
	}

	@SuppressWarnings("unchecked")
	public void read(SelectionKey key) throws IOException {
		readBuffer.clear();
		if (remainingIn != null) {
			readBuffer.put(remainingIn);
			remainingIn = null;
		}
		int bytes = channel.read(readBuffer);
		readBuffer.flip();
		readBuffer.mark();
		if (bytes < 1) {
			disconnect(key);
			return;
		}
		if (readBuffer.remaining() < 4) {
			readBuffer.reset();
			remainingIn = new byte[readBuffer.remaining()];
			readBuffer.get(remainingIn);
			return;
		}
		while (readBuffer.remaining() > 0) {
			readBuffer.mark();
			int size = readBuffer.getInt();
			if (size > bufferSize) {
				disconnect(key);
				return;
			}
			if (size > readBuffer.remaining()) {
				readBuffer.reset();
				remainingIn = new byte[readBuffer.remaining()];
				readBuffer.get(remainingIn);
				return;
			}
			int id = readBuffer.getInt();
			ByteBuffer payload = readBuffer.slice(readBuffer.position(), size - 4);
			readBuffer.position(readBuffer.position() + size - 4);
			InPacket<T> packet = readPacket(id, payload);
			if (!packet.instantHandle((T) this)) {
				inputPackets.offer(packet);
			}
		}
	}

	public static ServerConnection connectTo(InetSocketAddress address, Executor executor, int maxPacketSize) throws IOException {
		final SocketChannel sc = SocketChannel.open(address);
		System.out.println("connected");
		final ServerConnection server = new ServerConnection(sc, maxPacketSize);
		sc.configureBlocking(false);
		sc.register(server.selector, SelectionKey.OP_READ);

		final Socket socket = sc.socket();
		socket.setSendBufferSize(server.bufferSize);
		socket.setReceiveBufferSize(server.bufferSize);
		socket.setTcpNoDelay(true);
		socket.setSoTimeout(10 * 1000);
		executor.execute(server::loop);
		return server;
	}

	protected void disconnect(SelectionKey key) throws IOException {
		key.cancel();
		channel.close();
	}
}
