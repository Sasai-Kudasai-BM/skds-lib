package net.skds.lib2.misc.clazz.classbuilder;

import java.util.List;

public final class CBMethod extends CBElement {

	public final List<Arg> arguments;
	public final CodeBody body;

	public CBMethod(String name, int modifiers, CBType type, List<CBAnnotation> annotations, Object comment, List<Arg> arguments, CodeBody body) {
		super(name, modifiers, type, annotations, comment);
		this.arguments = arguments;
		this.body = body;
	}

	@Override
	public void imports(TextClassBuilder classBuilder) {
		super.imports(classBuilder);
		if (arguments != null) for (Arg arg : arguments) {
			if (arg.annotations != null) for (CBAnnotation annotation : arg.annotations) {
				classBuilder.checkImport(annotation.type());
			}
			classBuilder.checkImport(arg.type());
		}
		body.imports(classBuilder);
	}

	@Override
	public void write(StringBuilder sb) {
		super.write(sb);

		sb.append("(");

		if (arguments != null && !arguments.isEmpty()) {
			for (Arg arg : arguments) {
				if (arg.comment != null && !arg.comment.isEmpty()) {
					sb.append("/* ").append(arg.comment).append(" */ ");
				}
				if (arg.annotations != null) for (CBAnnotation annotation : arg.annotations) {
					sb.append(annotation).append(" ");
				}

				sb.append(arg.type).append(" ").append(arg.name).append(", ");
			}
			sb.setLength(sb.length() - 2);
		}

		sb.append(") ");
		sb.append("{\n\t\t");
		TextClassBuilder.writeTabbed(body.write(), sb);
		sb.append("}\n\t");
		sb.append("\n\t");
	}

	public record Arg(CBType type, String name, List<CBAnnotation> annotations, String comment) {
		public Arg(Class<?> t, String name) {
			this(CBType.of(t), name, null, null);
		}

		public Arg(Class<?> t, String name, List<CBAnnotation> annotations, String comment) {
			this(CBType.of(t), name, annotations, comment);
		}
	}
}
