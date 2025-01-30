package net.skds.lib2.mat;

import java.util.ArrayDeque;
import java.util.Deque;

import net.skds.lib2.mat.matrix4.Matrix4;
import net.skds.lib2.mat.matrix4.Matrix4F;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec4.Quat;
import net.skds.lib2.utils.exception.StackOverflowException;
import net.skds.lib2.utils.exception.StackUnderflowException;

public class MatrixStack implements AutoCloseable {
	private final Deque<Matrix4F> stack = new ArrayDeque<>();

	private Matrix4F last;

	public MatrixStack(Matrix4F matrix) {
		this.last = matrix;
	}

	public MatrixStack() {
		this.last = Matrix4F.SINGLE;
	}

	public MatrixStack translate(double x, double y, double z) {
		return translate((float) x, (float) y, (float) z);
	}

	public MatrixStack translate(Vec3 translation) {
		return translate(translation.xf(), translation.yf(), translation.zf());
	}

	public MatrixStack translate(float x, float y, float z) {
		this.last = last.translateF(x, y, z);
		return this;
	}

	public MatrixStack translateAbsolute(Vec3 translation) {
		return translateAbsolute(translation.xf(), translation.yf(), translation.zf());
	}

	public MatrixStack translateAbsolute(double x, double y, double z) {
		return translateAbsolute((float) x, (float) y, (float) z);
	}

	public MatrixStack translateAbsolute(float x, float y, float z) {
		this.last = last.translateAbsoluteF(x, y, z);
		return this;
	}

	public MatrixStack scale(Vec3 scale) {
		return scale(scale.xf(), scale.yf(), scale.zf());
	}

	public MatrixStack scale(float x, float y, float z) {
		this.last = last.scaleF(x, y, z);
		return this;
	}

	public MatrixStack scale(double x, double y, double z) {
		return scale((float) x, (float) y, (float) z);
	}

	public MatrixStack mul(Quat q) {
		this.last = last.multiplyF(q);
		return this;
	}

	public MatrixStack mul(Matrix4 m) {
		this.last = last.multiplyF(m);
		return this;
	}

	public Matrix4 getLast() {
		return this.last;
	}

	public MatrixStack push() {
		stack.push(this.last);
		return this;
	}

	public MatrixStack pop() {
		this.last = stack.poll();
		if (this.last == null) {
			throw new StackUnderflowException();
		}
		return this;
	}

	@Override
	public void close() {
		if (!stack.isEmpty()) {
			throw new StackOverflowException();
		}
	}
}
