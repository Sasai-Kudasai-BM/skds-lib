package net.skds.lib2.network.tcp;

import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;

public interface TCPConnectionFactory<C extends TCPConnection<?>, O extends ConnectionOwner<C, ?>> {

	CompletableFuture<C> createConnection(SocketChannel sc, O owner);

}
