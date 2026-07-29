package net.skds.lib2.misc.clazz.classbuilder;

import java.util.List;

public sealed class CBElement extends CBAbstractElement permits CBMethod, CBField {

	public final int modifiers;
	public final CBType type;

	public CBElement(String name, int modifiers, CBType type, List<CBAnnotation> annotations, Object comment) {
		super(name, annotations, comment);
		this.modifiers = modifiers;
		this.type = type;
	}

	public void imports(TextClassBuilder classBuilder) {
		classBuilder.checkImport(type);
		super.imports(classBuilder);
	}

	public void write(StringBuilder sb) {
		super.write(sb);
		TextClassBuilder.appendModifiers(sb, modifiers);
		sb.append(type).append(" ").append(name);
	}
}
