package net.skds.lib.physics;

import net.skds.lib.collision.ConvexCollision;
import net.skds.lib.collision.Direction;
import net.skds.lib.collision.IShape;
import net.skds.lib.mat.Vec3;

public class CollisionResult extends ConvexCollision.CollisionResult {

	public final PhysicalBody body;

	public CollisionResult(double distance, Vec3 normal, Vec3 point, Direction direction, IShape shape, PhysicalBody body) {
		super(distance, normal, point, direction, shape);
		this.body = body;
	}

	public CollisionResult(ConvexCollision.CollisionResult cr, PhysicalBody body) {
		super(cr.distance, cr.depth, cr.normal, cr.point, cr.direction, cr.getShape());
		this.body = body;
	}
}
