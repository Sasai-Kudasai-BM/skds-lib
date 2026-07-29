package net.skds.lib2.misc.clazz.classbuilder;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public sealed class CBAbstractElement permits CBElement, CBEnumElement {

	public final String name;
	public final List<CBAnnotation> annotations;
	public final Object comment;

	public void imports(TextClassBuilder classBuilder) {
		if (annotations != null) for (CBAnnotation a : annotations) {
			classBuilder.checkImport(a.type());
		}
	}

	public void write(StringBuilder sb) {
		if (comment != null) {
			TextClassBuilder.writeComment(comment, sb);
		}
		if (annotations != null) for (CBAnnotation a : annotations) {
			sb.append(a.toString()).append("\n\t");
		}
	}
}
