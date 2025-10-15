package net.skds.lib2.network.intercom.packet;

@FunctionalInterface
public interface PacketRegistry {

	PacketReader getPacketReader(int id);

}
