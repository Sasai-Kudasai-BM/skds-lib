package net.skds.lib2.shapes;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonDeserializeBuilder;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.skds.lib2.io.json.codec.JsonToStringSerialiser;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.typed.TypedConfig;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec4.Quat;
import net.skds.lib2.utils.AutoString;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@DefaultJsonCodec(CompositeSuperShape.JCodec.class)
public sealed class CompositeSuperShape implements CompositeShape, TypedConfig {

	private static final ConvexShape[] empty = {};

	public static final CompositeSuperShape EMPTY = new Empty();

	private final Shape[] shapes;
	private final transient AABB bounding;
	private final Vec3 center;
	@DefaultJsonCodec(JsonToStringSerialiser.class)
	private Object attachment;

	CompositeSuperShape(Shape[] shapes, Vec3 center, Object attachment) {
		this.shapes = shapes;
		this.center = center;
		this.attachment = attachment;
		if (shapes.length == 0) {
			this.bounding = AABB.EMPTY;
		} else {
			AABBBuilder b = new AABBBuilder(shapes[0].getBoundingBox());
			for (int i = 1; i < shapes.length; i++) {
				b = b.expand(shapes[i].getBoundingBox());
			}
			this.bounding = b.build();
		}
	}

	public static CompositeSuperShape of(Shape[] shapes, Vec3 center) {
		if (shapes == null || shapes.length == 0) {
			return EMPTY;
		}
		return new CompositeSuperShape(shapes, center, null);
	}

	public static CompositeSuperShape of(Shape[] shapes, Vec3 center, Object attachment) {
		if (shapes == null || shapes.length == 0) {
			return EMPTY;
		}
		return new CompositeSuperShape(shapes, center, attachment);
	}

	public boolean isEmpty() {
		return shapes.length == 0;
	}

	@Override
	public ConvexShape[] simplify(AABB bounding) {
		if (bounding == null) return simplifyAll();
		if (isEmpty() || !this.bounding.intersects(bounding)) return empty;
		if (bounding.contains(this.bounding)) return simplifyAll();
		List<ConvexShape> list = new ArrayList<>();
		collectConvex(list, bounding);
		return list.toArray(ConvexShape[]::new);
	}

	private ConvexShape[] simplifyAll() {
		List<ConvexShape> list = new ArrayList<>();
		collectConvex(list, null);
		return list.toArray(ConvexShape[]::new);
	}

	private void collectConvex(List<ConvexShape> list, AABB bounding) {
		for (int i = 0; i < shapes.length; i++) {
			Shape shape = this.shapes[i];
			if (shape instanceof ConvexShape cs) {
				if (bounding == null || cs.getBoundingBox().intersects(bounding)) list.add(cs);
			} else if (shape instanceof CompositeSuperShape css) {
				if (bounding == null || css.getBoundingBox().intersects(bounding)) css.collectConvex(list, bounding);
			} else {
				list.addAll(List.of(((CompositeShape) shape).simplify(bounding)));
			}
		}
	}

	@Override
	public CompositeSuperShape move(Vec3 delta) {
		final Shape[] shapes = new Shape[this.shapes.length];
		for (int i = 0; i < shapes.length; i++) {
			shapes[i] = this.shapes[i].move(delta);
		}
		return new CompositeSuperShape(shapes, center.add(delta), attachment);
	}

	@Override
	public CompositeSuperShape scale(double scale) {
		final Shape[] shapes = new Shape[this.shapes.length];
		for (int i = 0; i < shapes.length; i++) {
			Shape shape = this.shapes[i];
			Vec3 od = shape.getCenter().sub(center);
			Vec3 nd = od.scale(scale);
			shapes[i] = shape.move(nd.sub(od)).scale(scale);
		}
		return new CompositeSuperShape(shapes, center, attachment);
	}

	@Override
	public CompositeSuperShape rotate(Matrix3 m3) {
		final Shape[] shapes = new Shape[this.shapes.length];
		for (int i = 0; i < shapes.length; i++) {
			Shape shape = this.shapes[i];
			Vec3 od = shape.getCenter().sub(this.center);
			Vec3 nd = od.transform(m3);
			shapes[i] = shape.moveRotScale(nd.sub(od), m3, 1);
		}
		return new CompositeSuperShape(shapes, this.center, this.attachment);
	}

	@Override
	public CompositeSuperShape moveRotScale(Vec3 pos, Matrix3 m3, double scale) {
		final Shape[] shapes = new Shape[this.shapes.length];
		for (int i = 0; i < shapes.length; i++) {
			Shape shape = this.shapes[i];
			Vec3 od = shape.getCenter().sub(this.center);
			Vec3 nd = od.transform(m3).scale(scale).add(pos);
			shapes[i] = shape.moveRotScale(nd.sub(od), m3, scale);
		}
		return new CompositeSuperShape(shapes, center.add(pos), attachment);
	}

	public CompositeSuperShape setPose(PoseFunction pf, final Vec3 parentPos, final Quat parentRot, final double parentScale) {
		final Shape[] shapes = new Shape[this.shapes.length];

		PoseCallback pc = new PoseCallback();

		for (int i = 0; i < shapes.length; i++) {
			Shape shape = this.shapes[i];
			Vec3 od = shape.getCenter().sub(this.center);

			pc.setPos(Vec3.ZERO);
			pc.setRot(Quat.ONE);
			pc.setScale(1);

			pf.applyPose(shape, parentPos, parentRot, parentScale, pc);

			if (pc.pos != Vec3.ZERO) {
				od = od.add(pc.pos);
			}
			if (pc.rot == Quat.ONE) {
				pc.rot = parentRot;
			} else if (parentRot != Quat.ONE) {
				pc.rot = parentRot.multiply(pc.rot);
			}

			if (pc.scale != 1 || parentScale != 1) {
				pc.scale *= parentScale;
			}
			
			Vec3 nd = od.transform(parentRot);
			if (pc.scale != 1) {
				nd = nd.scale(pc.scale);
			}
			nd = nd.add(parentPos).sub(od);

			if (shape instanceof CompositeSuperShape css) {
				shapes[i] = css.setPose(pf, nd, pc.rot, pc.scale);
			} else {
				shapes[i] = shape.moveRotScale(nd, pc.rot, pc.scale);
			}
		}

		return new CompositeSuperShape(shapes, center.add(parentPos), attachment);
	}

	@Override
	public Vec3 getCenter() {
		return center;
	}

	@Override
	public AABB getBoundingBox() {
		return bounding;
	}

	@Override
	public Shape[] getAllShapes() {
		return this.shapes;
	}

	@Override
	public Object getAttachment() {
		return attachment;
	}

	/**
	 * Don't use it
	 *
	 * @see CompositeSuperShape#withAttachment
	 */
	@Override
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	@Override
	public CompositeSuperShape withAttachment(Object attachment) {
		return new CompositeSuperShape(shapes, center, attachment);
	}

	public class PoseCallback {
		@Getter
		@Setter
		private Vec3 pos = Vec3.ZERO;
		@Getter
		@Setter
		private Quat rot = Quat.ONE;
		@Getter
		@Setter
		private double scale = 1;
	}

	@FunctionalInterface
	public interface PoseFunction {
		void applyPose(Shape shape, Vec3 parentPos, Quat parentRot, double parentScale, PoseCallback callback);
	}

	private static final class Empty extends CompositeSuperShape {
		public Empty() {
			super(empty, Vec3.ZERO, null);
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public ConvexShape[] simplify(AABB bounding) {
			return empty;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\"shapes\":").append(Arrays.toString(this.shapes));
		builder.append(",\"aabb\":").append(this.bounding);
		builder.append(",\"center\":").append(this.center);
		if (this.attachment != null) {
			builder.append(",\"attachment\":").append(this.attachment);
		}
		return AutoString.build(this, builder.toString());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof CompositeShape compositeShape) {
			if (!this.center.equals(compositeShape.getCenter())) {
				return false;
			}
			if (!this.bounding.equals(compositeShape.getBoundingBox())) {
				return false;
			}
			if (!Arrays.equals(this.shapes, compositeShape.getAllShapes())) {
				return false;
			}
			return true;
		}
		return false;
	}

	static final class JCodec extends JsonReflectiveBuilderCodec<CompositeSuperShape> {

		public JCodec(Type type, JsonCodecRegistry registry) {
			super(type, CompositeShapeTypeAdapter.class, registry);
		}

		private static class CompositeShapeTypeAdapter implements JsonDeserializeBuilder<CompositeSuperShape> {

			private Shape[] shapes;
			private Vec3 center = Vec3.ZERO;
			private String attachment;

			@Override
			public CompositeSuperShape build() {
				return new CompositeSuperShape(shapes, center, this.attachment);
			}
		}
	}

	@Override
	public final ConfigType<?> getConfigType() {
		return ShapeType.COMPOSITE;
	}
}
