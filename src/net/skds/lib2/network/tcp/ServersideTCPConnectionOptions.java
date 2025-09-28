package net.skds.lib2.network.tcp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServersideTCPConnectionOptions extends TCPConnectionOptions {

	private int acceptTimeout = 1000;

}
