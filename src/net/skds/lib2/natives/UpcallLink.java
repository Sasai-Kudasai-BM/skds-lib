package net.skds.lib2.natives;

import lombok.RequiredArgsConstructor;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;

@RequiredArgsConstructor
public class UpcallLink<T> {

	private final FunctionDescriptor descriptor;
	private final MethodHandle virtualHandle;

	public MemorySegment bind(T receiver) {
		return LinkerUtils.LINKER.upcallStub(
				virtualHandle.bindTo(receiver),
				descriptor,
				Arena.ofAuto()
		);
	}
	
	public MemorySegment bind(T receiver, Arena arena) {
		return LinkerUtils.LINKER.upcallStub(
				virtualHandle.bindTo(receiver),
				descriptor,
				LinkerUtils.ARENA
		);
	}
}
