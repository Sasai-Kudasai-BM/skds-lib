package net.skds.lib.execution;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;

public class ParallelExecutor implements AutoCloseable {

	private BooleanSupplier task;
	private final int threadCount;
	private final Exec[] threads;
	private CountDownLatch latch;

	public ParallelExecutor() {
		this.threadCount = Runtime.getRuntime().availableProcessors();

		this.threads = new Exec[this.threadCount];
		for (int index = 0; index < threads.length; index++) {
			threads[index] = new Exec(index);
		}
	}

	public ParallelExecutor(int threadCount) {
		this.threadCount = threadCount;

		this.threads = new Exec[this.threadCount];
		for (int index = 0; index < threads.length; index++) {
			threads[index] = new Exec(index);
		}
	}

	public void execute(BooleanSupplier task) {
		this.task = task;
		this.latch = new CountDownLatch(threadCount);
		try {
			for (Exec exec : threads) {
				LockSupport.unpark(exec);
				//exec.resume();
			}
			//for (int i = 0; i < threadCount; i++) {
			//	new Exec(i).start();
			//}
			latch.await();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class Exec extends Thread {

		private boolean alive = true;

		public Exec(int n) {
			setName("SKDS-Executor-" + n);
			setDaemon(false);
			start();
		}

		public void kill() {
			alive = false;
			LockSupport.unpark(this);
			//resume();
		}

		@Override
		public void run() {
			//suspend();
			LockSupport.park();
			while (alive) {
				try {
					while (task.getAsBoolean()) ;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					latch.countDown();
					//interrupt();
					LockSupport.park();
					//suspend();
				}
			}
		}
	}

	public static class Supl implements BooleanSupplier {

		private Iterator<Runnable> src;

		public Supl(Collection<Runnable> src) {
			this.src = src.iterator();
		}

		public Supl(Runnable[] src) {
			this.src = Arrays.asList(src).iterator();
		}

		@Override
		public boolean getAsBoolean() {
			Runnable r;
			synchronized (src) {
				if (src.hasNext()) {
					r = src.next();
				} else {
					return false;
				}
			}
			r.run();
			return true;
		}
	}

	@Override
	public void close() throws Exception {
		for (Exec exec : threads) {
			exec.kill();
		}

	}
}