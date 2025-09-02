package net.skds.tests.json;

import net.skds.lib2.io.codec.*;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.codec.annotation.SerializationAlias;
import net.skds.lib2.io.json.annotation.JsonComment;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.mat.vec3.Vec3D;
import net.w3e.lib.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class JsonTestAmogus {

	public static void test(JsonTest.JsonTestRegistry registry) {
		UniversalCodec<JsonTestAmogus> codec = registry.getCodec(JsonTestAmogus.class);

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
		UniversalCodec<JsonElement> jec = registry.getCodec(JsonElement.class);
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
	@SerializationAlias("C-Gay")
	private final int c = 3;
	private Vec3D vec = new Vec3D(1, -1, 2);
	//private Pizdun p = new Pizdun("u");
	int d = 3;

	private Map<Float, JsonTestAmogus> meps;

	private JsonTestAmogus amogus = null;

	@DefaultCodec(AnusCodec.class)
	private interface Anus {
	}

	private static final class AnusCodec extends AbstractCodec<Anus> {

		public AnusCodec(Type type, UniversalCodecRegistry registry) {
			super(type, registry);
			System.out.println("create");
		}

		@Override
		public Anus read(UniversalReader reader) throws IOException {
			reader.skipNull();
			System.out.println("anus read");
			return null;
		}

		@Override
		public void write(Anus value, UniversalWriter writer) throws IOException {
			writer.writeNull();
			System.out.println("anus write");
		}
	}
}
