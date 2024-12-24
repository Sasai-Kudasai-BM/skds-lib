package net.skds.lib2.utils.json;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public interface JsonAdapter<S, D> extends JsonSerializer<S>, JsonDeserializer<D> {}
