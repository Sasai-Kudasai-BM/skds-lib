package net.skds.tests;

import net.skds.lib2.utils.logger.SKDSLogger;
import net.skds.lib2.utils.tlsf.TLSFBlock;
import net.skds.lib2.utils.tlsf.TLSFPage;

public class TLSFTest {

	static void main() {
		SKDSLogger.replaceOuts();

		TLSFPage tlsfPage = new TLSFPage(1024 * 2, 32);

		TLSFBlock block0 = tlsfPage.allocate(1024);
		System.out.println(block0.getOffset());

		TLSFBlock block1 = tlsfPage.allocate(100);
		tlsfPage.free(block0);
		TLSFBlock block2 = tlsfPage.allocate(1000);
		TLSFBlock block3 = tlsfPage.allocate(10);
		TLSFBlock block4 = tlsfPage.allocate(1);

		System.out.println(block1.getOffset());
		System.out.println(block2.getOffset());
		System.out.println(block3.getOffset());
		System.out.println(block4.getOffset());
		tlsfPage.free(block2);
		block2 = tlsfPage.allocate(500);
		block3 = tlsfPage.allocate(200);
		System.out.println(block2.getOffset());
		System.out.println(block3.getOffset());
		System.out.println(tlsfPage.allocate(1024));
	}
}
