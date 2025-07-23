package net.skds.lib2.misc.ogg;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class OggProcessor {

	private final HashMap<Integer, Consumer<OggPage>> consumers = new HashMap<>();
	private final Function<OggPage, Consumer<OggPage>> consumerFactory;

	public OggProcessor(Function<OggPage, Consumer<OggPage>> consumerFactory) {
		this.consumerFactory = consumerFactory;
	}

	public void process(OggInputStream in) throws IOException {
		OggPage p;
		while ((p = in.readPage()) != null) {
			Integer sn = p.bitstreamSerialNumber;
			if (p.isBOS()) {
				Consumer<OggPage> consumer = consumerFactory.apply(p);
				if (consumer != null) {
					consumers.put(sn, consumer);
					consumer.accept(p);
				}
			} else if (p.isEOS()) {
				Consumer<OggPage> consumer = consumers.remove(sn);
				if (consumer != null) consumer.accept(p);
			} else {
				Consumer<OggPage> consumer = consumers.get(sn);
				if (consumer != null) consumer.accept(p);
			}
		}
	}
}
