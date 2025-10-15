package net.skds.lib2.network;

import net.skds.lib2.utils.EnumHelper;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public enum AddressLocation implements EnumHelper<AddressLocation> {
	LOCALHOST,
	LAN,
	WEB;

	public static AddressLocation getLocation(InetSocketAddress address) {
		InetAddress adr = address.getAddress();

		if (adr.isLoopbackAddress()) {
			return LOCALHOST;
		}
		if (adr.isSiteLocalAddress()) {
			return LAN;
		}
		return WEB;
	}
}
