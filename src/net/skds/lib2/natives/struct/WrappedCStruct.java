package net.skds.lib2.natives.struct;

import java.lang.foreign.Arena;

public abstract class WrappedCStruct extends CStruct {

	@Override
	public int getSize() {
		return (int) getWrapper().layout.byteSize();
	}

	@Override
	public int getAlignment() {
		return (int) getWrapper().layout.byteAlignment();
	}

	public final void allocate() {
		getWrapper().alloc(this);
	}

	public final void allocate(Arena arena) {
		getWrapper().alloc(arena, this);
	}

	public final void take() {
		if (segment == null) throw new IllegalStateException("Structure is not initialized");
		getWrapper().take(this);
	}

	public final void allocTake() {
		CStructWrapper<?> wrapper = getWrapper();
		wrapper.alloc(this);
		wrapper.take(this);
	}

	public final void allocTake(Arena arena) {
		CStructWrapper<?> wrapper = getWrapper();
		wrapper.alloc(arena, this);
		wrapper.take(this);
	}

	public final void put() {
		if (segment == null) throw new IllegalStateException("Structure is not initialized");
		getWrapper().put(this);
	}

	public final void allocPut() {
		CStructWrapper<?> wrapper = getWrapper();
		wrapper.alloc(this);
		wrapper.put(this);
	}

	public final void allocPut(Arena arena) {
		CStructWrapper<?> wrapper = getWrapper();
		wrapper.alloc(arena, this);
		wrapper.put(this);
	}

	/**
	 * @return instance of CStructWrapper that unique for every class
	 */
	public abstract CStructWrapper<? extends WrappedCStruct> getWrapper();
}
