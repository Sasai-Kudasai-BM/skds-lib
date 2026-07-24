package net.skds.tests;

import net.skds.lib2.natives.MemoryAccess;
import net.skds.lib2.natives.wrapper.ForeignWrapper;
import net.skds.lib2.natives.wrapper.annotation.LibraryInfo;
import net.skds.lib2.natives.wrapper.annotation.LibraryName;
import net.skds.lib2.utils.SKDSUtils;

import java.lang.foreign.Arena;

import static net.skds.lib2.natives.MemoryAccess.getInt;

public class FWTest {

	static void main() {
		System.out.println("start");

		GLTest glTest = ForeignWrapper.wrap(GLTest.class);
		System.out.println(glTest);
		System.out.println(glTest.glGetError());
		System.out.println(glTest.glGetString(0x1F02));
		glTest.glClearColor(0, 0, 0, 0);
		System.out.println("end GL");
		System.out.println("hash " + glTest.hashCode());

		VKTest vkTest = ForeignWrapper.wrap(VKTest.class);
		try (Arena arena = Arena.ofConfined()) {
			long lPtr0 = MemoryAccess.alloc8(arena, 1);
			System.out.println("err: " + vkTest.vkEnumerateInstanceLayerProperties(lPtr0, 0));
			System.out.println("size: " + getInt(lPtr0));
			System.out.println("err: " + vkTest.vkEnumerateInstanceExtensionProperties(0, lPtr0, 0));
			System.out.println("size: " + getInt(lPtr0));
		}
		System.out.println("end VK");

		Null nullTest = ForeignWrapper.wrap(Null.class);
		try {
			nullTest.who(1, 2);
		} catch (Exception e) {
			System.out.println(e.getCause() + "");
		}
		System.out.println("end");
	}

	@LibraryInfo({
			@LibraryName(os = SKDSUtils.OSType.WINDOWS, value = "opengl32"),
			@LibraryName(os = SKDSUtils.OSType.LINUX, value = "libGL.so")
	})
	public interface GLTest {

		long glGetError();

		long glGetString(int string);

		void glClearColor(float r, float g, float b, float a);
	}

	@LibraryInfo({
			@LibraryName(os = SKDSUtils.OSType.WINDOWS, value = "vulkan-1"),
			@LibraryName(os = SKDSUtils.OSType.LINUX, value = "libvulkan.so.1")
	})
	public interface VKTest {

		int vkEnumerateInstanceLayerProperties(long pPropertyCount, long pProperties);

		int vkEnumerateInstanceExtensionProperties(long pLayerName, long pPropertyCount, long pProperties);
	}

	public interface Null {

		int who(long a, long b);
	}
}
