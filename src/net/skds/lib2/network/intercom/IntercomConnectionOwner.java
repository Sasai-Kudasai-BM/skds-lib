package net.skds.lib2.network.intercom;

import net.skds.lib2.network.intercom.packet.IntercomInputPacket;
import net.skds.lib2.network.tcp.ConnectionOwner;
import net.skds.lib2.network.tcp.TCPConnectionOptions;

public interface IntercomConnectionOwner<C extends IntercomConnection<?>, O extends TCPConnectionOptions>
		extends ConnectionOwner<C, O> {

	SecurityOptions getSecurityOptions();

	String getName();

	default boolean packetInbound(C connection, IntercomInputPacket<C> packet) {
		return false;
	}
}
