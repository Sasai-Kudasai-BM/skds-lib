package net.skds.tests.tcp;

import net.skds.lib2.io.ExtendedDataInput;
import net.skds.lib2.io.ExtendedDataOutput;
import net.skds.lib2.misc.timer.SimpleTimer;
import net.skds.lib2.network.tcp.*;
import net.skds.lib2.utils.Holders;
import net.skds.lib2.utils.SKDSUtils;
import net.skds.lib2.utils.ThreadUtils;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;

public class TCPServerTest {
	public static void main(String[] args) throws IOException {
		SKDSLogger.replaceOuts();

		Holders.IntHolder counter = new Holders.IntHolder();
		Holders.ObjectHolder<ClientsideTCPConnection> connection = new Holders.ObjectHolder<>();

		TCPServer server = new TCPServer(
				new ServersideTCPConnectionOptions(),
				"TestServer",
				(sc, s) -> CompletableFuture.completedFuture(new ServersideTCPConnection(sc, s) {

					final ExtendedDataInput input = ExtendedDataInput.wrap(getInputStream());
					final ExtendedDataOutput output = ExtendedDataOutput.wrap(getOutputStream());

					long read = 0;
					long readLast = 0;

					{
						SimpleTimer.INSTANCE.scheduleRelative(() -> {
							long r = read;
							System.out.println(SKDSUtils.memoryCompact(r - readLast));
							System.out.println(counter.getValue() + " " + connection.getValue().getOutputThread().getState());
							readLast = r;
							return isAlive() ? System.currentTimeMillis() + 1000 : -1;
						}, 1000);
					}

					@Override
					public void readInput(InputStream inputStream) throws IOException {
						read += input.readByteArray().length;
						resetTimeout();
					}

					@Override
					public void doDataWrite(OutputStream outputStream) throws IOException {
						ThreadUtils.await(1000);
						output.writeSizedString("AMOGUS");
					}
				})
		);


		server.start(new InetSocketAddress(8080));

		byte[] data = new byte[1024 * 32 + 3463];

		SocketChannel channel = SocketChannel.open(new InetSocketAddress(8080));
		var cn = new ClientsideTCPConnection(channel, new ClientTCPConnectionOptions()) {
			final ExtendedDataInput input = ExtendedDataInput.wrap(getInputStream());
			final ExtendedDataOutput output = ExtendedDataOutput.wrap(getOutputStream());

			@Override
			public void readInput(InputStream inputStream) throws IOException {
				System.out.println(input.readSizedString());
				resetTimeout();
			}

			@Override
			public void doDataWrite(OutputStream outputStream) throws IOException {
				output.writeByteArray(data);
				counter.increment();
			}
		};
		connection.setValue(cn);
		cn.startThreads();
	}
	// http://localhost:8080
}
