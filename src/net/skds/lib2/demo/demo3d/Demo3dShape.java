package net.skds.lib2.demo.demo3d;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.shapes.Collision;
import net.skds.lib2.shapes.Shape;

public abstract class Demo3dShape {

	@Getter
	private DemoShape3dHolder hovered;

	public final boolean isHovered() {
		return this.hovered != null;
	}

	public void setHovered(DemoShape3dHolder hovered) {
		this.hovered = hovered;
	}

	public abstract Object getShape();

	protected void tick(Demo3dFrame demo) {}

	public void mouseClicked(MouseEvent event) {
		mouseEvent(event, this::mouseClickedM1, this::mouseClickedM2, this::mouseClickedMiddle);
	}

	public void mouseClickedM1(MouseEvent event) {}
	public void mouseClickedM2(MouseEvent event) {}
	public void mouseClickedMiddle(MouseEvent event) {}

	public void mousePressed(MouseEvent event) {
		mouseEvent(event, this::mousePressedM1, this::mousePressedM2, this::mousePressedMiddle);
	}

	public void mousePressedM1(MouseEvent event) {}
	public void mousePressedM2(MouseEvent event) {}
	public void mousePressedMiddle(MouseEvent event) {}

	public void mouseReleased(MouseEvent event) {
		mouseEvent(event, this::mouseReleasedM1, this::mouseReleasedM2, this::mouseReleasedMiddle);
	}

	public void mouseReleasedM1(MouseEvent event) {}
	public void mouseReleasedM2(MouseEvent event) {}
	public void mouseReleasedMiddle(MouseEvent event) {}

	protected void mouseEvent(MouseEvent event, Consumer<MouseEvent> m1, Consumer<MouseEvent> m2, Consumer<MouseEvent> middle) {
		switch (event.getButton()) {
			case 1 -> {if (m1 != null) m1.accept(event);}
			case 3 -> {if (m2 != null) m2.accept(event);}
			case 2 -> {if (middle != null) middle.accept(event);}
		};
	}

	@AllArgsConstructor
	@RequiredArgsConstructor
	public class DemoShape3dHolder {
		public final Demo3dShape root;
		@Setter(value = AccessLevel.PACKAGE)
		@Getter
		private Shape shape;

		public final void setHovered() {
			this.root.setHovered(this);
		}
	}

	public static Demo3dShape of(Shape shape) {
		return new Demo3dShapeValue(shape);
	}

	@AllArgsConstructor
	private static class Demo3dShapeValue extends Demo3dShape {

		private final DemoShape3dHolder shape;

		public Demo3dShapeValue(Shape shape) {
			this.shape = new DemoShape3dHolder(this, shape);
		}

		@Override
		public Object getShape() {
			return this.shape;
		}
	}

	public static Demo3dShape of(Supplier<Shape> shape) {
		return new Demo3dShapeSupplier(shape);
	}

	@AllArgsConstructor
	private static class Demo3dShapeSupplier extends Demo3dShape {

		private final Supplier<Shape> supplier;

		private final DemoShape3dHolder shape;

		public Demo3dShapeSupplier(Supplier<Shape> shape) {
			this.supplier = shape;
			this.shape = new DemoShape3dHolder(this);
		}

		@Override
		public Object getShape() {
			this.shape.shape = this.supplier.get();
			return this.shape;
		}
	}

	//@AllArgsConstructor
	public abstract static class Demo3dShapeInterractable extends Demo3dShape {
	
		protected final Shape staticBox;

		protected final DemoShape3dHolder[] shapes;

		protected int move;

		protected Demo3dShapeInterractable() {
			this(1);
		}

		protected Demo3dShapeInterractable(int physexBodyCount) {
			this.staticBox = initStaticBox();

			List<DemoShape3dHolder> list = new ArrayList<>();
			list.add(new DemoShape3dHolder(this, this.staticBox));
			this.shapes = new DemoShape3dHolder[physexBodyCount + 1];
			this.shapes[0] = new DemoShape3dHolder(this, this.staticBox);
			for (int i = 0; i < physexBodyCount; i++) {
				this.shapes[i+1] = new DemoShape3dHolder(this);
			}
		}

		protected abstract Shape initStaticBox();

		@Override
		protected abstract void tick(Demo3dFrame demo);

		protected final Collision collide(Vec3 way) {
			return collide(this.shapes[1].getShape(), way);
		}

		protected final Collision collide(Shape human, Vec3 way) {
			return staticBox.collide(human, way);
		}

		@SuppressWarnings("unchecked")
		protected final <T extends Shape> T getHuman() {
			return (T)this.shapes[1].getShape();
		}

		protected final void setHuman(Shape human) {
			this.shapes[1].setShape(human);
		}
		
		@Override
		public final void mousePressedM1(MouseEvent event) {
			this.move = 1;
		}

		@Override
		public final void mouseReleasedM1(MouseEvent event) {
			this.move = 0;
		}
		
		@Override
		public final void mousePressedM2(MouseEvent event) {
			this.move = -1;
		}

		@Override
		public final void mouseReleasedM2(MouseEvent event) {
			this.move = 0;
		}

		@Override
		public final void mouseClickedMiddle(MouseEvent event) {
			this.reset();
		}

		protected abstract void reset();

		@Override
		public Object getShape() {
			return this.shapes;
		}
	}
}
