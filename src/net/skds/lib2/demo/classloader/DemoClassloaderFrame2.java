package net.skds.lib2.demo.classloader;

import lombok.CustomLog;
import net.skds.lib2.misc.clazz.StringClassLoader;
import net.skds.lib2.reflection.ReflectUtils;

import javax.swing.*;
import java.awt.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.function.Supplier;

@CustomLog
public class DemoClassloaderFrame2 extends JFrame {

	private StringClassLoader classloader = new StringClassLoader();
	private WeakReference<StringClassLoader> ref = new WeakReference<>(classloader);
	private final String testClassName = "net.skds.lib2.demo.classloader.TestClass";
	private final String testClassSource = """
			package net.skds.lib2.demo.classloader;
						
			public class TestClass {
				public void test() {
			  		System.out.println("ok");
				}
			}
			""";

	public DemoClassloaderFrame2(Component parent) {
		super("StringClassloader");
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(300, 200));

		JButton button = new JButton("create new");
		button.addActionListener(e -> {
			classloader = new StringClassLoader();
			System.out.println("before gc old is " + ref.get());
			System.gc();
			System.out.println("after gc old is " + ref.get());
			ref = new WeakReference<>(classloader);
		});
		add(button);

		button = new JButton("load class");
		button.addActionListener(e -> {
			try {
				Class<?> cl = classloader.load(testClassSource, testClassName);
				System.out.println(cl + "#" + cl.hashCode());
				Class<?> cl2 = Class.forName(testClassName);
				System.out.println(cl2 + "#" + cl.hashCode());

				Supplier<?> constructor = ReflectUtils.getConstructor(cl);
				assert constructor != null;
				Object o = constructor.get();
				System.out.println(o);
				Method m = cl.getMethod("test");
				m.invoke(o);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});
		add(button);


		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight());
		setLocationRelativeTo(parent);
		setVisible(true);
	}
}
