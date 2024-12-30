package net.skds.lib2.utils;

import lombok.experimental.UtilityClass;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Objects;

@UtilityClass
public class Types {

	public static ParameterizedType parameterizedType(Type rawType, Type... typeArguments) {
		return new ParameterizedTypeImpl(null, rawType, typeArguments);
	}

	public static ParameterizedType parameterizedOwnedType(Type ownerType, Type rawType, Type... typeArguments) {
		return new ParameterizedTypeImpl(ownerType, rawType, typeArguments);
	}

	@SuppressWarnings("ClassCanBeRecord")
	private static final class ParameterizedTypeImpl implements ParameterizedType {
		private final Type ownerType;
		private final Type rawType;
		private final Type[] typeArguments;

		public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... typeArguments) {

			if (rawType instanceof Class<?> rawTypeAsClass) {
				boolean isStaticOrTopLevelClass = Modifier.isStatic(rawTypeAsClass.getModifiers())
						|| rawTypeAsClass.getEnclosingClass() == null;
				if (ownerType == null && !isStaticOrTopLevelClass) {
					throw new IllegalArgumentException();
				}
			}

			this.ownerType = ownerType;
			this.rawType = rawType;
			this.typeArguments = typeArguments.clone();
			for (int t = 0, length = this.typeArguments.length; t < length; t++) {
				checkNotPrimitive(this.typeArguments[t]);
			}
		}

		@Override
		public Type[] getActualTypeArguments() {
			return typeArguments.clone();
		}

		@Override
		public Type getRawType() {
			return rawType;
		}

		@Override
		public Type getOwnerType() {
			return ownerType;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof ParameterizedType
					&& Types.equals(this, (ParameterizedType) other);
		}

		private static int hashCodeOrZero(Object o) {
			return o != null ? o.hashCode() : 0;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(typeArguments)
					^ rawType.hashCode()
					^ hashCodeOrZero(ownerType);
		}

		@Override
		public String toString() {
			int length = typeArguments.length;
			if (length == 0) {
				return typeToString(rawType);
			}

			StringBuilder stringBuilder = new StringBuilder(30 * (length + 1));
			stringBuilder.append(typeToString(rawType)).append("<").append(typeToString(typeArguments[0]));
			for (int i = 1; i < length; i++) {
				stringBuilder.append(", ").append(typeToString(typeArguments[i]));
			}
			return stringBuilder.append(">").toString();
		}
	}

	static void checkNotPrimitive(Type type) {
		if (type instanceof Class<?> cl && cl.isPrimitive()) {
			throw new IllegalArgumentException();
		}
	}

	static String typeToString(Type type) {
		return type instanceof Class ? ((Class<?>) type).getName() : type.toString();
	}

	public static boolean equals(Type a, Type b) {
		if (a == b) {
			return true;
		} else if (a instanceof Class) {
			return a.equals(b);
		} else if (a instanceof ParameterizedType pa) {
			if (!(b instanceof ParameterizedType pb)) {
				return false;
			}
			return Objects.equals(pa.getOwnerType(), pb.getOwnerType())
					&& pa.getRawType().equals(pb.getRawType())
					&& Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments());
		} else if (a instanceof GenericArrayType ga) {
			if (!(b instanceof GenericArrayType gb)) {
				return false;
			}
			return equals(ga.getGenericComponentType(), gb.getGenericComponentType());
		} else if (a instanceof WildcardType wa) {
			if (!(b instanceof WildcardType wb)) {
				return false;
			}
			return Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds())
					&& Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds());
		} else if (a instanceof TypeVariable<?> va) {
			if (!(b instanceof TypeVariable<?> vb)) {
				return false;
			}
			return va.getGenericDeclaration() == vb.getGenericDeclaration()
					&& va.getName().equals(vb.getName());
		} else {
			return false;
		}
	}
}
