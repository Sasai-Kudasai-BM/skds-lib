package net.skds.lib2.network.tcp;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.network.CommonConnectionOptions;

@Getter
@Setter
public abstract class TCPConnectionOptions extends CommonConnectionOptions {

	private int maxInputBufferSize = 8 << 16;

	private int inputBufferSize = 1 << 16;
	private int outputBufferSize = 1 << 16;
}
