package net.skds.lib2.utils.logger;

import net.skds.lib2.utils.StringUtils;
import net.skds.lib2.utils.ThreadUtils;
import net.w3e.lib.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

class LogWriter extends Thread {

	public static final LogWriter INSTANCE = new LogWriter();

	private boolean wait = true;

	private final LinkedBlockingQueue<LogEntry> entries = new LinkedBlockingQueue<>();

	public LogWriter() {
		super("SKDS-LogWriter");
		setDaemon(true);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			while (!entries.isEmpty() || !wait) {
				ThreadUtils.await(100);
			}
		}));
		start();
	}


	@Override
	public void run() {
		while (true) {
			try {
				wait = true;
				LogEntry le = entries.take();
				wait = false;
				write(le);
			} catch (Exception e) {
				e.printStackTrace(SKDSLogger.ORIGINAL_ERR);
			}
		}
	}

	public void add(LogEntry le) {
		entries.offer(le);
	}

	private void write(LogEntry le) throws IOException {
		Date date = new Date(le.time());
		SKDSLoggerConfig config = SKDSLoggerConfig.getInstance();
		StringBuilder logMsg = new StringBuilder();
		logMsg.append(config.getTimeFormat().format(date)).append(' ');
		if (le.loggingClass() != null) {
			logMsg.append('[').append(le.loggingClass().getSimpleName()).append("] ");
		}
		if (le.thread() != null) {
			logMsg.append('[').append(le.thread()).append("] ");
		}
		StackTraceElement trace = le.trace();
		if (trace != null) {
			logMsg.append('[')
					.append(StringUtils.cutStringAfterFromEnd(trace.getClassName(), '.'))
					.append(':')
					.append(trace.getMethodName())
					.append('.')
					.append(trace.getLineNumber())
					.append("] ");
		}
		logMsg.append('[').append(le.level().msg).append("] ");
		logMsg.append(le.message()).append('\n');

		String decoratedMsg = logMsg.toString();

		String logName = config.getLogDir() + '/' + config.getDateFormat().format(date) + ".log";
		Path path = Path.of(logName);

		try {
			if (!Files.exists(path)) {
				FileUtils.createParentDirs(path.toFile());
			}
			Files.writeString(path, decoratedMsg, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			switch (le.level()) {
				case WARN, ERROR -> SKDSLogger.ORIGINAL_ERR.print(decoratedMsg);
				default -> SKDSLogger.ORIGINAL_OUT.print(decoratedMsg);
			}
		} catch (Exception e) {
			e.printStackTrace(SKDSLogger.ORIGINAL_ERR);
			entries.add(le);
		}
	}
}
