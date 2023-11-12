package net.skds.lib.network;

public interface OutPacket<T extends AbstractConnection<T>, B> {
	public void write(T connection, B buffer);
	public int getId();
}
