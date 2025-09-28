package net.skds.lib2.utils.warnings;

import lombok.CustomLog;
import net.skds.lib2.utils.SKDSUtils;

import java.util.function.Consumer;


@CustomLog
public final class SingleUseWarning {

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
