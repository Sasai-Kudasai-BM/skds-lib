package net.skds.lib2.shapes;

import net.skds.lib2.io.codec.typed.ConfigEnumType;

public enum ShapeType implements ConfigEnumType<Shape> {
	AABB(AABB.class),
	OBB(OBB.class),
	COMPOSITE(CompositeSuperShape.class);

	private final Class<? extends Shape> typeClass;

	@SuppressWarnings("unchecked")
	@Override
	public Class<Shape> getTypeClass() {
		return (Class<Shape>) typeClass;
	}

	private ShapeType(final Class<? extends Shape> typeClass) {
		this.typeClass = typeClass;
	}
}
