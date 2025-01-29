package net.skds.lib2.utils.functester.api;

import java.awt.*;

public interface ITestFunction {

	Color DEF_COLOR = Color.BLUE;

	double calc(float input);

	default Color color() {
		return DEF_COLOR;
	}
}
