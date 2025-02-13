package net.skds.lib2.io.json.test;

import java.io.IOException;
import java.lang.reflect.Type;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.test.JsonTest.JsonTestRegistry;

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

	@DefaultJsonCodec(JTRD1.class)
	private class JTRD {}

	private static class JTRD1 extends AbstractJsonCodec<JTRD> {
	
		protected JTRD1(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(JTRD value, JsonWriter writer) throws IOException {
			writer.writeNull();
			System.out.println("1");
		}

		@Override
		public JTRD read(JsonReader reader) throws IOException {
			throw new UnsupportedOperationException("Unimplemented method 'read'");
		}
	}

	private static class JTRD2 extends AbstractJsonCodec<JTRD> {
	
		protected JTRD2(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(JTRD value, JsonWriter writer) throws IOException {
			writer.writeNull();
			System.out.println("2");
		}

		@Override
		public JTRD read(JsonReader reader) throws IOException {
			throw new UnsupportedOperationException("Unimplemented method 'read'");
		}
	}
}
