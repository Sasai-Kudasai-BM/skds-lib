package net.skds.lib2.misc.clazz.classbuilder;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CBArgument {

	public final CBType type;
	public final String name;
	public final List<CBAnnotation> annotations;
	public final String comment;

	public CBArgument(CBField field) {
		this(field.type, field.name, null, null);
	}

	public CBArgument(Class<?> t, String name) {
		this(CBType.of(t), name, null, null);
	}

	public CBArgument(Class<?> t, String name, List<CBAnnotation> annotations, String comment) {
		this(CBType.of(t), name, annotations, comment);
	}

	public static void imports(List<? extends CBArgument> arguments, TextClassBuilder classBuilder) {
		if (arguments != null) for (CBArgument arg : arguments) {
			if (arg.annotations != null) for (CBAnnotation annotation : arg.annotations) {
				classBuilder.checkImport(annotation.type());
			}
			classBuilder.checkImport(arg.type);
		}
	}

	public static void writeMethod(List<? extends CBArgument> arguments, StringBuilder sb) {
		if (arguments != null && !arguments.isEmpty()) {
			for (CBArgument arg : arguments) {
				if (arg.comment != null && !arg.comment.isEmpty()) {
					sb.append("/* ").append(arg.comment).append(" */ ");
				}
				if (arg.annotations != null) for (CBAnnotation annotation : arg.annotations) {
					sb.append(annotation).append(" ");
				}

				arg.writeMethod(sb);
			}
			sb.setLength(sb.length() - 2);
		}
	}

	protected void writeMethod(StringBuilder sb) {
		sb.append(this.type).append(" ").append(this.name).append(", ");
	}

}
