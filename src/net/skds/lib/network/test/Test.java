package net.skds.lib.network.test;

import net.skds.lib.network.AbstractConnection;
import net.skds.lib.network.AbstractNetServer;
import net.skds.lib.network.ServerConnection;
import net.skds.lib.utils.ThreadUtil;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Test {

	public static final int packetSize = 2 * 1024 * 1024;

	public static float ping;
	public static float time;

	public static int c;

	public static void main(String[] args) throws IOException {

		InetSocketAddress addr = new InetSocketAddress("0.0.0.0", 25565);
		AbstractNetServer server = new AbstractNetServer();
		server.start(addr, ThreadUtil::runTaskNewThread);

		addr = new InetSocketAddress("100.65.0.183", 10001);
		ServerConnection client = AbstractConnection.connectTo(addr, ThreadUtil::runTaskNewThread, packetSize);


		client.sendPacket(new TestPacket<>(1, 0, System.nanoTime()));

	}
}
