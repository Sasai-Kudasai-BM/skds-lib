package net.skds.lib2.natives;

import java.lang.foreign.SymbolLookup;
import java.nio.file.Path;

public abstract class AbstractLinkedLibrary {

	protected final SymbolLookup lib;

	public AbstractLinkedLibrary(String library) {
		this.lib = SafeLinker.library(library);
	}

	public AbstractLinkedLibrary(Path library) {
		this.lib = SafeLinker.library(library);
	}

}
