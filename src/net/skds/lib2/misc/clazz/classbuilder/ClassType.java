package net.skds.lib2.misc.clazz.classbuilder;

public enum ClassType {
	CLASS("class"),
	INTERFACE("interface"),
	ENUM("enum"),
	RECORD("record"),
	ANNOTATION_INTERFACE("@interface");

	public final String declaration;

	private ClassType(final String declaration) {
		this.declaration = declaration;
	}
}
