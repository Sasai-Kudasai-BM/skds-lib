package net.skds.lib2.misc.timer;

@FunctionalInterface
public interface ScheduledRunnable extends Runnable, ScheduledTimedAction {
	@Override
	default long runScheduledAction() {
		run();
		return -1;
	}
	
	static ScheduledRunnable of(Runnable runnable) {
		return runnable::run;
	}
}
