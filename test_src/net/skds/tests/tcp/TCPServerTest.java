package net.skds.tests.tcp;

import net.skds.lib2.io.ExtendedDataInput;
import net.skds.lib2.io.ExtendedDataOutput;
import net.skds.lib2.misc.timer.SimpleTimer;
import net.skds.lib2.network.tcp.ClientsideTCPConnectionOptions;
import net.skds.lib2.network.tcp.ServersideTCPConnectionOptions;
import net.skds.lib2.network.tcp.TCPConnection;
import net.skds.lib2.network.tcp.TCPServer;
import net.skds.lib2.utils.Holders;
import net.skds.lib2.utils.SKDSUtils;
import net.skds.lib2.utils.ThreadUtils;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;

public class TCPServerTest {
	public static void main(String[] args) throws IOException {
		SKDSLogger.replaceOuts();

		Holders.IntHolder counter = new Holders.IntHolder();
		Holders.ObjectHolder<TCPConnection<?>> connection = new Holders.ObjectHolder<>();

		byte[] dataIn = new byte[1024 * 64];

		ServersideTCPConnectionOptions serversideOptions = new ServersideTCPConnectionOptions();
		ClientsideTCPConnectionOptions clientsideOptions = new ClientsideTCPConnectionOptions();

		serversideOptions.setInputBufferSize(2 << 16);
		clientsideOptions.setOutputBufferSize(2 << 16);

		TCPServer<TCPConnection<ServersideTCPConnectionOptions>> server = new TCPServer<>(
				serversideOptions,
				"TestServer",
				(sc, s) -> CompletableFuture.supplyAsync(() -> new TCPConnection<>(sc, true, s.getOptions()) {

					//final ExtendedDataInput input = ExtendedDataInput.wrap(getInputStream());
					final ExtendedDataOutput output = ExtendedDataOutput.wrap(getOutputStream());

					long read = 0;
					long readLast = 0;

					{
						ThreadUtils.await(1000); // think about

						SimpleTimer.INSTANCE.scheduleRelative(() -> {
							long r = read;
							System.out.println(SKDSUtils.memoryCompact(r - readLast));
							System.out.println(counter.getValue() + " " + connection.getValue().getOutputThread().getState());
							readLast = r;
							return isAlive() ? System.currentTimeMillis() + 1000 : -1;
						}, 1000);
					}

					@Override
					public void readInput() throws IOException {
						//read += input.readByteArray().length;
						read += getInputStream().read(dataIn);
						resetTimeout();
					}

					@Override
					public void doDataWrite(boolean isQueueEmpty) throws IOException {
						ThreadUtils.await(1000);
						output.writeSizedString("AMOGUS");
					}
				}, ThreadUtils.EXECUTOR)
		);


		server.start(new InetSocketAddress(8080));

		byte[] data = new byte[1024 * 64];

		SocketChannel channel = SocketChannel.open(new InetSocketAddress(8080));
		var cn = new TCPConnection<>(channel, false, clientsideOptions) {
			final ExtendedDataInput input = ExtendedDataInput.wrap(getInputStream());
			//final ExtendedDataOutput output = ExtendedDataOutput.wrap(getOutputStream());

			@Override
			public void readInput() throws IOException {
				System.out.println(input.readSizedString());
				resetTimeout();
			}

			@Override
			public void doDataWrite(boolean isQueueEmpty) throws IOException {
				//output.writeByteArray(data);
				getOutputStream().write(data);
				counter.increment();
			}
		};
		connection.setValue(cn);
		cn.startThreads();
	}
	// http://localhost:8080
}
