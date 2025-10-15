package net.skds.lib2.network.intercom.packet;

import net.skds.lib2.network.NetworkDirection;

public interface AbstractIntercomPacket {

	default NetworkDirection getDirection() {
		return NetworkDirection.BOTH;
	}
}
