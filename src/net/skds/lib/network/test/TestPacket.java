package net.skds.lib.network.test;

import lombok.AllArgsConstructor;
import net.skds.lib.network.AbstractConnection;
import net.skds.lib.network.InPacket;
import net.skds.lib.network.OutPacket;

import java.nio.ByteBuffer;

@AllArgsConstructor
public class TestPacket<T extends AbstractConnection<T>> implements InPacket<T>, OutPacket<T, ByteBuffer> {

	int msg;
	int size;
	long time;

	public TestPacket(ByteBuffer buffer) {
		msg = buffer.getInt();
		size = buffer.getInt();
		byte[] bytes = new byte[size];
		buffer.get(bytes);
		time = buffer.getLong();
	}

	@Override
	public boolean instantHandle(T cc) {
		switch (msg) {
			case 1 -> {
				System.out.println("testing...");
				for (int i = 0; i < 1000; i++) {
					cc.sendPacket(new TestPacket<>(3, 1024 * 8, System.nanoTime()));
				}
				cc.sendPacket(new TestPacket<>(5, 0, time));
			}
			case 3 -> System.out.println("testing " + ++Test.c + "/" + 1000);
			//case 4 -> cc.sendPacket(new TestPacket<>(5, 0, time));
			case 5 -> {
				System.out.println("time:%.2f".formatted((System.nanoTime() - time) / 1000000f));
				System.exit(0);
			}
			default -> {
			}
		}
		return true;
	}

	@Override
	public void apply(T cc) {
	}

	@Override
	public void write(T connection, ByteBuffer buffer) {
		buffer.putInt(msg);
		buffer.putInt(size);
		byte[] bytes = new byte[size];
		buffer.put(bytes);
		buffer.putLong(time);
	}

	@Override
	public int getId() {
		return 777;
	}

}
