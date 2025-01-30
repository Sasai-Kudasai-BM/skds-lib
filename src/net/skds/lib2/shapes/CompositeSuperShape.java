package net.skds.lib2.shapes;

import lombok.AllArgsConstructor;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec4.Quat;
import net.skds.lib2.utils.AutoString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public sealed class CompositeSuperShape implements CompositeShape {

	private static final ConvexShape[] empty = {};

	public static final CompositeSuperShape EMPTY = new Empty();

	private final Shape[] shapes;
	private final AABB bounding;
	private final Vec3 center;
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

	public CompositeSuperShape setPose(PoseFunction pf, Vec3 pos, Quat rot, double scale) {
		final Shape[] shapes = new Shape[this.shapes.length];
		for (int i = 0; i < shapes.length; i++) {
			Shape shape = this.shapes[i];
			Vec3 od = shape.getCenter().sub(this.center);
			PoseCallbackImpl pc = new PoseCallbackImpl(Vec3.ZERO, rot, scale);

			pf.applyPose(shape, pos, rot, scale, pc);

			Vec3 nd = od.add(pc.pos).transform(pc.rot).scale(pc.scale).add(pos);
			if (shape instanceof CompositeSuperShape css) {
				shapes[i] = css.setPose(pf, nd.sub(od), pc.rot, pc.scale);
			} else {
				shapes[i] = shape.moveRotScale(nd.sub(od), pc.rot, pc.scale);
			}
		}
		return new CompositeSuperShape(shapes, center.add(pos), attachment);
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

	@AllArgsConstructor
	private static final class PoseCallbackImpl implements PoseCallback {
		Vec3 pos;
		Quat rot;
		double scale;

		@Override
		public void applyPose(Vec3 pos, Quat rot, double scale) {
			this.pos = pos;
			this.rot = rot;
			this.scale = scale;
		}
	}

	@FunctionalInterface
	public interface PoseFunction {
		void applyPose(Shape shape, Vec3 pos, Quat rot, double scale, PoseCallback callback);
	}

	@FunctionalInterface
	public interface PoseCallback {
		void applyPose(Vec3 pos, Quat rot, double scale);
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
}
