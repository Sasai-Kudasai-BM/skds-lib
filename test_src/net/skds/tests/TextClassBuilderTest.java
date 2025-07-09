package net.skds.tests;

import net.skds.lib2.misc.clazz.classbuilder.*;
import net.skds.lib2.utils.AutoString;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.lang.annotation.Retention;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextClassBuilderTest {

	public static void main(String[] args) {
		SKDSLogger.replaceOuts();


		TextClassBuilder tcb = new TextClassBuilder("net.skds.tests", "Test", ClassType.CLASS)
				.extend(HashMap.class)
				.implement(AutoString.class)
				.addElement("""
						ebt tvo
						hui
						""")
				.addElement("lol")
				.addElement(new CBMethod(
						"mtd0",
						Modifier.PUBLIC | Modifier.FINAL,
						CBType.of(void.class),
						List.of(new CBAnnotation(Override.class)),
						new CBJavadoc("method", "doc"),
						List.of(new CBMethod.Arg(int.class, "i")),
						new CodeBody(List.of("System.out.println(i);"), null)
				))
				.addElement(new CBMethod(
						"mtd1",
						Modifier.PUBLIC | Modifier.STATIC,
						CBType.of(int.class),
						List.of(new CBAnnotation(Override.class)),
						new CBJavadoc("method", "doc"),
						List.of(
								new CBMethod.Arg(int.class, "i"),
								new CBMethod.Arg(int.class, "j", List.of(
										new CBAnnotation(
												CBType.of(Override.class),
												null,
												Map.of(
														"arg", new CBAnnotation(Retention.class),
														"arg2", new CBAnnotation(Deprecated.class)
												)
										)
								), "sex")
						),
						new CodeBody(List.of(
								"System.out.println(i);",
								"return i + 2 * j;"
						), null)
				))
				.addElement(new CBJavadoc("sex", "@throws wtf"))
				.addElement(new TextClassBuilder("net.skds.tests", "Test2", ClassType.CLASS)
						.setStatic(true)
						.setJavadoc(new CBJavadoc("penis semen", "666"))
				)
				.addElement("lol2")
				.setJavadoc(new CBJavadoc("amo gus 1488"));
		System.out.println(tcb);
	}
}
