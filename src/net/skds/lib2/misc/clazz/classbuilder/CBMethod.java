package net.skds.lib2.misc.clazz.classbuilder;

import java.lang.reflect.Modifier;
import java.util.List;

public sealed class CBMethod extends CBElement permits CBConstructor {

	public final List<CBArgument> arguments;
	public final CodeBody body;

	public CBMethod(String name, int modifiers, CBType type, List<CBAnnotation> annotations, Object comment, List<CBArgument> arguments, CodeBody body) {
		super(name, modifiers, type, annotations, comment);
		this.arguments = arguments;
		this.body = body;
	}

	@Override
	public void imports(TextClassBuilder classBuilder) {
		super.imports(classBuilder);
		CBArgument.imports(this.arguments, classBuilder);
		if (body != null) body.imports(classBuilder);
	}

	protected void writeMethod(StringBuilder sb) {
		sb.append("(");
		CBArgument.writeMethod(this.arguments, sb);
		sb.append(")");

		if (body != null) {
			sb.append(" {");
			TextClassBuilder.writeTabbed2(body.write(), sb);
			sb.append("}");
		} else {
			sb.append(";");
		}
	}

	@Override
	public void write(StringBuilder sb) {
		super.write(sb);
		writeMethod(sb);
	}

	public static CBMethod createGetter(CBField field) {
		return createGetter(field, false);
	}

	public static CBMethod createGetter(CBField field, boolean override) {
		String name = "get" + field.name.substring(0, 1).toUpperCase() + field.name.substring(1);
		return new CBMethod(name, Modifier.PUBLIC, field.type,
				override ? List.of(new CBAnnotation(Override.class)) : null, null, null,
				new CodeBody("return this." + field.name + ";")
		);
	}

}
