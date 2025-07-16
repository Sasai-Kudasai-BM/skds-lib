package net.skds.lib2.utils.threadsafety;

import java.util.HashSet;
import java.util.Set;

public class ThreadSafetyValidator {

	private static final ThreadLocal<Entry> ENTRIES = ThreadLocal.withInitial(Entry::new);

	public static void setForbidden(Object marker) {
		ENTRIES.get().forbidden.add(marker);
	}

	public static void removeForbidden(Object marker) {
		ENTRIES.get().forbidden.remove(marker);
	}

	public static void setPermitted(Object marker) {
		ENTRIES.get().permitted.add(marker);
	}

	public static void removePermitted(Object marker) {
		ENTRIES.get().permitted.remove(marker);
	}

	public static boolean isForbidden(Object marker) {
		return ENTRIES.get().forbidden.contains(marker);
	}

	public static boolean isPermitted(Object marker) {
		return ENTRIES.get().permitted.contains(marker);
	}

	public static void ensureNotForbidden(Object marker) {
		if (ENTRIES.get().forbidden.contains(marker))
			throw new WrongThreadException("Safety check failed for thread \"" + Thread.currentThread() + "\" due to forbidden marker + \"" + marker + "\"");
	}

	public static void ensurePermitted(Object marker) {
		if (!ENTRIES.get().permitted.contains(marker))
			throw new WrongThreadException("Safety check failed for thread \"" + Thread.currentThread() + "\" due to lack of permitted marker + \"" + marker + "\"");
	}

	private record Entry(Set<Object> forbidden, Set<Object> permitted) {
		Entry() {
			this(new HashSet<>(), new HashSet<>());
		}
	}
}
