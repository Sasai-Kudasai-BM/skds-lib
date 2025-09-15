package net.skds.lib2.natives;

import java.lang.foreign.Arena;
import java.lang.foreign.SymbolLookup;
import java.nio.file.Path;

public abstract class AbstractLinkedLibrary {

	protected final SymbolLookup lib;
	protected final Arena arena = Arena.ofAuto();

	public AbstractLinkedLibrary(String library) {
		this.lib = LinkerUtils.library(library, arena);
	}

	public AbstractLinkedLibrary(Path library) {
		this.lib = LinkerUtils.library(library, arena);
	}

}
