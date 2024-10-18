package net.skds.lib.physics;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib.collision.Box;
import net.skds.lib.collision.IShape;
import net.skds.lib.collision.tree.BoundingTreeEntry;
import net.skds.lib.mat.FastMath;
import net.skds.lib.mat.Matrix3;
import net.skds.lib.mat.Quat;
import net.skds.lib.mat.Vec3;

public class PhysicalBody {

	public final PhysicalSpace space;
	public final Vec3 pos = new Vec3();
	public final Vec3 vel = new Vec3();
	public final Quat rot = new Quat();
	public final Matrix3 rotMatrix = new Matrix3();
	public final Vec3 spin = new Vec3();

	@Setter
	@Getter
	protected double mass = 1;
	@Setter
	@Getter
	protected double friction = .2;
	@Setter
	@Getter
	protected double bounce = .2;


	@Getter
	@Setter
	private IShape shape;

	@Setter
	@Getter
	private BoundingTreeEntry<PhysicalBody> bounding;

	private Box moveBox;

	public PhysicalBody(PhysicalSpace space, Vec3 pos, Quat rot, IShape shape) {
		this.space = space;
		this.shape = shape;
		setPos(pos);
		setRot(rot);
	}

	public boolean isStatic() {
		return false;
	}

	public void spawn() {
		if (this.bounding != null) {
			throw new IllegalStateException();
		}
		space.addBody(this);
	}

	public void remove() {
		if (this.bounding == null) {
			throw new IllegalStateException();
		}
		space.removeBody(this);
	}

	public void invalidate() {
		this.moveBox = null;
	}

	public void setPos(Vec3 pos) {
		this.pos.set(pos);
		this.shape.setPos(pos);
		invalidate();
	}

	public void setRot(Quat rot) {
		this.rot.set(rot);
		this.rotMatrix.set(rot);
		this.shape.setRotation(rotMatrix);
		invalidate();
	}

	public Box getBoundingBox() {
		return shape.getBoundingBox();
	}

	public Box getMovingBoundingBox() {
		if (moveBox == null) {
			double d = space.tickDuration();
			moveBox = getBoundingBox().stretch(vel.x * d, vel.y * d, vel.z * d);
		}
		return moveBox;
	}

	public Box getMovingBoundingBox(double d) {
		return getBoundingBox().stretch(vel.x * d, vel.y * d, vel.z * d);
	}

	public void tick(double duration) {
		vel.addMul(space.getG(pos), duration);

		//tryMove();

		//afterMove();
	}

	protected void move(double dd) {
		move(vel.copy().scale(dd * 0.9999));
		calcDrag(dd);
	}

	protected void move(Vec3 move) {
		this.pos.add(move);
		this.shape.setPos(pos);
		invalidate();
	}

	public void unstuck(CollisionResult cr) {
		Vec3 move = cr.normal.copy().scale(cr.depth);
		//if (cr.body.isStatic()) {
		move(move);
		afterMove();
		//} else {
		//	move(move);//.scale(0.50001));
		//	cr.body.move(move.inverse());
		//	afterMove();
		//	cr.body.afterMove();
		//}
	}

	protected void tryMove() {
		//double d = 0;
		//double duration = space.tickDuration();
		//int iteration = 0;
		//do {
		//	double k = 1 - d;
		//	Vec3 travel = vel.copy().scale(duration).scale(k);
		//	CollisionResult cr = space.checkCollisions(this, travel, k);
		//	if (cr != null) {
		//		if (cr.depth > 0) {
		//			//System.out.println("ebatt " + cr.depth + " " + cr.normal.yf);
		//			//move(cr.normal.copy().scale(cr.depth));
		//			break;
		//		}
		//		double dd = cr.distance * k;
		//		d += dd;
		//		travel.scale(cr.distance * 0.9999);
		//		move(travel);
		//		calcDrag(dd);
		//		onCollide(cr);
//
		//	} else {
		//		move(travel);
		//		calcDrag(k);
		//		break;
		//	}
		//	if (iteration++ >= 5) {
		//		System.out.println("iteration");
		//		break;
		//	}
		//} while (d < 1);
	}

	/**
	 * <a href="https://wikimedia.org/api/rest_v1/media/math/render/svg/9b2da910fe7a4ec1ccbd69504a206ddebcd10742">formula</a>
	 */

	public void onCollide(CollisionResult cr) {
		final PhysicalBody body = cr.body;
		final Vec3 dVel = vel.copy().sub(body.vel);
		double bounce = FastMath.avg(this.bounce, body.bounce);
		final Vec3 imp = dVel.copy().scale((1 + bounce) / (1 / mass + 1 / body.mass));
		Vec3 norm = cr.normal;
		double nProj = imp.dot(norm);

		double friction = FastMath.avg(this.friction, body.friction) * nProj;
		if (friction > 1) friction = 1;

		final Vec3 nImp = norm.copy().scale(nProj);
		final Vec3 tImp = imp.copy().sub(nImp).scale(friction);

		imp.set(nImp).add(tImp);
		//if (nProj < 0) {
		//	imp.inverse();
		//}
		body.applyImpulse(imp, cr.point);
		applyImpulse(imp.inverse(), cr.point);
	}

	public void applyImpulse(Vec3 imp, Vec3 point) {
		vel.add(imp.copy().div(mass));

		invalidate();
		afterMove();
	}

	protected void afterMove() {
		if (this.bounding == null) {
			throw new IllegalStateException();
		}
		space.getBodies().update(bounding, getMovingBoundingBox());
	}

	protected void calcDrag(double travel) {
		double tickMach = space.getMach(pos);
		double vel0 = vel.length();
		double velMach = vel0 / tickMach;
		double k = (velMach * getDragK()) + (velMach * velMach * getDragK2());
		k /= vel0;
		k *= space.getBaseAirDensity(pos);
		k *= travel;
		vel.scale(1 - k);
	}

	private double getDragK2() {
		return 0.001;
	}

	private double getDragK() {
		return 0.01;
	}
}
