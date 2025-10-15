package net.skds.lib2.network.intercom.packet;

import net.skds.lib2.io.ExtendedDataOutput;
import net.skds.lib2.network.intercom.IntercomConnection;

import java.io.IOException;

public interface IntercomOutputPacket extends AbstractIntercomPacket {

	void write(ExtendedDataOutput output) throws IOException;

	int packetId();

	PacketType packetType();

	default void send(IntercomConnection<?> connection) {
		connection.send(this);
	}

	default void send(IntercomConnection<?>[] connections) {
		for (IntercomConnection<?> c : connections) {
			c.send(this);
		}
	}

	default void send(Iterable<IntercomConnection<?>> connections) {
		for (IntercomConnection<?> c : connections) {
			c.send(this);
		}
	}

}
