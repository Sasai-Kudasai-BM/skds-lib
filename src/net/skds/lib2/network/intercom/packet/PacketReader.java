package net.skds.lib2.network.intercom.packet;

import net.skds.lib2.io.ExtendedDataInput;

import java.io.IOException;

@FunctionalInterface
public interface PacketReader {

	IntercomInputPacket<?> read(ExtendedDataInput input) throws IOException;

	default IntercomInputPacket<?> read(ExtendedDataInput input, PacketRegistry registry) throws IOException {
		return read(input);
	}

	static PacketReader advanced(Advanced advanced) {
		return new PacketReader() {
			@Override
			public IntercomInputPacket<?> read(ExtendedDataInput input) throws IOException {
				return null;
			}

			@Override
			public IntercomInputPacket<?> read(ExtendedDataInput input, PacketRegistry registry) throws IOException {
				return advanced.read(input, registry);
			}
		};
	}

	@FunctionalInterface
	interface Advanced {
		IntercomInputPacket<?> read(ExtendedDataInput input, PacketRegistry registry) throws IOException;
	}
}
