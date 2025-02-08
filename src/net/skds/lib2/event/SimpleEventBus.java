package net.skds.lib2.event;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleEventBus<T> {

	private final ConcurrentLinkedQueue<SimpleEventListener<T>> listeners = new ConcurrentLinkedQueue<>();

	public void addListener(SimpleEventListener<T> listener) {
		listeners.add(listener);
	}

	public void removeListener(SimpleEventListener<T> listener) {
		listeners.remove(listener);
	}

	public void post(T event) {
		for (SimpleEventListener<T> l : listeners) {
			l.onEvent(event);
		}
	}
}
