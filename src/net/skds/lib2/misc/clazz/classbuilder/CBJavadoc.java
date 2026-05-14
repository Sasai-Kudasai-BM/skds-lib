package net.skds.lib2.misc.clazz.classbuilder;

import java.util.Arrays;
import java.util.List;

public record CBJavadoc(List<String> lines, boolean block) {

	public CBJavadoc(String... lines) {
		this(Arrays.asList(lines), true);
	}

	public CBJavadoc(String lines) {
		this((lines == null || lines.isEmpty()) ? List.of() : Arrays.asList(lines.split("\n")), true);
	}
	
	public CBJavadoc(String lines, boolean block) {
		this((lines == null || lines.isEmpty()) ? List.of() : Arrays.asList(lines.split("\n")), block);
	}
}
