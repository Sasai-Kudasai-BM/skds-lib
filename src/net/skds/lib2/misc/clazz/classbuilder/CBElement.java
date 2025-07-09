package net.skds.lib2.misc.clazz.classbuilder;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public sealed class CBElement permits CBMethod, CBField {

	public final String name;
	public final int modifiers;
	public final CBType type;
	public final List<CBAnnotation> annotations;
	public final Object comment;

	public void imports(TextClassBuilder classBuilder) {
		classBuilder.checkImport(type);
		if (annotations != null) for (CBAnnotation a : annotations) {
			classBuilder.checkImport(a.type());
		}
		classBuilder.checkImport(type);
	}

	public void write(StringBuilder sb) {
		if (comment != null) {
			TextClassBuilder.writeComment(comment, sb);
		}
		if (annotations != null) for (CBAnnotation a : annotations) {
			sb.append(a.toString()).append("\n\t");
		}
		TextClassBuilder.appendModifiers(sb, modifiers);
		sb.append(type).append(" ").append(name);
	}
}
