package net.skds.lib2.io.json.test;

import net.skds.lib2.io.chars.StringCharOutput;
import net.skds.lib2.io.exception.EndOfOutputException;
import net.skds.lib2.io.json.test.JsonTest.JsonTestRegistry;
import net.skds.lib2.utils.StringUtils;

public class JsonTestEscape {

	public static void test(JsonTestRegistry registry) {
		write("");
		write("abc");
		write("'abc'");
		write("\"abc\"");
		write("\"abc\"");
		write("a\\tb");
		write("a\tb");
		write("a\bb");
		write("a\nb");
		write("a\rb");
		write("a\fb");
	}

	private static void write(String input) {
		StringCharOutput output = new StringCharOutput();
		try {
			StringUtils.writeQuoted(output, input, '"');
		} catch (EndOfOutputException e) {
			e.printStackTrace();
		}
		System.out.println("|%s| -> |%s|".formatted(input, output.toString()));
	}
}
