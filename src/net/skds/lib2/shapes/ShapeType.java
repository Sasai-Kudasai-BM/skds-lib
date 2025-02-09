package net.skds.lib2.shapes;

import lombok.AllArgsConstructor;
import net.skds.lib2.io.json.codec.typed.ConfigEnumType;

@AllArgsConstructor
public enum ShapeType implements ConfigEnumType<Shape> {
	AABB(AABB.class),
	OBB(OBB.class),
	COMPOSITE(CompositeSuperShape.class)
	;

	private final Class<? extends Shape> typeClass;

	@SuppressWarnings("unchecked")
	@Override
	public Class<Shape> getTypeClass() {
		return (Class<Shape>)typeClass;
	}

}
