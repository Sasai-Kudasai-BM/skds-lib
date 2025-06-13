package net.skds.lib2.storage.palette;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.io.ExtendedDataOutput;
import net.skds.lib2.mat.VarInt;

import java.io.IOException;

public class PalettedStorage<T> implements Cloneable {

	protected T defaultValue;
	@Setter
	@Getter
	protected PalettedData data;
	public final int bits;
	public final int size;
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
		int ind = data.setValue(index, directSupplier.getIndex(newValue));
		return directSupplier.get(ind);
	}

	public int getDataSize() {
		if (isSingle()) {
			return 1 + VarInt.getSize(directSupplier.getIndex(defaultValue)) + 1;
		}
		int size = VarInt.getSize(bits);
		size += VarInt.getSize(data.words.length);
		size += data.words.length * 8;
		return size;
	}

	public void write(ExtendedDataOutput output) throws IOException {
		if (isSingle()) {
			output.writeByte(0); // Bits Per Entry
			output.writeVarInt(directSupplier.getIndex(defaultValue)); // Palette
			output.writeVarInt(0); //Data Array Length
			// empty //Data Array
		} else {
			output.writeByte(bits); // Bits Per Entry
			output.writeVarInt(data.words.length); //Data Array Length
			for (int i = 0; i < data.words.length; i++) {
				output.writeLong(data.words[i]);
			}
		}
	}

	public static interface DirectSupplier<T> {

		T getDefault();

		T get(int index);

		int getIndex(T value);

		int size();

		int bitThreshold();

		int minBits();

		default int bits() {
			return calcBits(size());
		}
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
