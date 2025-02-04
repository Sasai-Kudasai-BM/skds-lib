package net.skds.lib2.demo.demo3d;

import java.awt.event.MouseEvent;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec4.Quat;
import net.skds.lib2.shapes.AABB;
import net.skds.lib2.shapes.Collision;
import net.skds.lib2.shapes.CompositeSuperShape;
import net.skds.lib2.shapes.Shape;

public class Demo3dFrameExample {
	
	public static Demo3dFrame initDefault(Demo3dFrame frame) {
		frame.addShape(AABB.fromCenter(0, 0, 0, 1, 1, 1));
		frame.addShape(AABB.fromCenter(0, -1, 0, 2, 1, 2));

		CompositeSuperShape superShape = CompositeSuperShape.of(new Shape[]{
			CompositeSuperShape.of(new Shape[]{
				AABB.fromCenter(Vec3.of(0, 1, 0), Vec3.of(1, 2, .5)),

				CompositeSuperShape.of(new Shape[]{
					AABB.fromCenter(Vec3.of(0, 0.75 / 2, 0), 0.75)
				}, Vec3.ZERO, "head").move(Vec3.of(0, 2, 0))
					.rotate(Quat.fromAxisDegrees(Vec3.ZN, 10))
				,

				CompositeSuperShape.of(new Shape[]{
					AABB.fromCenter(Vec3.of(0, 0, 0), Vec3.of(0.25, 1.75, 0.25))
				}, Vec3.of(0.25 / 2, 1.75 / 2 - 0.1, 0), "hand").move(Vec3.of(-1f / 2 - 0.25 / 2, 1.1, 0)).rotate(Quat.fromAxisDegrees(Vec3.XN, 30))

			}, Vec3.ZERO, "body")
		}, Vec3.ZERO);

		CompositeSuperShape rotated = superShape
				.move(Vec3.of(2, 0, 0))
				.rotate(Matrix3.fromQuat(Quat.fromAxisDegrees(Vec3.XN, 30)))
				;

		frame.addShape(() -> rotated.rotate(Quat.fromAxisDegrees(Vec3.YP, (System.currentTimeMillis() / 50d) % 360)));
		frame.addShape(superShape.move(Vec3.of(4, 0, 0)));

		CompositeSuperShape scaled = superShape.move(Vec3.of(6, 0, 0));

		frame.addShape(() -> scaled.scale(FastMath.sinDegr((System.currentTimeMillis() / 50d) % 360) / 2d + .5));

		CompositeSuperShape moveRotScale = superShape.move(Vec3.of(8, 0, 0));

		frame.addShape(() -> {
			Quat rot = Quat.fromAxisDegrees(Vec3.YN, (System.currentTimeMillis() / 10d) % 360);
			return moveRotScale.moveRotScale(Vec3.of(0, 0, FastMath.sinDegr((System.currentTimeMillis() / 50d) % 360)), rot, 1);
		});

		CompositeSuperShape posed1 = superShape.move(Vec3.of(10, 0, 0));

		frame.addShape(() -> {
			Quat rot = Quat.fromAxisDegrees(Vec3.YP, (System.currentTimeMillis() / 10d) % 360);
			return posed1.setPose((sh, p, r, s, c) -> {}, Vec3.ZP.scale(0.5).transform(rot), rot, 1);
		});

		CompositeSuperShape posed2 = superShape.move(Vec3.of(12, 0, 0));

		frame.addShape(() -> {
			return posed2.setPose((sh, p, r, s, c) -> {
				Object attachment = sh.getAttachment();
				if (attachment != null) {
					if (attachment.equals("head")) {
						c.setRot(Quat.fromAxisDegrees(Vec3.XP, 50));
					}
				}
			}, Vec3.ZERO, Quat.fromAxisDegrees(Vec3.YP, (System.currentTimeMillis() / 100d) % 360), 1);
		});

		return frame;
	}

	public static Demo3dFrame initCollision(Demo3dFrame frame) {
		Demo3dFrameCollision collision = new Demo3dFrameCollision(AABB.fromSize(.5)
			.rotate(Matrix3.SINGLE)
		, 200);
		frame.addShape(collision);

		return frame;
	}

	private static class Demo3dFrameCollision extends Demo3dShape {
		private final AABB staticBox = AABB.fromCenter(0, 0, 0, 1, 1, 1);
		private final Shape baseBox;
		private int move = 0;
		private final float speed;
		private DemoShape3dHolder[] shapes = new DemoShape3dHolder[]{new DemoShape3dHolder(this, this.staticBox), 
			new DemoShape3dHolder(this),
			new DemoShape3dHolder(this),
			new DemoShape3dHolder(this),
			new DemoShape3dHolder(this),
			new DemoShape3dHolder(this),
			new DemoShape3dHolder(this)
		};

		public Demo3dFrameCollision(Shape box, int speed) {
			this.baseBox = box;
			//this.movingBox = movingBox;
			//this.axis = axis;
			this.speed = 1f / speed;
			this.reset();
		}

		@Override
		public final void tick() {
			boolean collide = false;
			if (move != 0) {
				Direction[] directions = Direction.values();
				float speed = this.speed * this.move;
				for (int i = 1; i < this.shapes.length; i++) {
					Vec3 move = directions[i - 1].scale(speed);
					DemoShape3dHolder holder = this.shapes[i];
					Collision c = this.staticBox.collide(holder.getShape(), move);
					if (c != null) {
						this.staticBox.collide(holder.getShape(), move);
						if (directions[i - 1].getAsDoubleVec().equals(c.normal())) {
							System.err.println("wrong dir " + directions[i - 1]);
						}
						System.out.println("move dir " + directions[i - 1]);
						System.out.println(String.format(
							"{distance:%s, depth:%s, normal:%s, point:%s, direction:%s}",
							String.format("%.2f", c.distance()), c.depth(), c.normal(), c.point(), c.direction()
						));
						continue;
					}
					holder.setShape(holder.getShape().move(move));
				}
			}
			if (collide) {
				this.move = 0;
			}
		}

		@Override
		public void mousePressedM1(MouseEvent event) {
			this.move = 1;
		}

		@Override
		public void mouseReleasedM1(MouseEvent event) {
			this.move = 0;
		}
		
		@Override
		public void mousePressedM2(MouseEvent event) {
			this.move = -1;
		}

		@Override
		public void mouseReleasedM2(MouseEvent event) {
			this.move = 0;
		}

		@Override
		public void mouseClickedMiddle(MouseEvent event) {
			this.reset();
		}

		private void reset() {
			Direction[] directions = Direction.values();
			for (int i = 1; i < shapes.length; i++) {
				this.shapes[i].setShape(this.baseBox.move(directions[i - 1].getOpposite()));
			}
		}

		@Override
		public final Object getShape() {
			return this.shapes;
		}
	}
}
