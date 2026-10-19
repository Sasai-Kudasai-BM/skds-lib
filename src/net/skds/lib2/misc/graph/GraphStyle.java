package net.skds.lib2.misc.graph;

import net.skds.lib2.mat.vec2.Vec2;
import net.skds.lib2.mat.vec2.Vec2F;
import java.awt.*;

public class GraphStyle {
	private Vec2 scale = new Vec2F(100);
	private Vec2 step;
	private Vec2 center = Vec2.ZERO;

	public void setScale(Vec2 scale) {
		this.scale = scale;
		this.step = scale.scaleF(0.1F);
	}

	public void setScale(float scale) {
		setScale(new Vec2F(scale));
	}

	public Vec2 step() {
		return step;
	}

	public Vec2 scale() {
		return scale;
	}

	public Vec2 center() {
		return center;
	}

	public float getLineWidth(int index, boolean xAxis) {
		return (index % 5 == 0) ? 2.0F : 1.0F;
	}

	public Color getLineColor(int index, boolean xAxis) {
		return Color.DARK_GRAY;
	}

	public void setCenter(final Vec2 center) {
		this.center = center;
	}
}
