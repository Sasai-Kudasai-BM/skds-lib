package net.skds.lib2.demo.demo3d;

import java.util.Iterator;
import java.util.List;

import lombok.CustomLog;
import net.skds.lib2.demo.demo3d.Demo3dShapeCollector.Demo3dShapeCollectorImpl;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.vec3.Direction;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec4.Quat;
import net.skds.lib2.shapes.AABB;
import net.skds.lib2.shapes.Collision;
import net.skds.lib2.shapes.CompositeSuperShape;
import net.skds.lib2.shapes.Shape;

@CustomLog
@SuppressWarnings("unused")
public class Demo3dFrameExample {

	// BotAiModule#lookupEnemies
	// HumanoidRig //sit//arms

	public static <T extends Demo3dShapeCollector> T init(T frame) {
		/*Vec3 a = Vec3.of(999, 1, 1);
		Vec3 b = Vec3.of(1, -1, 0);

		System.out.println(a.dot(b));*/

		initCollisionFuck(frame);
		return frame;
	}

	public static void initDefault(Demo3dShapeCollector frame) {
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
	}

	public static void initCollisionBox(Demo3dShapeCollector frame) {
		Demo3dFrameCollisionBox collision = new Demo3dFrameCollisionBox(AABB.fromSize(.5)
			.rotate(Matrix3.fromQuat(Quat.fromAxisDegrees(Vec3.XP, 45)))
		);
		frame.addShape(collision);
	}

	private static class Demo3dFrameCollisionBox extends Demo3dShape.Demo3dShapeInterractable {
		private final Shape baseBox;
		private final float speed;

		public Demo3dFrameCollisionBox(Shape box) {
			super(6);
			this.baseBox = box;
			this.speed = 1f / 200;
			this.reset();
		}

		@Override
		protected Shape initStaticBox() {
			return AABB.fromCenter(0, 0, 0, 1, -1, 1);
		}

		@Override
		public final void tick(Demo3dFrame demo) {
			boolean collide = false;
			if (move != 0) {
				Direction[] directions = Direction.values();
				float speed = this.speed * this.move;
				for (int i = 1; i < this.shapes.length; i++) {
					Vec3 move = directions[i - 1].scale(speed);
					DemoShape3dHolder holder = this.shapes[i];
					Shape shape = holder.getShape();
					if (shape == null) {
						continue;
					}
					Collision cr = collide(shape, move);
					if (cr != null) {
						log.info(cr);
						if (cr.normal().dot(move) > 0) {
							System.out.println("go out");
							//holder.setShape(shape.move(move.scale(cr.distance())));
						} else {
							if (directions[i - 1].getAsDoubleVec().equals(cr.normal())) {
								log.error("wrong dir " + directions[i - 1]);
							}
							System.out.println("move dir " + move);
							System.out.println("normal " + cr.normal());
							System.out.println(String.format(
								"{distance:%s, depth:%s, normal:%s, point:%s, direction:%s}",
								String.format("%.2f", cr.distance()), cr.depth(), cr.normal(), cr.point(), cr.direction()
							));
							System.out.println(shape.getClass());
							holder.setShape(shape.move(move.scale(cr.distance())));
							continue;
						}
					}
					holder.setShape(shape.move(move));
				}
			}
			if (collide) {
				this.move = 0;
			}
		}

		protected final void reset() {
			Direction[] directions = Direction.values();
			for (int i = 1; i < shapes.length; i++) {
				this.shapes[i].setShape(this.baseBox.move(directions[i - 1].getOpposite()));
			}
			//this.shapes[1].setShape(AABB.fromSize(.5, 0, 0.5).move(directions[0].getOpposite().scale(1)));
			//this.shapes[1].setShape(this.baseBox.move(directions[0].getOpposite().scale(1)));
		}
	}

	public static void initCollisionPeople(Demo3dShapeCollector frame) {
		Demo3dFrameCollisionPeople collision = new Demo3dFrameCollisionPeople();
		frame.addShape(collision);
	}

	private static class Demo3dFrameCollisionPeople extends Demo3dShape.Demo3dShapeInterractable {

		private static final float PLATFORM_SIZE = 5f;
		private static final float HEIGHT = 1f;
		private static final float WIDTH = 0.1f;

		private final AABB peopleBox;
		private boolean onGround;

		public Demo3dFrameCollisionPeople() {
			float wallY = HEIGHT / 2 - WIDTH * 2;

			float objY = wallY + WIDTH / 2;
			float objw = WIDTH * 5;

			float objP = PLATFORM_SIZE / 2;

			float h2 = HEIGHT / 2;

			//this.peopleBox = AABB.fromCenter(0, objY + 1, 0, objw, h2, objw).withAttachment("human");
			this.peopleBox = AABB.fromCenter(objP / 2 + 0.1, objY, objP / 3, objw, h2, objw).withAttachment("human");

			this.reset();
		}

		@Override
		protected Shape initStaticBox() {
			float s2 = PLATFORM_SIZE / 2 + WIDTH / 2;
			float wallY = HEIGHT / 2 - WIDTH * 2;

			float objY = wallY + WIDTH / 2;
			float objw = WIDTH * 5;

			float objP = PLATFORM_SIZE / 2;
			float h2 = HEIGHT / 2;

			float xUp = -objP / 2;
			float zUp = -objP / 5 * 3;
			float heightUp = h2 / 4;

			float dxUp = objw * 1.1f;
			float dsUp = heightUp / 2;

			return CompositeSuperShape.of(new Shape[]{
				AABB.fromCenter(0, -WIDTH, 0, PLATFORM_SIZE, WIDTH, PLATFORM_SIZE),

				AABB.fromCenter(0, wallY, s2, PLATFORM_SIZE, HEIGHT, WIDTH),
				AABB.fromCenter(s2, wallY, 0, WIDTH, HEIGHT, PLATFORM_SIZE),
				AABB.fromCenter(0, wallY, -s2, PLATFORM_SIZE, HEIGHT, WIDTH),
				AABB.fromCenter(-s2, wallY, 0, WIDTH, HEIGHT, PLATFORM_SIZE),

				AABB.fromCenter(objP / 2, objY, objP / 3, objw, HEIGHT, objw),
				AABB.fromCenter(-objP / 2, objY + h2, objP / 5 * 3, objw, h2, objw),

				AABB.fromCenter(xUp, 0, zUp, objw, heightUp, objw),
				//AABB.fromCenter(xUp, heightUp, zUp - dsUp / 2, objw, heightUp * 5, objw),
				AABB.fromCenter(xUp + dxUp, h2, zUp, objw, heightUp, objw),
				AABB.fromCenter(xUp - dxUp, h2 + dsUp, zUp, objw, heightUp, objw),
			}, Vec3.of(0, -.1, 0));
		}

		@Override
		protected void tick(Demo3dFrame demo) {
			if (move == 0) {
				return;
			}
			Collision cr = collide(Vec3.YN);
			onGround = false;
			if (cr != null && cr.distance() == 0) {
				onGround = true;
			}
			Vec3 pos = this.getHuman().getCenter();
			tickMove(demo);
			log.debug(this.getHuman().getCenter().sub(pos));
		}

		private void tickMove(Demo3dFrame demo) {
			if (move != 0) {
				Shape human = this.shapes[1].getShape();

				float yaw = demo.getCameraYaw();
				if (this.move > 0) {
					yaw += 180;
				}

				Vec3 vel = Vec3.of(0, -0.01, 0);
				if (this.onGround) {
					vel = vel.add(Vec3.ZN.scale(0.02).rotateYaw(yaw));
				}

				double depth = 1;

				boolean hitGround = false;

				//log.warn("tick");

				int i = 5;

				while (depth > 1E-7 && vel.lengthSquared() > 1E-7) {
					i--;
					if (i == 0) {
						log.error("fuck error");
						break;
					}
					//System.out.println();
					Vec3 way = vel.scale(depth);
					//System.out.println("way " + way);
					Collision cr = collide(human, way);
					if (cr != null && cr.normal().dot(way) <= 0) {
						//log.info(cr);
						if (cr.depth() != 0) {
							//human = human.move(cr.normal().scale(cr.depth()));
							//log.error("break");
							//break;
						}
						if (cr.normal().x() != 0) {
							vel = vel.scale(0, 1, 1);
						}
						if (cr.normal().z() != 0) {
							vel = vel.scale(1, 1, 0);
						}
						if (cr.normal().y() != 0) {
							if (cr.normal().y() > 0 && vel.y() <= 0) {
								hitGround = true;
								vel = vel.scale(0.5, 0, 0.5);
							} else {
								vel = vel.scale(1, 0, 1);
							}
						}
						//if (cr.depth() != 0) {
						//	hitGround = true;
						//	vel = vel.scale(1, 0, 1);
						//}

						depth *= (1 - cr.distance());
						human = human.move(way.scale(cr.distance()));
					} else {
						human = human.move(way);
						//System.out.println("done");
						break;
					}
				}
	
				//System.out.println("onGround " + hitGround);
				this.shapes[1].setShape(human);
				this.onGround = hitGround;
			}
		}

		private float getClimbHeight() {
			return 0.25f;
		}

		private void upTick(Demo3dFrame demo) {
			if (this.move == 0) {
				return;
			}
			AABB human = getHuman();
			if (!this.onGround) {
				Vec3 move = Vec3.of(0, -0.02, 0);
				Collision cr = this.staticBox.collide(human, move);
				if (cr == null || cr.direction() == Direction.UP) {
					if (cr != null) {
						if (cr.distance() == 0) {
							move = null;
							onGround = true;
						} else {
							move = move.scale(cr.distance());
						}
					}
					if (!onGround) {
						this.shapes[1].setShape(human.move(move));
					}
				}
				return;
			}
			float yaw = demo.getCameraYaw();
			if (this.move > 0) {
				yaw += 180;
			}
			Vec3 move = Vec3.ZN.scale(0.3 * demo.getSpeed()).rotateYaw(yaw);

			boolean wasUp = false;

			System.out.println();
			System.out.println(move);

			final float climbHeight = this.getClimbHeight();

			if (move.y() != 0) {
				move = Vec3.of(move.x(), 0, move.z());
			}
			Vec3 normal = null;

			final double sizeX = human.sizeX();
			final double sizeZ = human.sizeX();

			f1:
			for (int i = 0; i < 4; i++) {
				if (normal != null) {
					int x = 1;
					int z = 1;
					if (normal.x() != 0) {
						x = 0;
					}
					if (normal.z() != 0) {
						z = 0;
					}
					move = move.scale(x, 1, z);
				}
				if (move.lengthSquared() <= 1E-7) {
					System.out.println("too low");
					break f1;
				}
				final Collision cr = collide(human, move);
				normal = null;
				if (cr != null) {
					if (cr.distance() >= 1E-7) {
						human = human.move(move.scale(cr.distance() * 0.9999999));
						move = move.scale(1 - cr.distance());
						i--;
						continue f1;
					}
					normal = cr.normal();
					if (climbHeight <= 0) {
						continue f1;
					}

					// подъем
					double climb = 0;
					AABB humanMoved = new AABB(human.minX, human.minY, human.minZ, human.maxX, human.minY + 0.01, human.maxZ);
					Collision cr1 = cr;
					while (cr1 != null && cr1.distance() < 1E-7) {
						AABB block = (AABB)cr1.shapeA();
						double climbL = block.maxY - humanMoved.minY;
						climb += climbL;
						if (climb > climbHeight) {
							continue f1;
						}
						humanMoved = humanMoved.move(0, climbL, 0);
						cr1 = collide(humanMoved, move);
					}
					if (climb == 0) {
						log.error("climb must be positive");
						return;
					}
					
					// вперед
					Vec3 moveForward = move;
					Vec3 moveForwardAbs = moveForward.abs();
					if (moveForwardAbs.x() > sizeX) {
						moveForward = moveForward.scale(sizeX / moveForwardAbs.x());
					}
					if (moveForwardAbs.z() > sizeZ) {
						moveForward = moveForward.scale(sizeZ / moveForwardAbs.z());
					}

					final Collision crForward = collide(humanMoved, moveForward);
					if (crForward != null) {
						moveForward = moveForward.scale(crForward.distance());
					}
					humanMoved = humanMoved.move(moveForward);

					// вниз
					/*Vec3 downMove = Vec3.of(0, -climbHeight, 0);
					final Collision crDown = collide(humanMoved, downMove);
					if (crDown == null) {
						System.out.println("down");
						continue f1;
					}
					downMove = downMove.scale(crDown.distance());
					humanMoved = humanMoved.move(downMove);*/

					// увеличить размеры
					humanMoved = new AABB(humanMoved.minX, humanMoved.minY, humanMoved.minZ, humanMoved.maxX, humanMoved.minY + human.sizeY(), humanMoved.maxZ);

					// коллизия назад
					Vec3 moveBackward = moveForward.inverse();
					final Collision crBackward = collide(humanMoved, moveBackward);
					if (crBackward != null) {
						System.out.println("back");
						continue f1;
					}

					// коллизия вниз
					humanMoved = humanMoved.move(moveBackward);
					Vec3 moveDownToSelf = human.getCenter().sub(humanMoved.getCenter());
					final Collision crDownToSelf = collide(humanMoved, moveDownToSelf);
					if (crDownToSelf != null && crDownToSelf.distance() != 1 && crDownToSelf.direction() != Direction.UP) {
						System.out.println("self");
						continue f1;
					}

					wasUp = true;

					human = human.move(moveDownToSelf.inverse().add(moveForward));
					if (crForward == null) {
						break f1;
					} else {
						move = move.sub(moveForward);
						normal = null;
					}
				} else {
					human = human.move(move);
					break f1;
				}
			}

			if (wasUp) {
				this.move = 0;
			}

			setHuman(human);
		}

		/*private void upMove(Demo3dFrame demo) {
			if (move == 0) {
				return;
			}
			log.info("up");
			float yaw = demo.getCameraYaw();
			if (this.move > 0) {
				yaw += 180;
			}
			Vec3 move = Vec3.ZN.scale(0.02).rotateYaw(yaw);
			Shape human = this.shapes[1].getShape();
			f1:
			for (int i = 0; i < 4; i++) {
				//Box box = getMoveBox();
				//AABB box = human.getBoundingBox();
				var cr = collide(human, move);
				if (cr != null) {
					boolean cx = cr.normal().x() != 0;
					boolean cy = cr.normal().y() != 0;
					boolean cz = cr.normal().z() != 0;

					float h = getClimbHeight();
					l1:
					if (h > 0 && (cx || cz) && (onGround || move.y() < 0)) {
						Vec3 moveFUp = move.add(0, h, 0);
						var cr2 = collide(human, move);
						double maxD = cr.distance();
						if (cr2 != null) {
							if (cr2.distance() > maxD) {
								maxD = cr2.distance();
							}
							if (cr2.depth() != 0) {
								System.out.println("xxx");
							}
						} else {
							maxD = 1;
						}
						moveFUp = moveFUp.scale(maxD);

						if (maxD < 1) {
							Vec3 moveFUp2 = Vec3.of(0, h, 0);
							var cr3 = collide(human, moveFUp2);
							if (cr3 != null) {
								moveFUp2 = moveFUp2.scale(cr3.distance());
							}
							Shape boxU = human.move(moveFUp2);
							var cr4 = collide(boxU, move);
							if (cr4 != null && cr3 == null && cr4.distance() == 0) {
								moveFUp2 = Vec3.of(moveFUp2.x(), h * .5, moveFUp2.z());
								boxU = human.move(moveFUp2);
								cr4 = collide(boxU, move);
							}
							double d = cr4 != null ? cr4.distance() : 1;
							if (maxD < 1E-7 && d < 1E-7) break l1;
							if (d > maxD) {
								maxD = d;
								moveFUp = moveFUp2.add(move.scale(maxD));
							}
						}

						if (maxD > 1E-7) {
							move = moveFUp;
							human = human.move(move);
							Collision rat = collide(human, Vec3.ZERO);
							if (rat != null) {
								log.warn(rat);
							}
							break f1;
						}
					}

					human = human.move(move.scale(cr.distance()));

					if (cr.depth() > 1E-7) {
						System.out.println("add");
						human = human.move(cr.normal().scale(cr.depth()));
					}

					Collision rat = collide(human, Vec3.ZERO);
					if (rat != null) {
						log.warn(rat);
					}

					if (cx) {
						if (cr.normal().x() * move.x() < 0) {
							move = move.scale(0, 1, 1);
						}
					}
					if (cy) {
						if (cr.normal().y() * move.y() < 0) {
							move = move.scale(1, 0, 1);
						}
					}
					if (cz) {
						if (cr.normal().z() * move.z() < 0) {
							move = move.scale(1, 1, 0);
						}
					}
					move = move.scale(1 - cr.distance());
					Collision rat1 = collide(human, Vec3.ZERO);
					if (rat1 != null) {
						log.warn(rat1);
					}
					if (move.lengthSquared() < 1E-6) {
						break f1;
					}
				} else {
					human = human.move(move);
					Collision rat = collide(human, Vec3.ZERO);
					if (rat != null) {
						log.warn(rat);
					}
					break f1;
				}
			}
			shapes[1].setShape(human);
		}*/

		@Override
		protected final void reset() {
			this.shapes[1].setShape(this.peopleBox);
			this.onGround = false;
		}
	}

	public static void initCollisionFuck(Demo3dShapeCollector frame) {
		FuckCollide collision = new FuckCollide();
		frame.addShape(collision);
	}

	private static class FuckCollide extends Demo3dShape.Demo3dShapeInterractable {

		public FuckCollide() {
			reset();
		}

		@Override
		protected Shape initStaticBox() {
			return AABB.ONE;
		}

		@Override
		protected void tick(Demo3dFrame demo) {
			if (this.move == 0) {
				return;
			}
			Collision cr = this.collide(Vec3.of(0.03321531436902421, 0.535343, 0.2219234683112594));
			System.out.println(cr);
			this.move = 0;
		}

		@Override
		protected void reset() {
			Demo3dShapeCollectorImpl shapes = new Demo3dShapeCollectorImpl();
			Demo3dToolPanel.read(shapes);
			Iterator<Shape> iterator = shapes.array.iterator();
			Shape human = null;
			while (iterator.hasNext()) {
				Shape next = iterator.next();
				if (next.getAttachment() instanceof String key) {
					if (key.contains("eblab")) {
						human = next;
					}
					iterator.remove();
				}
			}
			this.shapes[0].setShape(CompositeSuperShape.of(shapes.array.toArray(Shape[]::new), Vec3.ZERO));
			this.setHuman(human);
		}
	}
}
