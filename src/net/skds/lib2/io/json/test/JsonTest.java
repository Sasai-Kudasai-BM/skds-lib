package net.skds.lib2.io.json.test;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.skds.lib2.demo.demo3d.Demo3dFrame;
import net.skds.lib2.demo.demo3d.Demo3dFrameExample;
import net.skds.lib2.demo.demo3d.Demo3dShape;
import net.skds.lib2.demo.demo3d.Demo3dShapeCollector;
import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonPostDeserializeCall;
import net.skds.lib2.io.json.JsonPreSerializeCall;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.*;
import net.skds.lib2.io.json.codec.*;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.typed.TypedConfig;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.io.json.elements.JsonObject;
import net.skds.lib2.mat.*;
import net.skds.lib2.mat.vec2.Vec2;
import net.skds.lib2.mat.vec2.Vec2D;
import net.skds.lib2.mat.vec2.Vec2F;
import net.skds.lib2.mat.vec2.Vec2I;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec3.Vec3D;
import net.skds.lib2.mat.vec3.Vec3F;
import net.skds.lib2.mat.vec3.Vec3I;
import net.skds.lib2.utils.AnsiEscape;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.w3e.lib.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("unused")
@CustomLog
public class JsonTest {

	public static void main(String[] args) throws IOException, URISyntaxException {
		SKDSLogger.replaceOuts();
		String test2 = """
				  [[
				  	[null, "a", "b", "c", "d", "e"],
				  	["cum", "jizz"]
				  ]]
				""";

		String test3 = """
				  {
				  "3":1,
				  "34":2,
				  "35":3
				  }
				""";
		String test4 = """
				  {
				  }
				""";


		JsonCodecOptions options = new JsonCodecOptions();
		options.setCapabilityVersion(JsonCapabilityVersion.JSON_WITH_COMMENTS);
		options.setDecorationType(JsonCodecOptions.DecorationType.FANCY);

		JsonCodecRegistry registry = new JsonCodecRegistry(options, null);

		Map<String, Consumer<JsonCodecRegistry>> runs = new LinkedHashMap<>();
		runs.put("amogus", JsonTestAmogus::test);
		runs.put("record", JsonTestRecord::test);
		runs.put("yup", JsonTestYup::test);
		runs.put("dg", JsonTestDg::test);
		runs.put("collections", JsonTestCollections::test);
		runs.put("simple", JsonTestSimple::test);
		runs.put("vec3", JsonTestVec3::test);
		runs.put("shape", JsonTestShapes::test);

		if (args.length != 0) {
			List<String> list = new ArrayList<>(runs.keySet());
			for (String string : args) {
				list.remove(string);
			}
			for (String string : list) {
				runs.remove(string);
			}
		}

		String split = AnsiEscape.MAGENTA.sequence + "#";
		split = split + AnsiEscape.GREEN.sequence + "=".repeat(40) + split;
		for (Entry<String, Consumer<JsonCodecRegistry>> entry : runs.entrySet()) {
			log.info(entry.getKey());
			entry.getValue().accept(registry);
			log.info(split);
		}

		//throw new RuntimeException(new Exception("seaxe"));

		/*Demo3dShapeHolder shaper = new Demo3dShapeHolder() {
			@Override
			public void addShape(Demo3dShape shape) {
				
			}
		};
		Demo3dFrameExample.initDefault(shaper);*/
	}
}
