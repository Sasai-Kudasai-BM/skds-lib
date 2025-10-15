package net.skds.lib2.network.intercom.packet;

import net.skds.lib2.network.intercom.IntercomConnection;

public interface IntercomInputPacket<C extends IntercomConnection<?>> extends AbstractIntercomPacket {

	void handle(C connection);

	default boolean isInstantHandle() {
		return false;
	}
}
