package net.w3e.lib;

public enum TFNStateEnum implements ITFNStateEnum {
	TRUE,
	FALSE,
	NOT_STATED;

	@Override
	public final TFNStateEnum getAsITFNStateEnum() {
		return this;
	}

	@Override
	public final boolean isTrue() {
		return this == TRUE;
	}

	@Override
	public final boolean isFalse() {
		return this == FALSE;
	}

	@Override
	public final boolean isNotStated() {
		return this == NOT_STATED;
	}

	@Override
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

	public final TFNStateEnum and(TFNStateEnum value) {
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

	public final TFNStateEnum or(TFNStateEnum value) {
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
