package net.skds.lib2.utils.function;

import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
@FunctionalInterface
public interface FloatFunction extends UnaryOperator<Float> {

	float applyFloat(float f);

	@Override
	default Float apply(Float aFloat) {
		return applyFloat(aFloat);
	}
}
