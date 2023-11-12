package net.skds.lib.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

public class ThreadUtil {

	public static final ThreadGroup MAIN_GROUP = new ThreadGroup("Main");
	public static final ThreadGroup UTIL_GROUP = new ThreadGroup("Util");

	public static final boolean USE_ANALYZER = Boolean.getBoolean("skds.thread-analyzer");
	public static final ThreadAnalyzer ANALYZER;

	private static final int threads = Runtime.getRuntime().availableProcessors();

	public static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(threads,
			threads,
			10,
			TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(),
			//new Queue(),
			r -> {
				SKDSThread t = new SKDSThread(UTIL_GROUP, r);
				t.setDaemon(true);
				return t;
			},
			(r, e) -> {
				throw new UnsupportedOperationException("Tasks will not be rejected!");
			});

	public static void runTaskNewThread(Runnable runnable) {
		new SKDSThread(UTIL_GROUP, runnable).start();
	}

	public static SKDSThread runNewThreadMainGroup(Runnable runnable, String name) {
		SKDSThread t = new SKDSThread(MAIN_GROUP, runnable, name);
		t.start();
		return t;
	}

	public static SKDSThread runNewThreadMainGroupDaemon(Runnable runnable, String name) {
		SKDSThread t = new SKDSThread(MAIN_GROUP, runnable, name);
		t.setDaemon(true);
		t.start();
		return t;
	}

	public static SKDSThread runTickableDaemon(FiniteTickable tickable, String name, int period) {
		SKDSThread t = new SKDSThread(MAIN_GROUP, name) {
			@Override
			public void run() {
				while (tick(tickable, period))
					;
			}

		};
		t.setDaemon(true);
		t.start();
		return t;
	}

	public static SKDSThread runTickableDaemon(FiniteTickable tickable, Runnable initRun, String name, int period, Consumer<Exception> exceptionHandler) {
		SKDSThread t = new SKDSThread(MAIN_GROUP, name) {
			@Override
			public synchronized void run() {
				try {
					initRun.run();
					while (tick(tickable, period))
						;
				} catch (Exception e) {
					exceptionHandler.accept(e);
				}
			}

		};
		t.setDaemon(true);
		t.start();
		return t;
	}

	private static boolean tick(FiniteTickable tickable, int period) {
		long t0 = System.nanoTime();
		boolean tick = tickable.tick();
		long waitTime = t0 + (long) period * 1000_000L - System.nanoTime();
		LockSupport.parkNanos(waitTime);
		return tick;
	}

	public static void runAsync(Runnable runnable) {
		EXECUTOR.execute(runnable);
	}

	public static class SKDSThread extends Thread {

		//private final float k = 0.05f;
		//private float busy = 0;
		//private long lastCheck = 0;

		private static volatile int threadInitNumber;
		private final Runnable task;

		private static synchronized int nextThreadNum() {
			return threadInitNumber++;
		}

		public SKDSThread(ThreadGroup group, Runnable task) {
			super(group, task, "SKDS-" + group.getName() + "-" + nextThreadNum(), 0);
			this.task = task;
		}

		public SKDSThread(ThreadGroup group, Runnable task, String name) {
			super(group, task, name, 0);
			this.task = task;
		}

		public SKDSThread(String name) {
			super(MAIN_GROUP, null, name, 0);
			this.task = null;
		}

		public SKDSThread(ThreadGroup group, String name) {
			super(group, null, name, 0);
			this.task = null;
		}

		@Override
		public void run() {
			task.run();
		}

		//private void analyze() {
		//	final long time = System.nanoTime();
		//	final float dt = (time - this.lastCheck) / 1_000_000f;
		//	this.lastCheck = time;
		//	final State state = getState();
		//	final float k2 = k * dt;
		//	final float m = 1 - k2;
		//	busy *= m;
		//	if (state == State.RUNNABLE) {
		//		busy += k2;
		//	}
		//}

	}

	public static class ThreadAnalyzer extends Thread {

		private Thread[] threadArray = new Thread[2];
		//private DecimalFormat df = new DecimalFormat("##.##%");

		long t = 0;

		private ThreadAnalyzer() {
			super(MAIN_GROUP, "SKDS-Thread-Analyzer");
			setDaemon(true);
		}

		@Override
		public void run() {
			while (true) {
				loop();
				try {
					sleep(1);
				} catch (InterruptedException e) {
				}
			}

		}

		private void loop() {
			int count;
			while ((count = MAIN_GROUP.enumerate(threadArray)) == threadArray.length) {
				threadArray = new Thread[threadArray.length * 2];
			}

			long t2 = System.currentTimeMillis();
			if (t2 > t) {
				t = t2 + 1000;
				for (int i = 0; i < count; i++) {
					Thread t = threadArray[i];
					if (t instanceof SKDSThread skdsThread) {
						//log.info(skdsThread.getName() + " " + df.format(skdsThread.busy));
					}
				}
			}
			for (int i = 0; i < count; i++) {
				Thread t = threadArray[i];
				if (t instanceof SKDSThread skdsThread) {
					//skdsThread.analyze();
					//log.info(skdsThread.getName() + " " + skdsThread.busy);
				}
			}
		}
	}

	static {
		if (USE_ANALYZER) {
			ANALYZER = new ThreadAnalyzer();
			ANALYZER.start();
		} else {
			ANALYZER = null;
		}

		new Thread() { // Windows timer fix
			{
				this.setDaemon(true);
				this.start();
			}

			public void run() {
				while (true) {
					try {
						Thread.sleep(Long.MAX_VALUE);
					} catch (InterruptedException ex) {
					}
				}
			}
		};
	}
}
