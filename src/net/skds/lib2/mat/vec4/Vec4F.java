package net.skds.lib2.mat.vec4;

public record Vec4F(float xf, float yf, float zf, float wf) implements Vec4 {
	
	@Override
	public double x() {
		return this.xf;
	}

	@Override
	public double y() {
		return this.yf;
	}

	@Override
	public double z() {
		return this.zf;
	}

	@Override
	public double w() {
		return this.wf;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Vec4 vec) {
			return Vec4.equals(this, vec);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Vec4.hashCode(this);
	}
}
