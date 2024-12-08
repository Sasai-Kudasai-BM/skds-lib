package net.skds.lib.network_old;

import lombok.Setter;
import net.skds.lib.crypto.CryptoCodec;
import net.skds.lib.utils.SKDSByteBuf;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractConnection<T extends AbstractConnection<T>> {


	public final SocketChannel channel;
	protected final int bufferSize;
	protected final ByteBuffer readBuffer;
	protected final SKDSByteBuf readBufferWrapper;
	protected final ByteBuffer writeBuffer;
	protected final SKDSByteBuf writeBufferWrapper;
	protected byte[] remainingIn;
	protected final ConcurrentLinkedQueue<InPacket<T>> inputPackets = new ConcurrentLinkedQueue<>();
	protected final LinkedBlockingQueue<OutPacket<T, SKDSByteBuf>> outputPackets = new LinkedBlockingQueue<>();
	@Setter
	protected CryptoCodec cryptoCodec;

	public AbstractConnection(SocketChannel channel, int bufferSize) {
		this.bufferSize = bufferSize;
		this.channel = channel;
		this.readBuffer = ByteBuffer.allocate(bufferSize);
		this.readBufferWrapper = new SKDSByteBuf(readBuffer);
		this.writeBuffer = ByteBuffer.allocate(bufferSize).flip();
		this.writeBufferWrapper = new SKDSByteBuf(writeBuffer);
	}

	public boolean sendPacket(OutPacket<T, SKDSByteBuf> packet) {
		if (!isAlive()) {
			return false;
		}
		return outputPackets.add(packet);
	}

	public boolean isAlive() {
		return channel.isConnected();
	}

	protected abstract InPacket<T> createPacket(int id, SKDSByteBuf payload);


	@SuppressWarnings("unchecked")
	protected boolean writePacket(OutPacket<T, SKDSByteBuf> packet) {
		try {
			writeBuffer.clear();
			writeBuffer.position(4);
			writeBuffer.putInt(packet.getPacketId());
			packet.writePacket((T) this, writeBufferWrapper);
			CryptoCodec cc = cryptoCodec;
			if (cc != null) {
				writeBuffer.flip().position(4);
				byte[] payload = cc.decrypt(writeBuffer);
				writeBuffer.clear().position(4).put(payload);
			}
			writeBuffer.putInt(0, writeBuffer.position() - 4);
			writeBuffer.flip();
			return true;
		} catch (BufferOverflowException e) {
			return false;
		}
	}

	protected void flushPackets() throws IOException {
		if (writeBuffer.remaining() == 0) {
			try {
				OutPacket<T, SKDSByteBuf> packet = outputPackets.take();
				if (!writePacket(packet)) {
					throw new RuntimeException("packet " + packet.getPacketId() + " is too big");
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		channel.write(writeBuffer);
	}

	@SuppressWarnings("unchecked")
	public void read() throws IOException {
		readBuffer.clear();
		if (remainingIn != null) {
			readBuffer.put(remainingIn);
			remainingIn = null;
		}
		int bytes = channel.read(readBuffer);
		if (bytes < 1) {
			disconnect();
			return;
		}
		readBuffer.flip();
		readBuffer.mark();

		while (readBuffer.remaining() > 0) {
			if (readBuffer.remaining() < 4) {
				readBuffer.reset();
				remainingIn = new byte[readBuffer.remaining()];
				readBuffer.get(remainingIn);
				return;
			}
			readBuffer.mark();
			int size = readBuffer.getInt();
			if (size > bufferSize) {
				disconnect();
				return;
			}
			if (size > readBuffer.remaining()) {
				readBuffer.reset();
				remainingIn = new byte[readBuffer.remaining()];
				readBuffer.get(remainingIn);
				return;
			}
			ByteBuffer payload = readBuffer.slice(readBuffer.position(), size);
			readBuffer.position(readBuffer.position() + size);
			CryptoCodec cc = cryptoCodec;
			if (cc != null) {
				payload = ByteBuffer.wrap(cc.decrypt(payload));
			}
			int id = payload.getInt();
			InPacket<T> packet = createPacket(id, new SKDSByteBuf(payload));
			if (packet != null && !packet.instantHandle((T) this)) {
				inputPackets.offer(packet);
			}
		}
	}


	public abstract int getTimeout();


	protected void disconnect() throws IOException {
		channel.close();
	}
}
