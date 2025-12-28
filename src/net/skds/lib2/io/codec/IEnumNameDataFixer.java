package net.skds.lib2.io.codec;

// TODO enumNameDataFixer
public interface IEnumNameDataFixer<E extends Enum<E>> {
	E fixName(String name);
}
