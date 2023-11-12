package net.skds.lib.mat.graphics;

import net.skds.lib.mat.Quat;
import net.skds.lib.mat.Vec3;
import net.skds.lib.utils.exeption.StackOverflowExeption;
import net.skds.lib.utils.exeption.StackUnderflowExeption;

import java.util.ArrayDeque;
import java.util.Deque;

public class MatrixStack implements AutoCloseable {

	private final Deque<Matrix4f> stack = new ArrayDeque<>();

	private Matrix4f last;

	public MatrixStack(Matrix4f matrix) {
		last = matrix;
	}

	public MatrixStack() {
		last = new Matrix4f();
	}

	public MatrixStack translate(double x, double y, double z) {
		return translate((float) x, (float) y, (float) z);
	}

	public MatrixStack translate(Vec3 translation) {
		return translate((float) translation.x, (float) translation.y, (float) translation.z);
	}

	public MatrixStack translate(Vec3f translation) {
		return translate(translation.x, translation.y, translation.z);
	}

	public MatrixStack translate(float x, float y, float z) {
		last.multiplyByTranslation(x, y, z);
		return this;
	}

	public MatrixStack translateAbsolute(Vec3 translation) {
		translateAbsolute((float) translation.x, (float) translation.y, (float) translation.z);
		return this;
	}

	public MatrixStack translateAbsolute(double x, double y, double z) {
		translateAbsolute((float) x, (float) y, (float) z);
		return this;
	}

	public MatrixStack translateAbsolute(float x, float y, float z) {
		last.translateAbsolute(x, y, z);
		return this;
	}

	public MatrixStack scale(Vec3f scale) {
		return scale(scale.x, scale.y, scale.z);
	}

	public MatrixStack scale(float x, float y, float z) {
		last.mul(Matrix4f.makeScale(x, y, z));
		return this;
	}

	public MatrixStack scale(double x, double y, double z) {
		return scale((float) x, (float) y, (float) z);
	}

	public MatrixStack mul(Quat q) {
		last.mul(q);
		return this;
	}

	public MatrixStack mul(Quatf q) {
		last.mul(q);
		return this;
	}

	public MatrixStack mul(Matrix4f m) {
		last.mul(m);
		return this;
	}

	public Matrix4f getLast() {
		return last;
	}

	public MatrixStack push() {
		stack.push(last);
		last = last.copy();
		return this;
	}

	public MatrixStack pop() {
		last = stack.poll();
		if (last == null) {
			throw new StackUnderflowExeption();
		}
		return this;
	}

	@Override
	public void close() {
		if (!stack.isEmpty()) {
			throw new StackOverflowExeption();
		}
	}
}
