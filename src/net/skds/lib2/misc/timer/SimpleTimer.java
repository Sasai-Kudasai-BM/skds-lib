package net.skds.lib2.misc.timer;

import lombok.CustomLog;
import net.skds.lib2.utils.ThreadUtils;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

@CustomLog
public class SimpleTimer {

	public static final SimpleTimer INSTANCE;

	private static final Comparator<Entry> COMPARATOR = (e1, e2) -> Long.compare(e1.time, e2.time);

	private boolean stopped = false;
	private final Thread thread;
	private final PriorityBlockingQueue<Entry> queue = new PriorityBlockingQueue<>(16, COMPARATOR);

	public SimpleTimer(String name) {
		this.thread = new Thread(ThreadUtils.UTIL_GROUP, "SimpleTimer-" + name) {
			{
				setDaemon(true);
			}

			@Override
			public void run() {
				while (!stopped) {
					try {
						SimpleTimer.this.run();
					} catch (InterruptedException e) {
						log.warn("Unexpected interruption " + e);
					}
				}
			}
		};
	}

	private void run() throws InterruptedException {
		// wait for entries
		Entry e = queue.take();
		long t = System.currentTimeMillis();
		if (e.time <= t) {
			try {
				long nt = e.action.runScheduledAction();
				if (nt >= 0) {
					// schedule again if needed
					schedule(e.action, nt);
				}
			} catch (Throwable throwable) {
				throwable.printStackTrace(System.err);
			}
		} else {
			// not time yet, return to queue
			queue.offer(e);
			// wait till the time
			chill(e.time - t);
		}
	}

	public void schedule(ScheduledTimedAction action, long time) {
		queue.offer(new Entry(action, time));
		wakeUp();
	}

	public void scheduleRelative(ScheduledTimedAction action, long time) {
		schedule(action, time + System.currentTimeMillis());
	}

	public void schedule(Runnable action, long time) {
		queue.offer(new Entry(ScheduledRunnable.of(action), time));
		wakeUp();
	}

	public void scheduleRelative(Runnable action, long time) {
		schedule(action, time + System.currentTimeMillis());
	}

	public void stop() {
		this.stopped = true;
	}

	public void start() {
		this.thread.start();
	}

	public boolean isRunning() {
		return !stopped && thread.isAlive();
	}

	private void wakeUp() {
		synchronized (thread) {
			thread.notifyAll();
		}
	}

	private void chill(long timeout) {
		synchronized (thread) {
			try {
				thread.wait(timeout);
			} catch (InterruptedException e) {
				log.warn("Unexpected interruption " + e);
			}
		}
	}

	private record Entry(ScheduledTimedAction action, long time) {

	}

	static {
		INSTANCE = new SimpleTimer("Default-Instance") {
			{
				start();
			}

			@Override
			public void stop() {
				throw new UnsupportedOperationException("This instance must not be stopped");
			}
		};
	}
}
