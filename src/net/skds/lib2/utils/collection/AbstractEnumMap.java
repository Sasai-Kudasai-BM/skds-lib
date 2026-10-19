package net.skds.lib2.utils.collection;

class AbstractEnumMap<E extends Enum<E>> {
	protected final E[] keyUniverse;
	protected final Class<E> enumType;

	public AbstractEnumMap(Class<E> enumType) {
		this.enumType = enumType;
		this.keyUniverse = enumType.getEnumConstants();
	}

	public final int universeSize() {
		return this.keyUniverse.length;
	}

	public Class<E> getEnumType() {
		return this.enumType;
	}
}
