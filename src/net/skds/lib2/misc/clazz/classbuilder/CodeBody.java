package net.skds.lib2.misc.clazz.classbuilder;

import java.util.List;

public record CodeBody(List<String> body, List<CBType> imports) {

	public CodeBody(String body) {
		this(List.of(body), null);
	}

	public CodeBody(List<String> body) {
		this(body, null);
	}

	public void imports(TextClassBuilder classBuilder) {
		if (imports != null) for (CBType t : imports) {
			classBuilder.checkImport(t);
		}
	}

	public boolean multiline() {
		return body.size() > 1;
	}

	public void write(StringBuilder sb) {
		if (multiline()) {
			sb.append("{\n\t");
			for (String l : body) {
				sb.append("\t").append(l).append("\n\t");
			}
			sb.append("}\n\t");
		} else if (!body.isEmpty()) {
			sb.append(body.get(0));
		}
	}
}
