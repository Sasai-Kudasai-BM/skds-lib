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

	public static TFNStateEnum valueOf(boolean value) {
		return value ? TRUE : FALSE;
	}

	public static TFNStateEnum valueOf(Boolean value) {
		if (value == null) {
			return NOT_STATED;
		}
		return value ? TRUE : FALSE;
	}

	public TFNStateEnum and(TFNStateEnum value) {
		if (this == value) {
			return this;
		}
		if (!this.isStated()) {
			return value;
		}
		if (!value.isStated()) {
			return this;
		}
		return FALSE;
	}

	public TFNStateEnum or(TFNStateEnum value) {
		if (this == value) {
			return this;
		}
		if (!this.isStated()) {
			return value;
		}
		if (!value.isStated()) {
			return this;
		}
		return this.isTrue() || value.isTrue() ? TRUE : FALSE;
	}
}
