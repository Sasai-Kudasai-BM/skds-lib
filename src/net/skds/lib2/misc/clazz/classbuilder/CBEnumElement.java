package net.skds.lib2.misc.clazz.classbuilder;

import java.util.List;

public final class CBEnumElement extends CBAbstractElement {

	public final List<CBEnumArgument> arguments;

	public CBEnumElement(String name, List<CBAnnotation> annotations, Object comment, List<CBEnumArgument> arguments) {
		super(name, annotations, comment);
		this.arguments = arguments;
	}

	@Override
	public void imports(TextClassBuilder classBuilder) {
		super.imports(classBuilder);
		CBArgument.imports(this.arguments, classBuilder);
	}

	@Override
	public void write(StringBuilder sb) {
		super.write(sb);
		sb.append(name);
		if (this.arguments != null) {
			sb.append("(");
			CBArgument.writeMethod(this.arguments, sb);
			sb.append(")");
		}
	}

	public static class CBEnumArgument extends CBArgument {

		public CBEnumArgument(CBField field, String value) {
			this(field.type, null, null, value);
		}

		public CBEnumArgument(CBType type, List<CBAnnotation> annotations, String comment, String value) {
			super(type, value, annotations, comment);
		}

		public CBEnumArgument(Class<?> t, String value) {
			super(t, value);
		}

		public CBEnumArgument(Class<?> t, List<CBAnnotation> annotations, String comment, String value) {
			super(t, value, annotations, comment);
		}

		@Override
		protected void writeMethod(StringBuilder sb) {
			sb.append(this.name).append(", ");
		}
	}
}
