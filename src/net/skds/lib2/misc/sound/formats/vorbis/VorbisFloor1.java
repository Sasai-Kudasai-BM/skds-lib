package net.skds.lib2.misc.sound.formats.vorbis;

import net.skds.lib2.utils.ArrayUtils;

import java.io.IOException;

public class VorbisFloor1 extends VorbisFloor {

	int multiplier;
	int rangebits;
	int[] classSubclasses;
	int[] classMasterbooks;
	int[][] subclassBooks;
	int[] xList;

	@Override
	public void read(ByteArrayBitInputStream input) throws IOException {

		int partitions = input.readInt(5);
		int maximumClass = -1;
		int[] partitionClassList = new int[partitions];
		for (int i = 0; i < partitions; i++) {
			int pc = input.readInt(4);
			partitionClassList[i] = pc;
			if (pc > maximumClass) maximumClass = pc;
		}
		int[] classDimensions = new int[maximumClass + 1];
		classSubclasses = new int[maximumClass + 1];
		classMasterbooks = new int[maximumClass + 1];
		subclassBooks = new int[maximumClass + 1][];
		for (int i = 0; i <= maximumClass; i++) {
			classDimensions[i] = input.readInt(3) + 1;
			int sc = input.readInt(2);
			classSubclasses[i] = sc;
			if (sc != 0) {
				classMasterbooks[i] = input.readInt(8);
			}
			int ex = (1 << sc);
			int[] bi = new int[ex];
			subclassBooks[i] = bi;
			for (int j = 0; j < ex; j++) {
				bi[j] = input.readInt(8) - 1;
			}
		}
		multiplier = input.readInt(2) + 1;
		rangebits = input.readInt(4);

		ArrayUtils.IntGrowingArray xList = new ArrayUtils.IntGrowingArray(16);

		xList.add(0);
		xList.add(1 << rangebits);

		for (int i = 0; i < partitions; i++) {
			int currentClassNumber = partitionClassList[i];
			int dim = classDimensions[currentClassNumber];
			for (int j = 0; j < dim; j++) {
				xList.add(input.readInt(rangebits));
			}
		}
		this.xList = xList.getArray();
	}

}
