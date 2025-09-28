package net.skds.lib2.network;

import lombok.Getter;
import lombok.Setter;

/**
 * @noinspection FieldMayBeFinal
 */
@Getter
@Setter
public abstract class CommonConnectionOptions implements Cloneable {

	private int silenceTimeout = 10_000;

	@Override
	public CommonConnectionOptions clone() {
		throw new RuntimeException(new CloneNotSupportedException());
	}
}
