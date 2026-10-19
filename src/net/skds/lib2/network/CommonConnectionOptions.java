package net.skds.lib2.network;

/**
 * @noinspection FieldMayBeFinal
 */
public abstract class CommonConnectionOptions implements Cloneable {

	private int silenceTimeout = 10_000;

	@Override
	public CommonConnectionOptions clone() {
		throw new RuntimeException(new CloneNotSupportedException());
	}

	public int getSilenceTimeout() {
		return this.silenceTimeout;
	}

	public void setSilenceTimeout(final int silenceTimeout) {
		this.silenceTimeout = silenceTimeout;
	}
}
