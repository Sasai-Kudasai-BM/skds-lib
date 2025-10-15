package net.skds.lib2.network.intercom.packet;

import net.skds.lib2.io.ExtendedDataInput;

import java.io.IOException;

@FunctionalInterface
public interface PacketReader {

	IntercomInputPacket<?> read(ExtendedDataInput input) throws IOException;

}
