package net.skds.lib2.event;

@FunctionalInterface
public interface SimpleEventListener<T> {
	void onEvent(T event);
}
