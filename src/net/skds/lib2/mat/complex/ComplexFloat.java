package net.skds.lib2.mat.complex;

public record ComplexFloat(float re, float im) {

	public ComplexFloat add(float re, float im) {
		return new ComplexFloat(this.re + re, this.im + im);
	}

	public ComplexFloat add(ComplexFloat other) {
		return new ComplexFloat(this.re + other.re, this.im + other.im);
	}

	public ComplexFloat mul(float re, float im) {
		return new ComplexFloat(this.re * re - this.im * im, this.re * im + this.im * re);
	}

	public ComplexFloat mul(ComplexFloat other) {
		return new ComplexFloat(this.re * other.re - this.im * other.im, this.re * other.im + this.im * other.re);
	}

	public ComplexFloat sub(float re, float im) {
		return new ComplexFloat(this.re - re, this.im - im);
	}

	public ComplexFloat sub(ComplexFloat other) {
		return new ComplexFloat(this.re - other.re, this.im - other.im);
	}

	public ComplexFloat inverse() {
		return new ComplexFloat(-this.re, -this.im);
	}

	public ComplexFloat conjugate() {
		return new ComplexFloat(this.re, -this.im);
	}
}
