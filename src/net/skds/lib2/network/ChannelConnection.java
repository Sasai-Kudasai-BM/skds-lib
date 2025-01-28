package net.skds.lib2.network;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface ChannelConnection {

	SocketChannel getChannel();

	void read(SocketChannel channel) throws IOException;
}
