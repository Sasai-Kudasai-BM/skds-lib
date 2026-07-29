package net.skds.lib2.misc.clazz.classbuilder;

import java.util.List;

public final class CBField extends CBElement {

	public final CodeBody initializer;

	public CBField(String name, int modifiers, CBType type, List<CBAnnotation> annotations, Object comment, CodeBody initializer) {
		super(name, modifiers, type, annotations, comment);
		this.initializer = initializer;
	}

	@Override
	public void imports(TextClassBuilder classBuilder) {
		super.imports(classBuilder);
		if (initializer != null) initializer.imports(classBuilder);
	}

	@Override
	public void write(StringBuilder sb) {
		super.write(sb);

		if (initializer != null) {
			sb.append(" = ");
			TextClassBuilder.writeTabbed1(initializer.write(), sb);
		} else {
			sb.append(";");
		}
	}
}
