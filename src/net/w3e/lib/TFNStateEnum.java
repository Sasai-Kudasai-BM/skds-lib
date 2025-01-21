package net.w3e.lib;

public enum TFNStateEnum {
	TRUE,
	FALSE,
	NOT_STATED;

	public final boolean isTrue() {
		return this == TRUE;
	}

	public final boolean isFalse() {
		return this == FALSE;
	}

	public final boolean isNotStated() {
		return this == NOT_STATED;
	}

	public final boolean isStated() {
		return !this.isNotStated();
	}
}
