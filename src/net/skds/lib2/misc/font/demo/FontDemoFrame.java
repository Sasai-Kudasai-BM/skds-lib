package net.skds.lib2.misc.font.demo;

import net.skds.lib2.misc.font.FontTriangulator;
import net.skds.lib2.utils.SKDSUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

public class FontDemoFrame extends JFrame {

	private final FontTriangulator triangulator;
	private final SVGDrawPanel drawPanel;

	public FontDemoFrame() {
		super("Font demo");


		this.triangulator = new FontTriangulator();
		this.drawPanel = new SVGDrawPanel();
		//add(drawPanel);

		JButton button = new JButton("sex");
		button.addActionListener(e -> test());
		add(button);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(100, 100));
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void test() {

		Font f = new Font(Font.DIALOG, Font.PLAIN, 20);
		FontRenderContext frc = new FontRenderContext(null, false, false);
		var t = SKDSUtils.startTimeMeasure();
		int n = f.getNumGlyphs();
		int j = 0;
		char[] array = new char[n];
		for (char i = '\u0000'; i < '\uFFFF' && j < n; i++) {
			if (f.canDisplay(i)) {
				array[j] = i;
				j++;
			}
		}
		System.out.println(t.query());
		t = SKDSUtils.startTimeMeasure();
		GlyphVector glyphVector = f.createGlyphVector(frc, array);

		GeneralPath s = (GeneralPath) glyphVector.getGlyphOutline(10);
		System.out.println(t.query());

		System.out.println(new String(array));
		PathIterator iterator = s.getPathIterator(null);
		float[] buffer = new float[6];
		while (!iterator.isDone()) {
			iterator.currentSegment(buffer);
		}

		System.out.println();
	}

}
