package net.skds.lib2.io.json.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.typed.TypedConfig;
import net.skds.lib2.io.json.test.JsonTest.JsonTestRegistry;

public class JsonTestNest {
	private static final NestAdapter<?> parentAdapter = new NestAdapter<>("parent", JTNParent.class);
	private static final NestAdapter<?> childAdapter = new NestAdapter<>("child", JTNChild.class);
	private static final Map<String, NestAdapter<?>> ADAPTER_MAP = Map.of(
		parentAdapter.keyName, parentAdapter,
		childAdapter.keyName, childAdapter
	);

	public static void test(JsonTestRegistry registry) {
		JsonUtils.addTypedAdapter(JTN.class, ADAPTER_MAP);
		ListHolder list = new ListHolder();
		list.list = new ArrayList<>();
		JTNParent parent = new JTNParent();
		parent.child = new JTNParent();
		list.list.add(parent);
		System.out.println(JsonUtils.toJson(list));
	}

	private static class ListHolder {
		public List<JTN> list = Arrays.asList(new JTNParent());
	}

	private abstract static class JTN implements TypedConfig {

	}

	@SuppressWarnings("unused")
	private static class JTNParent extends JTN {
		public JTN child = new JTNChild();

		@Override
		public ConfigType<?> getConfigType() {
			return parentAdapter;
		}
	}

	private static class JTNChild extends JTN {

		@Override
		public ConfigType<?> getConfigType() {
			return childAdapter;
		}
		
	}

	@AllArgsConstructor
	public static class NestAdapter<CT> implements ConfigType<CT> {

		private final String keyName;
		@Getter(onMethod_ = {@Override})
		private final Class<CT> typeClass;

		@Override
		public final String keyName() {
			return this.keyName;
		}
	}
}
