package net.skds.tests.json;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.tests.json.JsonTest.JsonTestRegistry;

import java.io.IOException;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class JsonTestReplaceDefault {

	public static void test(JsonTestRegistry registry) {
		//registry.toJson(new JsonTestReplaceDefault());
		registry.addFactory(JTRD.class, (t, r) -> {
			return new JTRD2(t, r);
		});
		registry.toJson(new JsonTestReplaceDefault());
	}

	//@DefaultJsonCodec(JTRD1.class)
	private JTRD field = new JTRD();

	@DefaultCodec(JTRD1.class)
	private class JTRD {
	}

	private static class JTRD1 extends AbstractCodec<JTRD> {

		protected JTRD1(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(JTRD value, UniversalWriter writer) throws IOException {
			writer.writeNull();
			System.out.println("1");
		}

		@Override
		public JTRD read(UniversalReader reader) throws IOException {
			throw new UnsupportedOperationException("Unimplemented method 'read'");
		}
	}

	private static class JTRD2 extends AbstractCodec<JTRD> {

		protected JTRD2(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(JTRD value, UniversalWriter writer) throws IOException {
			writer.writeNull();
			System.out.println("2");
		}

		@Override
		public JTRD read(UniversalReader reader) throws IOException {
			throw new UnsupportedOperationException("Unimplemented method 'read'");
		}
	}
}
