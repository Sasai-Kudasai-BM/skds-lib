package net.skds.lib.storage;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib.utils.SKDSByteBuf;

public class PalettedStorage<T extends Palettable<T>> implements Cloneable {

	protected T defaultValue;
	@Setter
	@Getter
	protected PalettedData data;
	protected final int bits;
	protected final int size;
	protected final DirectSupplier<T> directSupplier;

	public PalettedStorage(int size, T defaultValue, DirectSupplier<T> directSupplier) {
		this.directSupplier = directSupplier;
		this.defaultValue = defaultValue;
		this.size = size;
		this.bits = directSupplier.bits();
	}

	public boolean isSingle() {
		return this.data == null;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(T value) {
		defaultValue = value;
	}

	public void expand() {
		this.data = new PalettedData(bits, size);
	}

	public T get(int index) {
		if (isSingle()) {
			return defaultValue;
		}
		return directSupplier.get(data.getValue(index));
	}

	public T set(int index, T newValue) {
		if (isSingle()) {
			if (newValue.equals(defaultValue)) {
				return defaultValue;
			}
			expand();
		}
		int ind = data.setValue(index, newValue.getIndex());
		return directSupplier.get(ind);
	}

	public int getDataSize() {
		if (isSingle()) {
			return 1 + SKDSByteBuf.getVarIntSize(defaultValue.getIndex()) + 1;
		}
		int size = SKDSByteBuf.getVarIntSize(bits);
		size += SKDSByteBuf.getVarIntSize(data.words.length);
		size += data.words.length * 8;
		return size;
	}

	public void write(SKDSByteBuf buffer) {
		if (isSingle()) {
			buffer.writeByte(0); // Bits Per Entry
			buffer.writeVarInt(defaultValue.getIndex()); // Palette
			buffer.writeVarInt(0); //Data Array Length
			// empty //Data Array
		} else {
			buffer.writeByte(bits); // Bits Per Entry
			buffer.writeVarInt(data.words.length); //Data Array Length
			for (int i = 0; i < data.words.length; i++) {
				buffer.putLong(data.words[i]);
			}
		}

	}

	public static interface DirectSupplier<T> {

		public T get(int index);

		public int size();

		public int bitThreshold();

		public int minBits();

		public default int bits() {
			return calcBits(size());
		}

		public Class<T> getType();
	}

	public static int calcBits(int values) {
		return 32 - Integer.numberOfLeadingZeros(values - 1);
	}

	@Override
	public PalettedStorage<T> clone() {
		PalettedStorage<T> st = new PalettedStorage<>(size, defaultValue, directSupplier);
		if (data != null) {
			st.data = new PalettedData(data.dataBits, data.capacity, data.words.clone());
		}
		return st;
	}
}
