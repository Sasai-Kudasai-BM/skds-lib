package net.skds.lib2.network;

import lombok.Getter;
import net.skds.lib2.utils.SKDSByteBuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class ChannelConnection {

	@Getter
	private final SocketChannel channel;
	private SKDSByteBuf buffer;

	public ChannelConnection(SocketChannel channel, int bufferSize) {
		this.channel = channel;
		this.buffer = SKDSByteBuf.allocate(bufferSize);
	}

	public void setBufferSize(int bufferSize) {
		SKDSByteBuf b = SKDSByteBuf.allocate(bufferSize);
		//b.write(this.buffer.getBuffer().array());
		b.flip();
		this.buffer = b;
	}

	void read(SocketChannel channel) throws IOException {
		var sb = this.buffer;
		ByteBuffer bb = sb.getBuffer();
		bb.clear();
		int i = channel.read(bb);
		if (i < 0) {
			disconnect();
		} else if (i > 0) {
			bb.flip();
			processData(sb);
		}
	}

	abstract protected void processData(SKDSByteBuf buffer);

	abstract public void disconnect() throws IOException;
}
