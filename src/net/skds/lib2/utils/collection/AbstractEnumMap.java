package net.skds.lib2.utils.collection;

import lombok.Getter;

class AbstractEnumMap<E extends Enum<E>> {

	protected final E[] keyUniverse;
	@Getter
	protected final Class<E> enumType;

	public AbstractEnumMap(Class<E> enumType) {
		this.enumType = enumType;
		this.keyUniverse = enumType.getEnumConstants();
	}

	public int universeSize() {
		return keyUniverse.length;
	}

}
