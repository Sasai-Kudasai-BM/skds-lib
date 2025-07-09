package net.skds.lib2.misc.clazz.classbuilder;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ClassType {
	CLASS("class"),
	INTERFACE("interface"),
	ENUM("enum"),
	RECORD("record"),
	ANNOTATION_INTERFACE("@interface");

	public final String declaration;
}
