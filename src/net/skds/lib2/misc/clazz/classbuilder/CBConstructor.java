package net.skds.lib2.misc.clazz.classbuilder;

import java.util.List;

public final class CBConstructor extends CBMethod {

	public CBConstructor(int modifiers, CBType type, List<CBAnnotation> annotations, Object comment, List<CBArgument> arguments, CodeBody body) {
		super("<init>", modifiers, type, annotations, comment, arguments, body);
	}

	@Override
	public void write(StringBuilder sb) {
		if (comment != null) {
			TextClassBuilder.writeComment(comment, sb);
		}
		if (annotations != null) for (CBAnnotation a : annotations) {
			sb.append(a.toString()).append("\n\t");
		}
		TextClassBuilder.appendModifiers(sb, modifiers);
		sb.append(type);
		writeMethod(sb);
	}
}
