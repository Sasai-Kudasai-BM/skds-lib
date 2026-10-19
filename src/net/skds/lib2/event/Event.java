package net.skds.lib2.event;

public class Event {
	protected boolean canceled = false;

	public void setCanceled(final boolean canceled) {
		this.canceled = canceled;
	}

	public boolean isCanceled() {
		return this.canceled;
	}
}
