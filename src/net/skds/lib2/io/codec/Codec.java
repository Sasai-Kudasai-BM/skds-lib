package net.skds.lib2.io.codec;

public interface Codec<V, W, R> extends Serializer<V, W>, Deserializer<V, R> {

}
