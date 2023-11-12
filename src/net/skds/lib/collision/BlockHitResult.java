package net.skds.lib.collision;

import net.skds.lib.mat.Vec3;

public class BlockHitResult {

	public static final BlockHitResult MISS = createMissed(Vec3.ZERO, Direction.UP, BlockPos.ZERO);

	protected final Vec3 pos;

	private final Direction side;
	private final BlockPos blockPos;
	private final boolean missed;
	private final boolean insideBlock;

	public enum Type {
		MISS,
		BLOCK,
		ENTITY;
	}

	public static BlockHitResult createMissed(Vec3 pos, Direction side, BlockPos blockPos) {
		return new BlockHitResult(true, pos, side, blockPos, false);
	}

	public BlockHitResult(Vec3 pos, Direction side, BlockPos blockPos, boolean insideBlock) {
		this(false, pos, side, blockPos, insideBlock);
	}

	private BlockHitResult(boolean missed, Vec3 pos, Direction side, BlockPos blockPos, boolean insideBlock) {
		this.pos = pos;
		this.missed = missed;
		this.side = side;
		this.blockPos = blockPos;
		this.insideBlock = insideBlock;
	}

	public BlockHitResult withSide(Direction side) {
		return new BlockHitResult(this.missed, this.pos, side, this.blockPos, this.insideBlock);
	}

	public BlockHitResult withBlockPos(BlockPos blockPos) {
		return new BlockHitResult(this.missed, this.pos, this.side, blockPos, this.insideBlock);
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public Direction getSide() {
		return this.side;
	}

	public Type getType() {
		return this.missed ? Type.MISS : Type.BLOCK;
	}

	public boolean isInsideBlock() {
		return this.insideBlock;
	}
}
