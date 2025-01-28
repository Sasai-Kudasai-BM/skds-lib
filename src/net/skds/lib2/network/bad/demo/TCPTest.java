package net.skds.lib2.network.bad.demo;

import net.skds.lib2.network.bad.*;
import net.skds.lib2.utils.SKDSByteBuf;
import net.skds.lib2.utils.ThreadUtils;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class TCPTest {

	public static class ClientConnection extends AbstractClientConnection<ClientConnection> {

		public ClientConnection(SocketChannel channel) {
			super(channel, 4096);
		}
	}

	public static class ServerConnection extends AbstractServerConnection<ServerConnection> {
		public ServerConnection(SocketChannel channel) {
			super(channel, 4096);
		}

		@Override
		protected InPacket<ServerConnection> createPacket(int id, SKDSByteBuf payload) {
			var sup = super.createPacket(id, payload);
			if (sup == null && id == 1) {
				return new DemoPacket(payload);
			}
			return sup;
		}
	}

	public static void main(String[] args) {

		BadTCPServer server = new BadTCPServer(ClientConnection::new, ThreadUtils.EXECUTOR, ThreadUtils.EXECUTOR);
		TCPClient<ServerConnection> client = new TCPClient<>(ServerConnection::new, ThreadUtils.EXECUTOR, ThreadUtils.EXECUTOR);

		InetSocketAddress address = new InetSocketAddress(1000);
		server.start(address);
		client.connect(address);

		for (int i = 0; i < 100; i++) {
			ThreadUtils.await(1_000);
			client.getConnection().sendPacket(new DemoPacket("ses" + ('0' + i)));
		}
		server.stop();
	}
}
