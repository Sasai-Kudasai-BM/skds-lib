package net.skds.lib.network;

import net.skds.lib.network.test.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;

public class AbstractNetServer {

	protected boolean running = false;
	public final Selector selector;
	public final ServerSocketChannel server;
	protected final Object acceptAttachment = new Object();

	public AbstractNetServer() {
		try {
			this.selector = Selector.open();
			this.server = ServerSocketChannel.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void start(InetSocketAddress address, Executor executor) {
		running = true;
		try {
			server.bind(address);
			server.configureBlocking(false);
			server.register(selector, SelectionKey.OP_ACCEPT, acceptAttachment);

			final ServerSocket socket = server.socket();
			socket.setReceiveBufferSize(getBufferSize());
			socket.setSoTimeout(10 * 1000);
			executor.execute(this::loop);
		} catch (IOException e) {
			running = false;
			throw new RuntimeException(e);
		}
	}

	protected int getBufferSize() {
		return Test.packetSize;
	}

	protected void onSelect(SelectionKey key) {
		if (key.isAcceptable()) {
			if (key.attachment() != acceptAttachment) {
				key.cancel();
				return;
			}
			try {
				final SocketChannel sc = server.accept();
				sc.configureBlocking(false);
				final Socket socket = sc.socket();
				socket.setSendBufferSize(getBufferSize());
				socket.setReceiveBufferSize(getBufferSize());
				socket.setTcpNoDelay(true);
				socket.setSoTimeout(10 * 1000);
				sc.register(selector, SelectionKey.OP_READ, new ClientConnection(sc, getBufferSize()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else if (key.isReadable() && (key.attachment() instanceof ClientConnection cc)) {
			try {
				cc.read(key);
			} catch (IOException e) {
				key.cancel();
				try {
					cc.channel.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				throw new RuntimeException(e);
			}
		} else {
			key.cancel();
		}

	}

	protected void loop() {
		while (running) {
			try {
				selector.select(this::onSelect, 5);
				selector.keys().forEach(key -> {
					if (key.attachment() instanceof ClientConnection cc) {
						try {
							cc.flushPackets();
						} catch (IOException e) {
							e.printStackTrace();
							try {
								cc.disconnect(key);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
