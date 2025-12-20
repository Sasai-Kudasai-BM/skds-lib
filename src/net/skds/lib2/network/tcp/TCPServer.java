package net.skds.lib2.network.tcp;

import lombok.CustomLog;
import net.skds.lib2.utils.SKDSUtils;
import net.skds.lib2.utils.ThreadUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@CustomLog
public class TCPServer<C extends TCPConnection<?>> implements ConnectionOwner<C, ServersideTCPConnectionOptions> {

	protected boolean running = false;
	//public final Selector selector;
	public final Selector monitirSelector;
	public final ServerSocketChannel server;
	protected final Object acceptAttachment = new Object();
	private final ServersideTCPConnectionOptions options;

	protected final String serverName;
	protected final TCPConnectionFactory<?, TCPServer<C>> connectionFactory;

	@SuppressWarnings("unchecked")
	public TCPServer(ServersideTCPConnectionOptions options,
					 String serverName,
					 TCPConnectionFactory<C, ? extends TCPServer<C>> connectionFactory
	) {
		this.options = options;
		this.serverName = serverName;
		this.connectionFactory = (TCPConnectionFactory<?, TCPServer<C>>) connectionFactory;
		try {
			this.monitirSelector = Selector.open();
			//this.selector = Selector.open();
			this.server = ServerSocketChannel.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	public void stop() {
		running = false;
		try {
			server.close();
			//selector.close();
			monitirSelector.close();
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
			socket.setSoTimeout(options.getAcceptTimeout());
			ThreadUtils.runNewThreadMainGroup(() -> {
				while (running) {
					try {
						monitirSelector.select(this::onSelectMonitor);
					} catch (Exception e) {
						e.printStackTrace(System.err);
					}
				}
			}, serverName + "-monitor");
			log.info("TCP Server \"" + serverName + "\" started");
		} catch (IOException e) {
			running = false;
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void onSelectMonitor(SelectionKey key) {
		if (key.isAcceptable()) {
			if (key.attachment() != acceptAttachment) {
				disconnectKey(key);
				return;
			}
			try {
				@SuppressWarnings("resource") final SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
				//key.cancel();
				//log.debug("[Monitor] accepting " + sc.getRemoteAddress());
				connectionFactory.createConnection(sc, this).thenAccept(cc -> {
					if (cc == null) {
						disconnectKey(key);
						return;
					}
					cc.validateServerside();
					onConnect((C) cc);
					cc.startThreads();
				}).exceptionally(SKDSUtils.getCatcher());
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
			log.debug("Disconnecting " + key);
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

	@Override
	public void onConnect(C connection) {

	}

	@Override
	public void onDisconnect(C connection) {

	}

	@Override
	public ServersideTCPConnectionOptions getOptions() {
		return options;
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
