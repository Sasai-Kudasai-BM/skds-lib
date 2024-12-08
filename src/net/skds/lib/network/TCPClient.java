package net.skds.lib.network;


import lombok.Getter;
import lombok.Setter;
import net.sdteam.libmerge.Lib2Merge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.function.Function;

//TODO
@Lib2Merge
public class TCPClient<T extends AbstractServerConnection<?>> {

	private static final ConnectResult SUCCESS = new ConnectResult(true, null);

	public final Selector selector;
	protected boolean running = false;
	@Getter
	protected T connection;
	@Setter
	private Function<SocketChannel, T> connectionFactory;
	private final Executor inputExecutor, outputExecutor;

	public TCPClient(Function<SocketChannel, T> connectionFactory, Executor inputExecutor, Executor outputExecutor) {
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
						connection.disconnect();
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
				connection.disconnect();
			}
		}
	}

	public ConnectResult connect(InetSocketAddress address) {
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
			return SUCCESS;
		} catch (Exception e) {
			//e.printStackTrace();
			return new ConnectResult(false, e);
		}
	}

	public record ConnectResult(boolean success, Exception exception) {
	}
}
