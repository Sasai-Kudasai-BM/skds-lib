package net.skds.lib2.misc.clazz.classbuilder;

import java.util.Arrays;
import java.util.List;

public record CBJavadoc(List<String> lines) {

	public CBJavadoc(String... lines) {
		this(Arrays.asList(lines));
	}

	public CBJavadoc(String lines) {
		this((lines == null || lines.isEmpty()) ? List.of() : Arrays.asList(lines.split("\n")));
	}
}
