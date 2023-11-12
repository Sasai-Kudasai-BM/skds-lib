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

		AbstractNetServer server = null;
		ServerConnection client = null;

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.contains("server=") && server == null) {
				String value = arg.substring(arg.indexOf('=') + 1, arg.length() - 1);
				InetSocketAddress addr = new InetSocketAddress("0.0.0.0", Integer.valueOf(value));
				server = new AbstractNetServer();
				server.start(addr, ThreadUtil::runTaskNewThread);
			}
			if (arg.contains("client=") && client == null) {
				String value = arg.substring(arg.indexOf('=') + 1, arg.length() - 1);
				String[] valueArr = value.split(":");
				InetSocketAddress addr = new InetSocketAddress(valueArr[0], Integer.valueOf(valueArr[1]));
				client = AbstractConnection.connectTo(addr, ThreadUtil::runTaskNewThread, packetSize);
			}
		}

		if (client != null) {
			client.sendPacket(new TestPacket<>(1, 0, System.nanoTime()));
		}
	}
}
