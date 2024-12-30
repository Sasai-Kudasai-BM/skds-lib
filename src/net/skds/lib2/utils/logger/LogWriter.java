package net.skds.lib2.utils.logger;

import net.skds.lib2.utils.ThreadUtils;
import net.w3e.lib.utils.FileUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

class LogWriter extends Thread {

	public static final LogWriter INSTANCE = new LogWriter();

	private boolean running = true;

	private final LinkedBlockingQueue<LogWriteable> entries = new LinkedBlockingQueue<>();
	private final FileLogWriter fileWriter;


	public LogWriter() {
		super("SKDS-LogWriter");
		this.fileWriter = new FileLogWriter();
		setDaemon(true);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			while (isBusy()) {
				ThreadUtils.await(100);
			}
			running = false;
		}, "LogWriter-Finalizer"));
		start();
		fileWriter.start();
	}

	boolean isBusy() {
		return getState() == State.RUNNABLE || !entries.isEmpty() || fileWriter.isBusy();
	}

	@Override
	public void run() {
		while (running) {
			try {
				LogWriteable le = entries.take();
				le.write();
			} catch (Exception e) {
				e.printStackTrace(SKDSLogger.ORIGINAL_ERR);
			}
		}
	}

	static void write(Date date, String msg, LoggerLevel level, PrintStream[] attachedStreams, boolean useGlobalPrintStream, boolean useFileOut) {
		SKDSLoggerConfig config = SKDSLoggerConfig.getInstance();
		try {
			if (useGlobalPrintStream) {
				switch (level) {
					case WARN, ERROR, SYSTEM_ERR -> SKDSLogger.ORIGINAL_ERR.print(msg);
					default -> SKDSLogger.ORIGINAL_OUT.print(msg);
				}
			}
			for (PrintStream ps : SKDSLogger.attachedPrintStreams) {
				ps.print(msg);
			}
			if (useFileOut) {
				String logName = config.getLogDir() + '/' + config.getDateFormat().format(date) + ".log";
				Path path = Path.of(logName);
				if (msg.length() > 3) {
					msg = msg.substring(level.getColor().length());
				}
				INSTANCE.fileWriter.addMsg(path, msg);
			}
		} catch (Exception e) {
			e.printStackTrace(SKDSLogger.ORIGINAL_ERR);
		}
	}

	public void add(LogWriteable le) {
		entries.offer(le);
	}

	interface LogWriteable {
		void write();
	}

	private class FileLogWriter extends Thread {

		private final Map<Path, StringBuffer> buffers = new ConcurrentHashMap<>();

		FileLogWriter() {
			super("SKDS-FileLogWriter");
			setDaemon(true);
		}

		void addMsg(Path path, String msg) {
			StringBuffer sb = buffers.computeIfAbsent(path, p -> new StringBuffer(64));
			sb.append(msg);
			synchronized (this) {
				notify();
			}
		}

		private boolean isBusy() {
			return getState() == State.RUNNABLE || !entries.isEmpty();
		}

		@Override
		public void run() {
			while (running) {
				try {
					for (var itr = buffers.entrySet().iterator(); itr.hasNext(); ) {
						var e = itr.next();
						Path path = e.getKey();
						StringBuffer sb = e.getValue();
						itr.remove();
						if (!sb.isEmpty()) {
							writeFile(path, sb);
						}
					}
					if (buffers.isEmpty()) synchronized (this) {
						wait(1000);
					}
				} catch (Exception e) {
					e.printStackTrace(SKDSLogger.ORIGINAL_ERR);
				}
			}
		}

		private void writeFile(Path path, StringBuffer sb) throws IOException {
			if (!Files.exists(path)) {
				FileUtils.createParentDirs(path.toFile());
			}
			Files.writeString(path, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
		}
	}

}
