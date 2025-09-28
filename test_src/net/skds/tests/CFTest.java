package net.skds.tests;

import net.skds.lib2.utils.SKDSUtils;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class CFTest {
	public static void main(String[] args) {
		SKDSLogger.replaceOuts();

		Queue<Runnable> ex = new ConcurrentLinkedQueue<>();
		Executor executor = ex::add;

		CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() -> {
			System.out.println("cf1");
			//throw new RuntimeException("cf1");
		}, executor);

		CompletableFuture<Void> cf2 = cf1.thenRunAsync(() -> {
			System.out.println("cf2");
			//throw new RuntimeException("cf2");
		}, executor);

		for (Runnable r; (r = ex.poll()) != null; ) {
			r.run();
		}
		
		CompletableFuture<Void> cf3 = cf2.thenRun(() -> {
			System.out.println("cf3");
			throw new RuntimeException("cf3");
		}).exceptionally(SKDSUtils.getCatcher());

		for (Runnable r; (r = ex.poll()) != null; ) {
			r.run();
		}

	}
}
