package net.w3e.lib;

import static net.w3e.lib.TFNStateEnum.*;

public interface ITFNStateEnum {

	TFNStateEnum getAsITFNStateEnum();

	default boolean isTrue() {
		return this.getAsITFNStateEnum() == TRUE;
	}

	default boolean isFalse() {
		return this.getAsITFNStateEnum() == FALSE;
	}

	default boolean isNotStated() {
		return this.getAsITFNStateEnum() == NOT_STATED;
	}

	default boolean isStated() {
		return !this.isNotStated();
	}

}
