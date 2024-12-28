package net.skds.lib2.utils.logger;

import net.skds.lib2.utils.ThreadUtils;
import net.w3e.lib.utils.FileUtils;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

class LogWriter extends Thread {

	public static final LogWriter INSTANCE = new LogWriter();

	private boolean wait = true;

	private final LinkedBlockingQueue<LogWriteable> entries = new LinkedBlockingQueue<>();

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
				LogWriteable le = entries.take();
				wait = false;
				le.write();
			} catch (Exception e) {
				e.printStackTrace(SKDSLogger.ORIGINAL_ERR);
			}
		}
	}

	static void write(Date date, String msg, LoggerLevel level, boolean useGlobalPrintStream, boolean useFileOut) {
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
				if (!Files.exists(path)) {
					FileUtils.createParentDirs(path.toFile());
				}
				Files.writeString(path, msg, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
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

}
