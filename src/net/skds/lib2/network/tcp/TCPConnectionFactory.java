package net.skds.lib2.network.tcp;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface TCPConnectionFactory<C extends TCPConnection<?>, O extends ConnectionOwner<C, ?>> {

	CompletableFuture<C> createConnection(SocketChannel sc, O owner) throws IOException;

}
