package net.skds.lib2.demo.classloader;

import lombok.CustomLog;
import net.skds.lib2.utils.classloader.JarClassLoader;
import net.skds.lib2.utils.logger.SKDSLogger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

@CustomLog
public class DemoClassloaderFrame extends JFrame {

	private TestClassloader classloader = new TestClassloader();
	private WeakReference<TestClassloader> ref = new WeakReference<>(classloader);
	private String testClass = "net.skds.lib2.demo.classloader.TestClass";
	private Class<?> cl;

	public DemoClassloaderFrame(Component parent) {
		super("Classloader demo");
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(300, 200));

		JButton button = new JButton("create new");
		button.addActionListener(e -> {
			classloader = new TestClassloader();
			System.gc();
			System.out.println("old is " + ref.get());
			ref = new WeakReference<>(classloader);
		});
		add(button);

		button = new JButton("load class");
		button.addActionListener(e -> {
			try {
				cl = classloader.findClass(testClass);
				System.out.println(cl + "#" + cl.hashCode());
				Class<?> cl2 = Class.forName(testClass);
				System.out.println(cl2 + "#" + cl.hashCode());
			} catch (ClassNotFoundException ex) {
				throw new RuntimeException(ex);
			}
		});
		add(button);

		button = new JButton("jar");
		button.addActionListener(e -> {

			JarClassLoader jcl = new JarClassLoader(Path.of("kek.jar"));

			for (String r : jcl.listResources()) {
				System.out.println(r);
			}

			try (InputStream is = jcl.getResourceAsStream("META-INF/MANIFEST.MF")) {
				Objects.requireNonNull(is);
				System.out.println(new String(is.readAllBytes(), StandardCharsets.UTF_8));
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
			try {
				SKDSLogger.waitForBusy();
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
			log.info("NOT_BUSY");
		});
		add(button);


		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight());
		setLocationRelativeTo(parent);
		setVisible(true);
	}
}
