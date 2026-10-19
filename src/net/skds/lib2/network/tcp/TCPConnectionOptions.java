package net.skds.lib2.network.tcp;

import net.skds.lib2.network.CommonConnectionOptions;

public abstract class TCPConnectionOptions extends CommonConnectionOptions {
	private int maxInputBufferSize = 8 << 16;
	private int inputBufferSize = 1 << 16;
	private int outputBufferSize = 1 << 16;

	public int getMaxInputBufferSize() {
		return this.maxInputBufferSize;
	}

	public int getInputBufferSize() {
		return this.inputBufferSize;
	}

	public int getOutputBufferSize() {
		return this.outputBufferSize;
	}

	public void setMaxInputBufferSize(final int maxInputBufferSize) {
		this.maxInputBufferSize = maxInputBufferSize;
	}

	public void setInputBufferSize(final int inputBufferSize) {
		this.inputBufferSize = inputBufferSize;
	}

	public void setOutputBufferSize(final int outputBufferSize) {
		this.outputBufferSize = outputBufferSize;
	}
}
