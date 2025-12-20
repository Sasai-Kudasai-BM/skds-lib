package net.skds.lib2.network.intercom;

import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.io.ByteArrayExtendedDataOutput;
import net.skds.lib2.io.ExtendedDataInput;
import net.skds.lib2.io.ExtendedDataOutput;
import net.skds.lib2.network.AddressLocation;
import net.skds.lib2.network.NetworkDirection;
import net.skds.lib2.network.exception.ProtocolViolationException;
import net.skds.lib2.network.exception.ProtocolViolationIOException;
import net.skds.lib2.network.exception.WrongSideException;
import net.skds.lib2.network.intercom.packet.*;
import net.skds.lib2.network.intercom.packet.system.DisconnectPacket;
import net.skds.lib2.network.intercom.packet.system.PingPacket;
import net.skds.lib2.network.tcp.TCPConnection;
import net.skds.lib2.network.tcp.TCPConnectionOptions;
import net.skds.lib2.utils.SKDSUtils;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.DataFormatException;

@CustomLog
public abstract class IntercomConnection<C extends IntercomConnection<?>> extends TCPConnection<TCPConnectionOptions> {

	private final LinkedBlockingQueue<IntercomInputPacket<?>> inputPacketQueue = new LinkedBlockingQueue<>();
	private final LinkedBlockingQueue<IntercomOutputPacket> outputPacketQueue = new LinkedBlockingQueue<>();
	@Getter
	private final IntercomConnectionOwner<C, ?> connectionOwner;

	private ExtendedDataInput input = ExtendedDataInput.wrap(getInputStream());
	private ExtendedDataOutput output = ExtendedDataOutput.wrap(getOutputStream());

	private final ByteArrayExtendedDataOutput bundleOutputBuffer;
	private final ByteArrayExtendedDataOutput outputBuffer;
	private final int outputBufferSize;
	protected final IntercomSettings intercomSettings;
	@Getter
	private final AddressLocation addressLocation;

	private IntercomOutputPacket terminationPacket;

	@Setter(AccessLevel.PROTECTED)
	@Getter
	private SecurityLevel securityLevel;


	@Getter
	private HandshakeStatus handshakeStatus = HandshakeStatus.NONE;

	@Getter
	@Setter
	private boolean forceInstantHandle = true;
	@Setter
	@Getter
	private boolean isPacketErrorCritical = false;
	@Getter
	private int lastPing = 999;

	@Getter
	private boolean authorized = false;

	@Getter
	@Setter
	private PacketRegistry applicationPacketRegistry = _ -> null;

	private IntercomOutputPacket pendingPacket;

	protected IntercomEncryption encryption;

	public IntercomConnection(SocketChannel channel,
							  boolean isServerside,
							  IntercomConnectionOwner<C, ?> connectionOwner,
							  IntercomSettings intercomSettings,
							  TCPConnectionOptions options
	) {
		super(channel, isServerside, options);
		this.outputBufferSize = options.getOutputBufferSize();
		this.intercomSettings = intercomSettings;
		this.addressLocation = AddressLocation.getLocation(getAddress());
		this.outputBuffer = new ByteArrayExtendedDataOutput(outputBufferSize);
		this.bundleOutputBuffer = new ByteArrayExtendedDataOutput(outputBufferSize);
		this.connectionOwner = connectionOwner;
	}

	void setAuthorized() {
		this.authorized = true;
	}

	protected void setUnauthorized() {
		this.authorized = false;
	}

	public void send(IntercomOutputPacket packet) {
		if (isAlive()) {
			if (!validateSendSide(packet.getDirection()))
				throw new WrongSideException(packet.getDirection().toString());
			outputPacketQueue.offer(packet);
		}
	}

	public boolean validateSendSide(NetworkDirection side) {
		if (isServerside()) {
			return side != NetworkDirection.TO_SERVER;
		}
		return side != NetworkDirection.TO_CLIENT;
	}

	public boolean validateReceiveSide(NetworkDirection side) {
		if (!isServerside()) {
			return side != NetworkDirection.TO_SERVER;
		}
		return side != NetworkDirection.TO_CLIENT;
	}

	public synchronized void validateHandshakeStatus(HandshakeStatus required, HandshakeStatus next, AbstractIntercomPacket packet) {
		if (this.handshakeStatus != required) {
			throw new ProtocolViolationException("HandshakeStatus mismatch: requires %s but actually is %s for packet %s"
					.formatted(required, this.handshakeStatus, packet.getClass().getSimpleName()));
		}
		this.handshakeStatus = next;
	}

	public synchronized void validateHandshakeStatus(HandshakeStatus required, HandshakeStatus next) {
		if (this.handshakeStatus != required) {
			throw new ProtocolViolationException("HandshakeStatus mismatch: requires %s but actually is %s for next state %s"
					.formatted(required, this.handshakeStatus, next));
		}
		this.handshakeStatus = next;
	}

	public synchronized void validateHandshakeStatus(HandshakeStatus required) {
		if (this.handshakeStatus != required) {
			throw new ProtocolViolationException("HandshakeStatus mismatch: requires %s but actually is %s"
					.formatted(required, this.handshakeStatus));
		}
	}

	void enableEncryption() {
		IntercomEncryption e = this.encryption;
		this.input = ExtendedDataInput.wrap(e.wrapInput(getInputStream()));
		this.output = ExtendedDataOutput.wrap(e.wrapOutput(getOutputStream()));
	}

	protected abstract String getPublicInfo();

	public abstract void receivePublicInfo(String info);

	public void pong(long time) {
		this.lastPing = (int) (System.currentTimeMillis() - time);
	}

	public void ping() {
		send(new PingPacket(System.currentTimeMillis()));
	}

	@Override
	protected void safeClose() {
		super.safeClose();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void close() throws IOException {
		var dl = this.connectionOwner;
		if (dl != null) {
			//log.warn("disconnectListener in close");
			dl.onDisconnect((C) this);
			//this.connectionOwner = null;
		}
		super.close();
	}

	@SuppressWarnings("unchecked")
	public IntercomInputPacket<C> nextPacket(boolean wait) {
		try {
			return (IntercomInputPacket<C>) (wait ? inputPacketQueue.take() : inputPacketQueue.poll());
		} catch (InterruptedException e) {
			log.warn(e);
			return (IntercomInputPacket<C>) inputPacketQueue.poll();
		}
	}

	@SuppressWarnings("unchecked")
	private void readPacket(ExtendedDataInput input, boolean inBundle, boolean compressed) throws IOException {
		final PacketHeader header = PacketHeader.read(input);
		if (!header.type().isValidForStatus(getHandshakeStatus())) {
			throw new ProtocolViolationIOException("Packet type " + header.type() + " is not allowed for status " + getHandshakeStatus());
		}
		if (inBundle && !header.type().allowBundle) {
			throw new ProtocolViolationIOException("Bundle is not allowed for " + header.type());
		}
		if (compressed && header.compression()) {
			throw new ProtocolViolationIOException("Compressed in compressed");
		}
		//if (header.size() == 0) return;
		int uncompressed = header.decompressedSize();
		byte[] packetBody = input.readBytes(header.size());
		ExtendedDataInput in;
		if (header.compression()) {
			try {
				in = ExtendedDataInput.wrap(SKDSUtils.decompress(packetBody, uncompressed).array());
			} catch (DataFormatException e) {
				throw new RuntimeException(e);
			}
		} else {
			in = ExtendedDataInput.wrap(packetBody);
		}
		switch (header.type()) {
			case SYSTEM, SYSTEM_CRITICAL, APPLICATION -> {
				PacketRegistry registry = getPacketRegistry(header.type());
				try {
					PacketReader packetReader = registry.getPacketReader(header.id());
					if (packetReader == null) {
						log.warn("Unknown packet: " + header);
						return;
					}
					IntercomInputPacket<C> packet = (IntercomInputPacket<C>) packetReader.read(in, registry);
					if (!validateReceiveSide(packet.getDirection())) throw new WrongSideException(header.toString());
					if (forceInstantHandle || packet.isInstantHandle()) {
						packet.handle((C) this);
					} else {
						if (!connectionOwner.packetInbound((C) this, packet)) {
							inputPacketQueue.offer(packet);
						}
					}
					resetTimeout();
				} catch (Exception ex) {
					if (isPacketErrorCritical) {
						throw new IOException(ex);
					} else {
						log.warn("Exception while reading packet " + header + ": " + ex);
						ex.printStackTrace(SKDSLogger.ERROR_PRINTSTREAM);
					}
				}
			}
			case BUNDLE -> {
				int c = header.id(); // packet count in bundle
				for (int i = 0; i < c; i++) {
					readPacket(in, true, header.compression());
				}
			}
		}
	}

	protected void sendTerminationPacket(IntercomOutputPacket packet) {
		this.terminationPacket = packet;
		send(packet);
	}

	public final void disconnect(String message) {
		sendTerminationPacket(new DisconnectPacket(System.currentTimeMillis(), message));
	}

	public final void handleDisconnect(DisconnectPacket packet) {
		handleDisconnect(packet.getTime(), packet.getReason());
		safeClose();
	}

	public void handleDisconnect(long timestamp, String reason) {
		log.info("Disconnect: " + reason);
	}

	private PacketRegistry getPacketRegistry(PacketType type) {
		return switch (type) {
			case SYSTEM, SYSTEM_CRITICAL -> SystemPacketRegistry.INSTANCE;
			case APPLICATION -> applicationPacketRegistry;
			default -> throw new IllegalArgumentException("Invalid registry type " + type);
		};
	}

	@Override
	public void readInput() throws IOException {
		readPacket(this.input, false, false);
	}

	@Override
	public void doDataWrite(boolean isQueueEmpty) throws IOException {
		IntercomOutputPacket packet;
		final LinkedList<IntercomOutputPacket> bundle = new LinkedList<>();
		try {
			if (this.pendingPacket == null) {
				while ((packet = (isQueueEmpty && bundle.isEmpty()) ? outputPacketQueue.take() : outputPacketQueue.poll()) != null) {

					if (packet.packetType().allowBundle) {
						bundle.addLast(packet);
					} else {
						if (bundle.isEmpty()) {
							bundle.addLast(packet);
						} else {
							this.pendingPacket = packet;
						}
						break;
					}
				}
			} else {
				bundle.addLast(this.pendingPacket);
				this.pendingPacket = null;
			}
			final int bundleSize = bundle.size();
			if (bundleSize == 0) return;
			final ByteArrayExtendedDataOutput out = this.outputBuffer;
			final ByteArrayExtendedDataOutput bundleOut = this.bundleOutputBuffer;
			final boolean doBundle = intercomSettings.checkBundling(bundleSize);

			boolean terminate = false;
			while (!terminate && (packet = bundle.pollFirst()) != null) {
				if (packet == terminationPacket) {
					terminate = true;
				}
				packet.write(out);
				int uncompressed = -1;
				if (!doBundle && intercomSettings.checkCompression(out.size(), addressLocation)) { // try to compress
					ByteBuffer bb = SKDSUtils.compress(out.rawArray(), 0, out.size());
					if (bb.remaining() + 4 < out.size()) {
						uncompressed = out.size();
						out.reset();
						out.writeFromByteBuffer(bb);
					}
				}
				PacketHeader.write(bundleOut, packet.packetType(), packet.packetId(), out.size(), uncompressed);
				bundleOut.write(out.rawArray(), 0, out.size());
				out.resetAndCap(this.outputBufferSize);
			}

			if (doBundle) {
				int uncompressed = -1;
				if (intercomSettings.checkCompression(out.size(), addressLocation)) { // try to compress
					ByteBuffer bb = SKDSUtils.compress(bundleOut.rawArray(), 0, bundleOut.size());
					if (bb.remaining() + 4 < bundleOut.size()) {
						uncompressed = bundleOut.size();
						bundleOut.reset();
						bundleOut.writeFromByteBuffer(bb);
					}
				}
				PacketHeader.write(this.output, PacketType.BUNDLE, bundleSize, bundleOut.size(), uncompressed);
			}
			this.output.write(bundleOut.rawArray(), 0, bundleOut.size());
			bundleOut.resetAndCap(this.outputBufferSize);
			if (terminate) {
				setPacketTerminated();
			}
		} catch (InterruptedException ignored) {
		}
	}


}
