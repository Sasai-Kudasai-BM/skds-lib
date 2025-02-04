package net.w3e.lib.utils;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;

public class RobotUtils {
	
	public static final Robot ROBOT;

	static {
		try {
			ROBOT = new Robot();
		} catch (AWTException e) {
			throw new RuntimeException(e);
		}
	}

	public static void mouseMove(int x, int y) {
		ROBOT.mouseMove(x, y);
	}

	public static int[] mousePos() {
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		Point point = pointerInfo.getLocation();
		int xOld = (int)point.getX();
		int yOld = (int)point.getY();
		return new int[] {xOld, yOld};
	}
}
