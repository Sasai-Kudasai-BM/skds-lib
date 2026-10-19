package net.skds.lib2.utils.warnings;

import net.skds.lib2.utils.SKDSUtils;
import java.util.function.Consumer;

public final class SingleUseWarning {
	private static final net.skds.lib2.utils.logger.SKDSLogger log = net.skds.lib2.utils.logger.SKDSLoggerFactory.getLogger(SingleUseWarning.class);
	private Consumer<Object> action;

	public SingleUseWarning(String message) {
		this.action = s -> {
			if (s == null) {
				log.warn(message);
			} else {
				log.warn(message + ": " + s);
			}
			action = SKDSUtils.emptyConsumer();
		};
	}

	public void warn() {
		action.accept(null);
	}

	public void warn(Object o) {
		action.accept(o);
	}
}
