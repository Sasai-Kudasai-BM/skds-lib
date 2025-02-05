package net.skds.lib2.natives;

import lombok.RequiredArgsConstructor;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

@RequiredArgsConstructor
public class UpcallLink<T> {

	private final FunctionDescriptor descriptor;
	private final MethodHandle virtualHandle;

	public MemorySegment bind(T receiver) {
		return SafeLinker.LINKER.upcallStub(
				virtualHandle.bindTo(receiver),
				descriptor,
				SafeLinker.ARENA
		);
	}
}
