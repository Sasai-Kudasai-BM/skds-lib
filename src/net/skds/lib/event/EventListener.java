package net.skds.lib.event;

@FunctionalInterface
public interface EventListener<T extends Event> {
	public void onEvent(T event);
}
