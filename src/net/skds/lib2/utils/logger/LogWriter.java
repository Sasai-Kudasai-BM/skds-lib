package net.skds.lib2.utils.logger;

import lombok.RequiredArgsConstructor;
import net.skds.lib2.utils.ThreadUtils;
import net.w3e.lib.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

class LogWriter extends Thread {

	private static final int SHUTDOWN_TIMEOUT = Integer.getInteger("skds.logger-shutdown-timeout", 2500);
	public static final LogWriter INSTANCE = new LogWriter();

	private boolean running = true;

	private final LinkedBlockingQueue<LogWriteable> entries = new LinkedBlockingQueue<>();
	private final FileLogWriter fileWriter;

	public LogWriter() {
		super("SKDS-LogWriter");
		this.fileWriter = new FileLogWriter();
		setDaemon(true);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			running = false;
			long t0 = System.currentTimeMillis();
			for (long t = 0; isBusy(); t = System.currentTimeMillis() - t0) {
				if (t > SHUTDOWN_TIMEOUT) {
					SKDSLogger.ORIGINAL_ERR.println("SKDS-LogWriter-Finalizer error: log write timeout");
					return;
				}
				ThreadUtils.await(100);
			}
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

	static void write(Date date, String msg, LoggerLevel level, PrintStream[] attachedStreams, boolean useGlobalPrintStream, String fileOut) {
		SKDSLoggerConfig config = SKDSLoggerConfig.getInstance();
		try {
			if (useGlobalPrintStream) {
				switch (level) {
					case WARN, ERROR, SYSTEM_ERR -> SKDSLogger.ORIGINAL_ERR.print(msg);
					default -> SKDSLogger.ORIGINAL_OUT.print(msg);
				}
			}
			for (PrintStream ps : attachedStreams) {
				ps.print(msg);
			}
			if (fileOut != null) {
				String path = config.getLogDir() + '/' + config.getDateFormat().format(date);
				INSTANCE.fileWriter.addMsg(path, fileOut, config);
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

		private static final Function<String, FileEntry> BUFFER_CONSTRUCTOR = FileEntry::new;

		private final Map<String, FileEntry> entries = new ConcurrentHashMap<>();

		FileLogWriter() {
			super("SKDS-FileLogWriter");
			setDaemon(true);
		}

		void addMsg(String path, String msg, SKDSLoggerConfig config) {
			FileEntry entry = entries.computeIfAbsent(path, BUFFER_CONSTRUCTOR);
			entry.splitSize = config.getLogFileSplitSize();
			StringBuffer sb = entry.buffer;
			sb.append(msg);
			synchronized (this) {
				notify();
			}
		}

		private boolean isBusy() {
			return getState() == State.RUNNABLE || !LogWriter.this.entries.isEmpty();
		}

		@Override
		public void run() {
			while (running) {
				try {
					for (var itr = entries.entrySet().iterator(); itr.hasNext(); ) {
						var e = itr.next();
						String path = e.getKey();
						FileEntry entry = e.getValue();
						StringBuffer sb = entry.buffer;
						String num = entry.currentSplit == 0 ? "(0)" : "(" + entry.currentSplit + ")";
						File f = new File(path + num + ".log");
						long ss = entry.splitSize;
						while (f.length() > ss) {
							f = new File(path + "(" + ++entry.currentSplit + ").log");
						}

						// TODO clear?
						if (!sb.isEmpty()) {
							writeFile(f, sb);
							sb.setLength(0);
							if (sb.capacity() > 1024 * 16) {
								entry.buffer = new StringBuffer(64);
							}
						}
					}
					if (entries.isEmpty()) synchronized (this) {
						wait(1000);
					}
				} catch (Exception e) {
					e.printStackTrace(SKDSLogger.ORIGINAL_ERR);
				}
			}
		}

		private void writeFile(File file, StringBuffer sb) throws IOException {
			if (!file.exists()) {
				FileUtils.createParentDirs(file);
			}
			Files.writeString(file.toPath(), sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
		}
	}

	@RequiredArgsConstructor
	private static class FileEntry {
		final String name;
		int currentSplit = 0;
		long splitSize;
		StringBuffer buffer = new StringBuffer(64);
	}

	//private record FileKey(String name, SKDSLoggerConfig config) {
	//}

}
