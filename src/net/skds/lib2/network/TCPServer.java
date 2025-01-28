package net.skds.lib2.network;

import lombok.CustomLog;
import net.skds.lib2.utils.ThreadUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.*;
import java.util.function.Function;


@CustomLog
public class TCPServer {

	protected boolean running = false;
	public final Selector selector;
	public final Selector monitirSelector;
	public final ServerSocketChannel server;
	protected final Object acceptAttachment = new Object();

	protected final String serverName;
	protected final Function<SocketChannel, ChannelConnection> connectionFactory;

	public TCPServer(String serverName, Function<SocketChannel, ChannelConnection> connectionFactory) {
		this.serverName = serverName;
		this.connectionFactory = connectionFactory;
		try {
			this.monitirSelector = Selector.open();
			this.selector = Selector.open();
			this.server = ServerSocketChannel.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		running = false;
		for (var k : selector.keys()) {
			try {
				k.channel().close();
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
		try {
			selector.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void start(InetSocketAddress address) {
		running = true;
		try {
			server.bind(address);
			server.configureBlocking(false);
			server.register(monitirSelector, SelectionKey.OP_ACCEPT, acceptAttachment);
			//server.register(selector, SelectionKey.OP_READ);

			final ServerSocket socket = server.socket();
			socket.setSoTimeout(5000);
			ThreadUtils.runNewThreadMainGroup(() -> {
				while (running) {
					try {
						monitirSelector.select(this::onSelectMonitor);
					} catch (Exception e) {
						e.printStackTrace(System.err);
					}
				}
			}, serverName + "-monitor");
			ThreadUtils.runNewThreadMainGroup(() -> {
				while (running) {
					try {
						selector.select(this::onSelect);
					} catch (Exception e) {
						e.printStackTrace(System.err);
					}
				}
			}, serverName + "-input");
		} catch (IOException e) {
			running = false;
			throw new RuntimeException(e);
		}
	}

	public void registerMain(ChannelConnection cs) {
		SocketChannel sc = cs.getChannel();
		try {
			sc.register(selector, SelectionKey.OP_READ, sc);
		} catch (ClosedChannelException e) {
			e.printStackTrace(System.err);
			disconnectSC(sc);
		}
	}

	protected void onSelectMonitor(SelectionKey key) {
		if (key.isAcceptable()) {
			if (key.attachment() != acceptAttachment) {
				disconnectKey(key);
				return;
			}
			try {

				@SuppressWarnings("resource") final SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
				log.debug("[Monitor] accepting " + sc.getRemoteAddress());

				final Socket socket = sc.socket();
				sc.configureBlocking(false);
				socket.setTcpNoDelay(true);
				socket.setSoTimeout(5000);
				sc.register(monitirSelector, SelectionKey.OP_READ, connectionFactory.apply(sc));
			} catch (IOException e) {
				e.printStackTrace(System.err);
				disconnectKey(key);
			}
		} else if (key.isReadable() && (key.attachment() instanceof ChannelConnection cr)) {
			try {
				cr.read((SocketChannel) key.channel());
			} catch (IOException e) {
				e.printStackTrace(System.err);
				disconnectKey(key);
			}
		} else {
			disconnectKey(key);
		}
	}

	protected void onSelect(SelectionKey key) {
		if (key.isReadable() && (key.attachment() instanceof ChannelConnection cr)) {
			try {
				cr.read((SocketChannel) key.channel());
			} catch (IOException e) {
				e.printStackTrace(System.err);
				disconnectKey(key);
			}
		} else {
			disconnectKey(key);
		}
	}

	public void disconnectKey(SelectionKey key) {
		try {
			key.cancel();
			key.channel().close();
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}

	public void disconnectSC(SocketChannel sc) {
		try {
			sc.close();
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}

	//protected void outputLoop(AbstractClientConnection<?> connection) {
	//	while (running && connection.isAlive()) {
	//		try {
	//			connection.flushPackets();
	//		} catch (IOException e) {
	//			connection.disconnect();
	//		}
	//	}
	//}
}
