package net.skds.lib2.utils;

@SuppressWarnings("unused")
public final class Holders {
	private Holders() {
	}


	public static class Multi2Holder<A, B> {
		protected A valueA;
		protected B valueB;

		public Multi2Holder<A, B> setValue(A valueA, B valueB) {
			this.valueA = valueA;
			this.valueB = valueB;
			return this;
		}

		@Override
		public String toString() {
			return "MultiHolder(" + valueA + ", " + valueB + ")";
		}

		public Multi2Holder() {
		}

		public Multi2Holder(final A valueA, final B valueB) {
			this.valueA = valueA;
			this.valueB = valueB;
		}

		public A getValueA() {
			return this.valueA;
		}

		public B getValueB() {
			return this.valueB;
		}

		public void setValueA(final A valueA) {
			this.valueA = valueA;
		}

		public void setValueB(final B valueB) {
			this.valueB = valueB;
		}
	}


	public static class Multi3Holder<A, B, C> {
		protected A valueA;
		protected B valueB;
		protected C valueC;

		public Multi3Holder<A, B, C> setValue(A valueA, B valueB, C valueC) {
			this.valueA = valueA;
			this.valueB = valueB;
			this.valueC = valueC;
			return this;
		}

		@Override
		public String toString() {
			return "MultiHolder(" + valueA + ", " + valueB + ", " + valueC + ")";
		}

		public Multi3Holder() {
		}

		public Multi3Holder(final A valueA, final B valueB, final C valueC) {
			this.valueA = valueA;
			this.valueB = valueB;
			this.valueC = valueC;
		}

		public A getValueA() {
			return this.valueA;
		}

		public B getValueB() {
			return this.valueB;
		}

		public C getValueC() {
			return this.valueC;
		}

		public void setValueA(final A valueA) {
			this.valueA = valueA;
		}

		public void setValueB(final B valueB) {
			this.valueB = valueB;
		}

		public void setValueC(final C valueC) {
			this.valueC = valueC;
		}
	}


	public static class Multi4Holder<A, B, C, D> {
		protected A valueA;
		protected B valueB;
		protected C valueC;
		protected D valueD;

		public Multi4Holder<A, B, C, D> setValue(A valueA, B valueB, C valueC, D valueD) {
			this.valueA = valueA;
			this.valueB = valueB;
			this.valueC = valueC;
			this.valueD = valueD;
			return this;
		}

		@Override
		public String toString() {
			return "MultiHolder(" + valueA + ", " + valueB + ", " + valueC + ", " + valueD + ")";
		}

		public Multi4Holder() {
		}

		public Multi4Holder(final A valueA, final B valueB, final C valueC, final D valueD) {
			this.valueA = valueA;
			this.valueB = valueB;
			this.valueC = valueC;
			this.valueD = valueD;
		}

		public A getValueA() {
			return this.valueA;
		}

		public B getValueB() {
			return this.valueB;
		}

		public C getValueC() {
			return this.valueC;
		}

		public D getValueD() {
			return this.valueD;
		}

		public void setValueA(final A valueA) {
			this.valueA = valueA;
		}

		public void setValueB(final B valueB) {
			this.valueB = valueB;
		}

		public void setValueC(final C valueC) {
			this.valueC = valueC;
		}

		public void setValueD(final D valueD) {
			this.valueD = valueD;
		}
	}


	public static class Multi5Holder<A, B, C, D, E> {
		protected A valueA;
		protected B valueB;
		protected C valueC;
		protected D valueD;
		protected E valueE;

		public Multi5Holder<A, B, C, D, E> setValue(A valueA, B valueB, C valueC, D valueD, E valueE) {
			this.valueA = valueA;
			this.valueB = valueB;
			this.valueC = valueC;
			this.valueD = valueD;
			this.valueE = valueE;
			return this;
		}

		@Override
		public String toString() {
			return "MultiHolder(" + valueA + ", " + valueB + ", " + valueC + ", " + valueD + ", " + valueE + ")";
		}

		public Multi5Holder() {
		}

		public Multi5Holder(final A valueA, final B valueB, final C valueC, final D valueD, final E valueE) {
			this.valueA = valueA;
			this.valueB = valueB;
			this.valueC = valueC;
			this.valueD = valueD;
			this.valueE = valueE;
		}

		public A getValueA() {
			return this.valueA;
		}

		public B getValueB() {
			return this.valueB;
		}

		public C getValueC() {
			return this.valueC;
		}

		public D getValueD() {
			return this.valueD;
		}

		public E getValueE() {
			return this.valueE;
		}

		public void setValueA(final A valueA) {
			this.valueA = valueA;
		}

		public void setValueB(final B valueB) {
			this.valueB = valueB;
		}

		public void setValueC(final C valueC) {
			this.valueC = valueC;
		}

		public void setValueD(final D valueD) {
			this.valueD = valueD;
		}

		public void setValueE(final E valueE) {
			this.valueE = valueE;
		}
	}


	public static class ObjectHolder<T> {
		protected T value;

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}

		public ObjectHolder() {
		}

		public ObjectHolder(final T value) {
			this.value = value;
		}

		public T getValue() {
			return this.value;
		}

		public void setValue(final T value) {
			this.value = value;
		}
	}


	public static class IntHolder {
		protected int value;

		public int increment() {
			return increment(1);
		}

		public int increment(int inc) {
			return value += inc;
		}

		public int decrement() {
			return decrement(1);
		}

		public int decrement(int inc) {
			return value -= inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}

		public IntHolder() {
		}

		public IntHolder(final int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		public void setValue(final int value) {
			this.value = value;
		}
	}


	public static class LongHolder {
		protected long value;

		public long increment() {
			return increment(1);
		}

		public long increment(long inc) {
			return value += inc;
		}

		public long decrement() {
			return decrement(1);
		}

		public long decrement(long inc) {
			return value -= inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}

		public LongHolder() {
		}

		public LongHolder(final long value) {
			this.value = value;
		}

		public long getValue() {
			return this.value;
		}

		public void setValue(final long value) {
			this.value = value;
		}
	}


	public static class FloatHolder {
		protected float value;

		public float increment() {
			return increment(1);
		}

		public float increment(float inc) {
			return value += inc;
		}

		public float decrement() {
			return decrement(1);
		}

		public float decrement(float inc) {
			return value -= inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}

		public FloatHolder() {
		}

		public FloatHolder(final float value) {
			this.value = value;
		}

		public float getValue() {
			return this.value;
		}

		public void setValue(final float value) {
			this.value = value;
		}
	}


	public static class DoubleHolder {
		protected double value;

		public double increment() {
			return increment(1);
		}

		public double increment(float inc) {
			return value += inc;
		}

		public double decrement() {
			return decrement(1);
		}

		public double decrement(float inc) {
			return value -= inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}

		public DoubleHolder() {
		}

		public DoubleHolder(final double value) {
			this.value = value;
		}

		public double getValue() {
			return this.value;
		}

		public void setValue(final double value) {
			this.value = value;
		}
	}


	public static class BooleanHolder {
		protected boolean value;

		@Deprecated
		public boolean isValue() {
			return value;
		}

		public boolean getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}

		public BooleanHolder() {
		}

		public BooleanHolder(final boolean value) {
			this.value = value;
		}

		public void setValue(final boolean value) {
			this.value = value;
		}
	}


	public static class ByteHolder {
		protected byte value;

		public byte increment() {
			return increment((byte) 1);
		}

		public byte increment(byte inc) {
			return value += inc;
		}

		public byte decrement() {
			return decrement((byte) 1);
		}

		public byte decrement(byte inc) {
			return value -= inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}

		public ByteHolder() {
		}

		public ByteHolder(final byte value) {
			this.value = value;
		}

		public byte getValue() {
			return this.value;
		}

		public void setValue(final byte value) {
			this.value = value;
		}
	}


	public static class CharHolder {
		protected char value;

		public char increment() {
			return increment(1);
		}

		public char increment(int inc) {
			return value += inc;
		}

		public char decrement() {
			return decrement(1);
		}

		public char decrement(int inc) {
			return value -= inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}

		public CharHolder() {
		}

		public CharHolder(final char value) {
			this.value = value;
		}

		public char getValue() {
			return this.value;
		}

		public void setValue(final char value) {
			this.value = value;
		}
	}


	public static class ShortHolder {
		protected short value;

		public short increment() {
			return increment((short) 1);
		}

		public short increment(short inc) {
			return value += inc;
		}

		public short decrement() {
			return decrement((short) 1);
		}

		public short decrement(short inc) {
			return value -= inc;
		}

		@Override
		public String toString() {
			return "Holder(" + value + ")";
		}

		public ShortHolder() {
		}

		public ShortHolder(final short value) {
			this.value = value;
		}

		public short getValue() {
			return this.value;
		}

		public void setValue(final short value) {
			this.value = value;
		}
	}
}
