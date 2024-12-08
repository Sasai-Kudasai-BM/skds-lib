package net.skds.lib.network_old;


import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class NetClient<T extends AbstractServerConnection<T>> {

	public final Selector selector;
	protected boolean running = false;
	@Getter
	protected T connection;
	@Setter
	private Function<SocketChannel, T> connectionFactory;
	private final Executor inputExecutor, outputExecutor;

	public NetClient(Function<SocketChannel, T> connectionFactory, Executor inputExecutor, Executor outputExecutor) {
		this.outputExecutor = outputExecutor;
		this.inputExecutor = inputExecutor;
		this.connectionFactory = connectionFactory;
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	protected void inputLoop() {
		while (running && connection.isAlive()) {
			try {
				selector.select(k -> {
					try {
						connection.read();
					} catch (IOException e) {
						try {
							connection.disconnect();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					}

				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void outputLoop() {
		while (running && connection.isAlive()) {
			try {
				connection.flushPackets();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean connect(InetSocketAddress address) {
		try {
			if (this.connection != null && this.connection.isAlive()) {
				running = false;
				this.connection.disconnect();
			}

			final SocketChannel channel = SocketChannel.open(address);
			channel.configureBlocking(false);
			final T serverConnection = connectionFactory.apply(channel);

			final Socket socket = channel.socket();
			socket.setTcpNoDelay(true);
			socket.setSoTimeout(serverConnection.getTimeout());

			channel.register(selector, SelectionKey.OP_READ, serverConnection);
			this.connection = serverConnection;
			running = true;
			inputExecutor.execute(this::inputLoop);
			outputExecutor.execute(this::outputLoop);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
