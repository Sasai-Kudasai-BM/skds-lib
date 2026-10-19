package net.skds.lib2.reflection;

import java.lang.reflect.Field;

public class FindOptions {
	private boolean strictType = true;
	private int ordinal = 0;
	//private String name;
	private Class<?> type;

	public FindOptions(int ordinal, Class<?> type) {
		this.ordinal = ordinal;
		this.type = type;
	}

	public FindOptions(Class<?> type) {
		this.ordinal = -1;
		this.type = type;
	}

	public FindOptions(Class<?> type, boolean strictType) {
		this.ordinal = -1;
		this.type = type;
		this.strictType = strictType;
	}

	public FindOptions(int ordinal, Class<?> type, boolean strictType) {
		this.ordinal = ordinal;
		this.type = type;
		this.strictType = strictType;
	}

	public boolean test(Field field) {
		if (strictType) {
			return field.getType() == type;
		} else {
			return type.isAssignableFrom(field.getType());
		}
	}

	public boolean isStrictType() {
		return this.strictType;
	}

	public int getOrdinal() {
		return this.ordinal;
	}

	public Class<?> getType() {
		return this.type;
	}

	public void setStrictType(final boolean strictType) {
		this.strictType = strictType;
	}

	public void setOrdinal(final int ordinal) {
		this.ordinal = ordinal;
	}

	public void setType(final Class<?> type) {
		this.type = type;
	}
}
