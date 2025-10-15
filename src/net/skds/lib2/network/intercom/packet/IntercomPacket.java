package net.skds.lib2.network.intercom.packet;

import net.skds.lib2.network.intercom.IntercomConnection;

public interface IntercomPacket<C extends IntercomConnection<?>> extends IntercomOutputPacket, IntercomInputPacket<C> {
}
