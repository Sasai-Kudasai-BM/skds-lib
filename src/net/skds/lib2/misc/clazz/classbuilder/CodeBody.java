package net.skds.lib2.misc.clazz.classbuilder;

import java.util.Arrays;
import java.util.List;

public record CodeBody(List<String> body, List<CBType> imports) {

	public CodeBody(String body) {
		this(Arrays.asList(body.split("\n")), null);
	}

	public CodeBody(String... body) {
		this(Arrays.asList(body), null);
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

	public String write() {
		StringBuilder sb = new StringBuilder();
		if (multiline()) {
			for (String l : body) {
				sb.append(l).append("\n");
			}
			sb.setLength(sb.length() - 1);
		} else if (!body.isEmpty()) {
			sb.append(body.getFirst());
		}
		return sb.toString();
	}
}
