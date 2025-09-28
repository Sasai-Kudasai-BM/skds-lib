package net.skds.lib2.network.tcp;

import lombok.CustomLog;
import lombok.Getter;
import net.skds.lib2.io.ContinuousBuffer;
import net.skds.lib2.utils.ArrayUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

@CustomLog
public abstract class TCPConnection<T extends TCPConnectionOptions> implements Closeable {

	@Getter
	private final SocketChannel channel;
	@Getter
	private final SocketAddress address;
	@Getter
	protected final T options;
	private final ByteBuffer directInputBuffer;
	private final ByteBuffer directOutputBuffer;
	private final ContinuousBuffer inputData;
	private final ContinuousBuffer outputData;
	@Getter
	private final InputStream inputStream;
	@Getter
	private final OutputStream outputStream;
	@Getter
	private Thread inputThread;
	@Getter
	private Thread outputThread;
	private long lastTalk = 0;


	public TCPConnection(SocketChannel channel, T options) {
		this.channel = channel;
		this.options = options;
		try {
			this.address = channel.getRemoteAddress();
			int inputSize = options.getInputBufferSize();
			channel.socket().setReceiveBufferSize(inputSize);
			this.directInputBuffer = ByteBuffer.allocateDirect(inputSize).flip();
			this.inputData = new ContinuousBuffer(inputSize, options.getMaxInputBufferSize() / inputSize);
			int outputSize = options.getOutputBufferSize();
			channel.socket().setSendBufferSize(outputSize);
			this.directOutputBuffer = ByteBuffer.allocateDirect(outputSize).flip();
			this.outputData = new ContinuousBuffer(outputSize, -1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.inputStream = new InStr();
		this.outputStream = new OutStr();
	}

	public final void startThreads() {
		this.inputThread = startInputThread();
		this.outputThread = startOutputThread();
	}

	protected Thread startInputThread() {
		return Thread.ofPlatform().name(getClass().getSimpleName() + "-" + address + "-in-", 0).start(this::readLoop);
	}

	protected Thread startOutputThread() {
		return Thread.ofPlatform().name(getClass().getSimpleName() + "-" + address + "-out-", 0).start(this::writeLoop);
	}

	protected long checkTimeout() {
		if (!isAlive()) return -1;
		if (lastTalk + options.getSilenceTimeout() < System.currentTimeMillis()) {
			try {
				timeoutDisconnect();
			} catch (IOException e) {
				e.printStackTrace(System.err);
			}
			return -1;
		}
		return lastTalk + options.getSilenceTimeout();
	}

	public boolean isAlive() {
		return channel.isConnected();
	}

	protected void timeoutDisconnect() throws IOException {
		log.warn("Timeout disconnect " + this);
		channel.close();
	}

	protected void resetTimeout() {
		this.lastTalk = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + (isAlive() ? " [connected] " : " ") + address;
	}

	@Override
	public void close() throws IOException {
		log.info("Disconnect " + channel);
		channel.close();
	}

	public void readLoop() {
		try {
			while (isAlive()) {
				readInput(inputStream);
			}
		} catch (IOException e) {
			safeClose();
		}
	}

	protected void safeClose() {
		try {
			if (isAlive()) close();
		} catch (IOException ignored) {
		}
	}

	public void writeLoop() {
		try {
			while (isAlive()) {
				if (directOutputBuffer.hasRemaining()) {
					log.debug("Output buffer overflow");
					// skip data write
					channel.write(directOutputBuffer);
					break;
				}
				if (outputData.available() < directOutputBuffer.capacity()) {
					doDataWrite(outputStream);
				}
				outputData.takeData(directOutputBuffer.clear());
				channel.write(directOutputBuffer.flip());
			}
		} catch (IOException e) {
			safeClose();
		}
	}

	public abstract void readInput(InputStream input) throws IOException;

	public abstract void doDataWrite(OutputStream output) throws IOException;

	protected int doRead(byte[] b, int off, int len) throws IOException {
		int read = 0;
		while (read < len) {
			read += inputData.takeData(b, off + read, len - read);
			if (read < len) {
				if (directInputBuffer.hasRemaining()) {
					log.debug("Input buffer overflow");
					// skip socket read
					inputData.putData(directInputBuffer);
					continue;
				}
				directInputBuffer.clear();
				int i = channel.read(directInputBuffer);
				if (i > 0) {
					directInputBuffer.flip();
					inputData.putData(directInputBuffer);
				} else if (i < 0) {
					safeClose();
				} else {
					log.warn("shit");
				}
			}
		}
		return read;
	}

	private class InStr extends InputStream {

		private final byte[] buf = {0};

		@Override
		public int read() throws IOException {
			int r = read(buf, 0, 1);
			if (r == 0) {
				return -1;
			}
			return buf[0] & 0xff;
		}

		@Override
		public int available() {
			return (int) inputData.available();
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return doRead(b, off, len);
		}

		@Override
		public byte[] readAllBytes() throws IOException {
			int available = available();
			if (available == 0) return ArrayUtils.EMPTY_BYTE;
			byte[] data = new byte[available];
			int i = read(data);
			if (i < data.length) data = Arrays.copyOf(data, i);
			return data;
		}

		@Override
		public void close() {
			TCPConnection.this.safeClose();
		}
	}

	private class OutStr extends OutputStream {

		private final byte[] buf = {0};

		@Override
		public void write(int b) {
			buf[0] = (byte) b;
			write(buf, 0, 1);
		}

		@Override
		public void write(byte[] b, int off, int len) {
			int put = outputData.putData(b, off, len);
			if (put < len) {
				throw new IllegalStateException();
			}
		}

		@Override
		public void close() {
			TCPConnection.this.safeClose();
		}
	}


}
