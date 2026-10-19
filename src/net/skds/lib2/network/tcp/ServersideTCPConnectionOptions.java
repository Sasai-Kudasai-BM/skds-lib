package net.skds.lib2.network.tcp;

public class ServersideTCPConnectionOptions extends TCPConnectionOptions {
	private int acceptTimeout = 1000;

	public int getAcceptTimeout() {
		return this.acceptTimeout;
	}

	public void setAcceptTimeout(final int acceptTimeout) {
		this.acceptTimeout = acceptTimeout;
	}
}
