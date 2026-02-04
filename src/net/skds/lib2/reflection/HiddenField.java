package net.skds.lib2.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public record HiddenField<T>(Field field) {

	public String getName() {
		return this.field.getName();
	}

	public int getModifiers() {
		return this.field.getModifiers();
	}

	public boolean isTransient() {
		return Modifier.isTransient(this.getModifiers());
	}

	@SuppressWarnings("unchecked")
	public Class<T> getType() {
		return (Class<T>) field.getType();
	}

	@SuppressWarnings("unchecked")
	public T get(Object o) {
		try {
			return (T) this.field.get(o);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void set(Object o, T value) {
		try {
			this.field.set(o, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}

