package net.skds.lib2.demo.demo3d;

import javax.swing.*;

import lombok.CustomLog;
import net.skds.lib2.demo.demo3d.Demo3dShapeCollector.Demo3dShapeCollectorImpl;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.shapes.AABB;
import net.skds.lib2.shapes.AABBBuilder;
import net.skds.lib2.shapes.Collision;
import net.skds.lib2.shapes.Shape;

import java.awt.*;

@CustomLog
public class Demo3dToolPanel extends JPanel {

	private final Demo3dFrame demo;

	public Demo3dToolPanel(Demo3dFrame demo) {
		this.demo = demo;

		this.setLayout(new BorderLayout());

		GridLayout layout = new GridLayout(2, 3);

		JPanel downPanel = new JPanel(layout);

		JCheckBox fillCheckBox = new JCheckBox("fill");
		fillCheckBox.addItemListener(e -> {
			this.demo.setFill(fillCheckBox.isSelected());
		});
		downPanel.add(fillCheckBox);

		JCheckBox sortCheckBox = new JCheckBox("sort");
		sortCheckBox.addItemListener(e -> {
			this.demo.setSort(sortCheckBox.isSelected());
		});
		downPanel.add(sortCheckBox);

		JButton collideButton = new JButton("collide");
		collideButton.addActionListener(e -> {
			Demo3dShapeCollectorImpl collector = new Demo3dShapeCollectorImpl();
			Demo3dShape hovered = demo.getHovered();
			if (hovered == null) {
				log.warn("hover is null");
				return;
			}
			for (Demo3dShape shape : demo.shapes) {
				if (shape != hovered) {
					collector.addShape(shape);
				}
			}
			Shape h = hovered.getHovered().getShape();
			System.out.println("collide " + h.getAttachment());
			for (Shape shape : collector.array) {
				if (shape == h) {
					continue;
				}
				Collision cr = shape.collide(h, Vec3.ZERO);
				if (cr != null) {
					System.out.println(cr);
				}
			}
		});
		downPanel.add(collideButton);

		JButton resetButton = new JButton("reset");
		resetButton.addActionListener(e -> {
			Demo3dShapeCollectorImpl collector = new Demo3dShapeCollectorImpl();
			for (Demo3dShape shape : demo.shapes) {
				collector.addShape(shape);
			}
			
			AABB aabb = null;
			if (collector.array.isEmpty()) {
				aabb = AABB.EMPTY;
			} else {
				AABBBuilder b = new AABBBuilder(collector.array.getFirst().getBoundingBox());
				for (Shape shape : collector.array) {
					b.expand(shape.getBoundingBox());
				}
				aabb = b.build();
			}

			this.demo.setCameraPos(aabb.getBoundingBox().getCenter());
			this.demo.setCameraYaw(0);
			this.demo.setCameraPitch(0);
			this.repaint();
		});
		downPanel.add(resetButton);

		JButton saveButton = new JButton("save");
		saveButton.addActionListener(e -> {
			Demo3dShapeCollectorImpl collector = new Demo3dShapeCollectorImpl();
			for (Demo3dShape shape : demo.shapes) {
				collector.addShape(shape);
			}
			JsonUtils.saveJson("demo/shapes.json", collector);
		});
		downPanel.add(saveButton);

		JButton readButton = new JButton("read");
		readButton.addActionListener(e -> {
			Demo3dShapeCollectorImpl collector = JsonUtils.readJson("demo/shapes.json", Demo3dShapeCollectorImpl.class);
			demo.shapes.clear();
			for (Shape shape : collector.array) {
				demo.addShape(shape);
			}
			demo.renderPanel.repaint();
		});
		downPanel.add(readButton);

		downPanel.validate();
		downPanel.setPreferredSize(downPanel.getPreferredSize());

		this.add(downPanel, BorderLayout.SOUTH);

		setBackground(Color.LIGHT_GRAY);
		setPreferredSize(new Dimension(300, 0));
	}

	public static void read(Demo3dShapeCollector shapes) {
		Demo3dShapeCollectorImpl collector = JsonUtils.readJson("demo/shapes.json", Demo3dShapeCollectorImpl.class);
		shapes.clear();
		for (Shape shape : collector.array) {
			shapes.addShape(shape);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Vec3 cameraPos = this.demo.getCameraPos();

		int y = 0;
		int d = 20;

		Font font = g.getFont().deriveFont( 20.0f );
		g.setFont(font);

		g.drawString(String.format("Pos: [%s, %s, %s] (w,a,s,d)", 
				String.format("%.1f", cameraPos.x()), 
				String.format("%.1f", cameraPos.y()), 
				String.format("%.1f", cameraPos.z())
		), 5, y += d);

		g.drawString(String.format("Rot: [%s, %s] (mouse)", 
				String.format("%.1f", this.demo.getCameraYaw()), 
				String.format("%.1f", this.demo.getCameraPitch())
		), 5, y += d);

		g.drawString(String.format("Spd: %s (scroll)", 
				String.format("%.2f", this.demo.getSpeed())
		), 5, y += d);

		g.drawString(String.format("Fov: %s (shift + scroll)", 
				String.format("%.0f", this.demo.getCameraFov())
		), 5, y += d);
	}
}
