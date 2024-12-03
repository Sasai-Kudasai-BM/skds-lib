package net.skds.lib.utils;

import net.sdteam.libmerge.Lib2Merge;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

@Lib2Merge
public final class ThreadAnalyzer {

	private long lastAnalyzeNanos;
	private boolean running = true;

	private final Thread thread;
	private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	private final Map<Long, Entry> lastThreadInfo = new HashMap<>();
	private ThreadLoad[] lastReport = null;

	private static final ThreadLoad[] emptyReport = {};

	public ThreadAnalyzer(Consumer<ThreadLoad[]> listener) {
		this(500, listener);
	}

	public ThreadAnalyzer(long periodMs, Consumer<ThreadLoad[]> listener) {
		this.thread = new ThreadUtil.SKDSThread("Thread-Analyzer") {
			@Override
			public void run() {
				while (running) {
					long t0 = System.currentTimeMillis();
					analyze();
					listener.accept(getLoadReport());
					LockSupport.parkUntil(t0 + periodMs);
				}
			}
		};
		this.thread.setDaemon(true);
	}

	public void start() {
		this.thread.start();
	}

	public void stop() {
		running = false;
	}

	private void analyze() {
		long nanos = System.nanoTime();
		long delta = nanos - lastAnalyzeNanos;
		lastAnalyzeNanos = nanos;

		ThreadInfo[] threads = threadMXBean.dumpAllThreads(false, false);

		lastThreadInfo.forEach((id, e) -> e.listed = false);

		for (int i = 0; i < threads.length; i++) {
			ThreadInfo thread = threads[i];
			long id = thread.getThreadId();
			long time = threadMXBean.getThreadCpuTime(id);

			Entry e = lastThreadInfo.get(id);
			if (e == null) {
				e = new Entry(thread);
				lastThreadInfo.put(id, e);
			} else {
				e.load = (double) (time - e.lastThreadTime) / delta;
			}
			e.listed = true;
			e.lastThreadTime = time;
		}
		lastThreadInfo.values().removeIf(e -> !e.listed);
		lastReport = lastThreadInfo.values().stream()
				.filter(e -> e.load >= 0)
				.map(e -> new ThreadLoad(e.threadInfo, e.load))
				.toArray(ThreadLoad[]::new);
	}

	public ThreadLoad[] getLoadReport() {
		ThreadLoad[] rep = lastReport;
		return rep == null ? emptyReport : lastReport;
	}

	private static class Entry {

		//private final long id;
		private final ThreadInfo threadInfo;
		private long lastThreadTime;
		private boolean listed;
		private double load = -1;

		private Entry(ThreadInfo threadInfo) {
			this.threadInfo = threadInfo;
		}
	}

	public record ThreadLoad(ThreadInfo info, double load) {
		@Override
		public String toString() {
			return "%s: %.2f%%".formatted(info.getThreadName(), load * 100);
		}
	}

	//public ThreadInfo[] findDeadlocks() {
	//	threadMXBean.findMonitorDeadlockedThreads()
	//	threadMXBean.findDeadlockedThreads()
	//}
}
