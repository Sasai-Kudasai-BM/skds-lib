package net.skds.lib2.network.tcp;

import lombok.CustomLog;
import lombok.Getter;
import net.skds.lib2.io.ContinuousBuffer;
import net.skds.lib2.misc.timer.SimpleTimer;
import net.skds.lib2.network.exception.WrongSideException;
import net.skds.lib2.utils.ArrayUtils;
import net.skds.lib2.utils.ThreadUtils;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

@CustomLog
public abstract class TCPConnection<O extends TCPConnectionOptions> implements Closeable {

	@Getter
	private final SocketChannel channel;
	@Getter
	private final InetSocketAddress address;
	@Getter
	protected final O options;
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

	@Getter
	private final boolean serverside;

	@Getter
	private boolean packetTerminated = false;

	private boolean threadsStarted = false;

	private boolean closed = false;

	public TCPConnection(SocketChannel channel, boolean isServerside, O options) {
		this.serverside = isServerside;
		this.channel = channel;
		this.options = options;
		try {
			this.address = (InetSocketAddress) channel.getRemoteAddress();
			int inputSize = options.getInputBufferSize();
			channel.socket().setReceiveBufferSize(inputSize);
			this.directInputBuffer = ByteBuffer.allocateDirect(inputSize).flip();
			this.inputData = new ContinuousBuffer(inputSize, options.getMaxInputBufferSize() / inputSize);
			int outputSize = options.getOutputBufferSize();
			channel.socket().setSendBufferSize(outputSize);
			this.directOutputBuffer = ByteBuffer.allocateDirect(outputSize).flip();
			this.outputData = new ContinuousBuffer(outputSize, -1);

			final Socket socket = channel.socket();
			socket.setTcpNoDelay(true);
			socket.setSoTimeout(5000);
			SimpleTimer.INSTANCE.scheduleRelative(this::checkTimeout, options.getSilenceTimeout());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.inputStream = new InStr();
		this.outputStream = new OutStr();
	}

	public final void startThreads() {
		if (threadsStarted) throw new IllegalStateException("Threads has been already started");
		threadsStarted = true;
		this.inputThread = startInputThread();
		this.outputThread = startOutputThread();
	}

	protected Thread startInputThread() {
		return Thread.ofPlatform().group(ThreadUtils.IO_GROUP).name(getClass().getSimpleName() + "-" + address + "-in-", 0).start(this::readLoop);
	}

	protected Thread startOutputThread() {
		return Thread.ofPlatform().group(ThreadUtils.IO_GROUP).name(getClass().getSimpleName() + "-" + address + "-out-", 0).start(this::writeLoop);
	}

	public void validateServerside() {
		if (!serverside)
			throw new WrongSideException("Connection [%s] expected to be serverside but it is clientside".formatted(this));
	}

	public void validateClientside() {
		if (serverside)
			throw new WrongSideException("Connection [%s] expected to be clientside but it is serverside".formatted(this));
	}


	protected void setPacketTerminated() {
		this.packetTerminated = true;
	}

	protected long checkTimeout() {
		if (!isAlive()) return -1;
		if (lastTalk + options.getSilenceTimeout() < System.currentTimeMillis()) {
			try {
				timeoutDisconnect();
			} catch (IOException e) {
				e.printStackTrace(SKDSLogger.ERROR_PRINTSTREAM);
			}
			return -1;
		}
		return lastTalk + options.getSilenceTimeout();
	}

	public boolean isAlive() {
		return channel.isConnected() && !closed;
	}

	protected void timeoutDisconnect() throws IOException {
		log.warn("Timeout disconnect " + this);
		safeClose();
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
		log.log("Disconnect " + channel);
		if (closed) throw new IllegalStateException();
		closed = true;
		channel.close();
	}

	public void readLoop() {
		try {
			while (isAlive()) {
				readInput();
			}
		} catch (ClosedChannelException | SocketException closed) {
			safeClose();
		} catch (Exception e) {
			e.printStackTrace(SKDSLogger.ERROR_PRINTSTREAM);
			safeClose();
		}
	}

	protected synchronized void safeClose() {
		try {
			if (isAlive()) {
				close();
			}
		} catch (IOException e) {
			e.printStackTrace(SKDSLogger.ERROR_PRINTSTREAM);
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
				if (packetTerminated) {
					safeClose();
					return;
				}
				if (outputData.available() < directOutputBuffer.capacity()) {
					doDataWrite(outputData.isEmpty() && !directOutputBuffer.hasRemaining());
				}
				outputData.takeData(directOutputBuffer.clear());
				channel.write(directOutputBuffer.flip());
			}
		} catch (ClosedChannelException | SocketException closed) {
			safeClose();
		} catch (Exception e) {
			e.printStackTrace(SKDSLogger.ERROR_PRINTSTREAM);
			safeClose();
		}
	}

	public abstract void readInput() throws IOException;

	public abstract void doDataWrite(boolean isQueueEmpty) throws IOException;

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
				directInputBuffer.flip();
				if (i > 0) {
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
			if (Thread.currentThread() != inputThread) throw new WrongThreadException(
					"Read operation must only be called from " + inputThread
							+ " but was called from " + Thread.currentThread()
			);
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
			if (Thread.currentThread() != outputThread) throw new WrongThreadException(
					"Write operation must only be called from " + outputThread
							+ " but was called from " + Thread.currentThread()
			);
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
