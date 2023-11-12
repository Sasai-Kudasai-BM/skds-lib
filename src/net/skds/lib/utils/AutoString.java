package net.skds.lib.utils;

import java.lang.reflect.Field;

public interface AutoString {

	public default String autoString() {
		String s = getClass().getSimpleName() + ":\n";

		for (Field field : getClass().getFields()) {
			//for (Field field : getClass().getDeclaredFields()) {
			try {
				s += field.getName() + "	: " + field.get(this) + "\n";
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return s;
	}
}
