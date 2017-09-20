package cadelac.framework.blade.core.code.compiler;

import java.io.File;

public interface DynamicCompiler {
	public int compile(final File sourceFile_);
	public File getSourceDir();
	public File getClassDir();
}
