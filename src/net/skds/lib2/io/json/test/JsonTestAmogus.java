package net.skds.lib2.io.json.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.annotation.JsonAlias;
import net.skds.lib2.io.json.annotation.JsonComment;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.io.json.test.JsonTest.JsonTestRegistry;
import net.skds.lib2.io.json.test.JsonTest.JsonTestRun;
import net.skds.lib2.mat.vec3.Vec3D;
import net.w3e.lib.utils.FileUtils;

@SuppressWarnings("unused")
public class JsonTestAmogus {

	public static void test(JsonTestRegistry registry) {
		JsonCodec<JsonTestAmogus> codec = registry.getCodec(JsonTestAmogus.class);

		JsonTestAmogus amogus = new JsonTestAmogus();
		amogus.vec = new Vec3D(0, 100, 0);
		amogus.amogus = new JsonTestAmogus();
		amogus.amogus.vec = null;
		amogus.amogus.a = 1488;
		amogus.meps = Map.of(1F, amogus.amogus, 2F, new JsonTestAmogus());
		amogus.anusis = List.of(new Anus() {
			@Override
			public int hashCode() {
				return super.hashCode();
			}
		});
		amogus.lol = List.of(new JsonTestAmogus(), new JsonTestAmogus());

		String json = codec.toJson(amogus);
		FileUtils.save(new File("amogus.json5"), json.getBytes(StandardCharsets.UTF_8));
		System.out.println(json);
		JsonTestAmogus amogus2 = codec.parse(json);
		String json2 = codec.toJson(amogus2);

		System.out.println(json2);

		System.out.println(json2.equals(json));
		JsonCodec<JsonElement> jec = registry.getCodec(JsonElement.class);
		JsonElement je = jec.parse(json2);
		System.out.println(je);
		JsonTestAmogus amg = codec.parse(je);
		String json3 = codec.toJson(amg);
		System.out.println(json2.equals(json3));
	}

	//private Set<Anus> anusis2 = null;
	//private Set<Integer> ints = Set.of(1, 2, 3);

	//private Pizdun ssss = new Pizdun();

	private List<Anus> anusis = null;
	@JsonComment("Am cuuummm zzz")
	private List<JsonTestAmogus> lol = null;

	@JsonComment("""
			suck
			nice
			""")
	private int a = 1;
	private final int b = 2;
	@JsonAlias("C-Gay")
	private final int c = 3;
	private Vec3D vec = new Vec3D(1, -1, 2);
	//private Pizdun p = new Pizdun("u");
	int d = 3;

	private Map<Float, JsonTestAmogus> meps;

	private JsonTestAmogus amogus = null;

	@DefaultJsonCodec(AnusCodec.class)
	private interface Anus {}

	private static final class AnusCodec extends AbstractJsonCodec<Anus> {

		public AnusCodec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
			System.out.println("create");
		}

		@Override
		public Anus read(JsonReader reader) throws IOException {
			reader.skipNull();
			System.out.println("anus read");
			return null;
		}

		@Override
		public void write(Anus value, JsonWriter writer) throws IOException {
			writer.writeNull();
			System.out.println("anus write");
		}
	}
}
