package net.skds.lib2.io;

import net.skds.lib2.io.codec.PostDeserializeCall;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugReloadPostDeserializeCall {
	private static final net.skds.lib2.utils.logger.SKDSLogger log = net.skds.lib2.utils.logger.SKDSLoggerFactory.getLogger(DebugReloadPostDeserializeCall.class);
	private static final Map<Object, List<StackTraceElement[]>> callRegistry = new HashMap<>();

	public static void clear() {
		callRegistry.clear();
	}

	@SuppressWarnings("all")
	public static synchronized boolean inspectMethodCall(PostDeserializeCall object) {
		List<StackTraceElement[]> counter = callRegistry.computeIfAbsent(object, k -> new ArrayList<>());
		counter.add((new Exception()).getStackTrace());
		if (counter.size() >= 2) {
			System.err.println("!!! Found Duplicate Call !!!");
			System.err.println("Class: " + object.getClass().getName());
			System.err.println("ID: " + System.identityHashCode(object));
			//System.err.println("Стек вызовов второго выполнения:");
			// Выведет в консоль всю цепочку методов, которая привела ко второму вызову
			var iterator = counter.iterator();
			while (iterator.hasNext()) {
				for (StackTraceElement traceElement : iterator.next()) {
					log.error(traceElement);
				}
				if (iterator.hasNext()) {
					System.err.println();
				}
			}
			System.err.println("----------------------------------------");
			return true;
		}
		return false;
	}
}
