package net.skds.lib2.demo.demo3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import net.skds.lib2.demo.demo3d.Demo3dShape.DemoShape3dHolder;
import net.skds.lib2.shapes.Shape;

public interface Demo3dShapeCollector {
	
	default void addShape(Supplier<Shape> shape) {
		addShape(Demo3dShape.of(shape));
	}

	default void addShape(Shape shape) {
		addShape(Demo3dShape.of(shape));
	}

	void addShape(Demo3dShape shape);

	public static class Demo3dShapeCollectorImpl implements Demo3dShapeCollector {
		public final List<Shape> array = new ArrayList<>();

		@Override
		public final void addShape(Shape shape) {
			apply(shape);
		}

		@Override
		public final void addShape(Supplier<Shape> shape) {
			apply(shape.get());
		}

		@Override
		public final void addShape(Demo3dShape shape) {
			apply(shape.getShape());
		}

		private void apply(Object shape) {
			if (shape == null) {
				return;
			}
			if (shape instanceof Shape s) {
				this.array.add(s);
				return;
			}
			if (shape instanceof DemoShape3dHolder holder) {
				apply(holder.getShape());
				return;
			}
			if (shape instanceof Collection collection) {
				for (Object object : collection) {
					apply(object);
				}
				return;
			}
			if (shape.getClass().isArray()) {
				for (Object object : ((Object[])shape)) {
					apply(object);
				}
				return;
			}

			throw new IllegalStateException(shape.getClass().toGenericString());
		}
		
	}
}
