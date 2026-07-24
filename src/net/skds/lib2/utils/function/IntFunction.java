package net.skds.lib2.utils.function;

import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
@FunctionalInterface
public interface IntFunction extends UnaryOperator<Integer> {

	int applyInt(int i);

	@Override
	default Integer apply(Integer i) {
		return applyInt(i);
	}
}
