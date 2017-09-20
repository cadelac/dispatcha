package cadelac.framework.blade.core.code.compiler;

import java.io.File;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.log4j.Logger;

public class DefaultCompiler implements DynamicCompiler {

	public DefaultCompiler() {
		this(System.getProperty("java.class.path"), System.getProperty("user.dir"), REPOSITORY_DIR, SOURCE_DIR, CLASS_DIR);
	}
	
	public DefaultCompiler(final String rootDir_) {
		this(System.getProperty("java.class.path"), rootDir_, REPOSITORY_DIR, SOURCE_DIR, CLASS_DIR);
	}
	
	public DefaultCompiler(final String classPath_, final String rootDir_, final String repoDir_, final String sourceDir_, final String classDir_)  {
		_classPath = classPath_;
		_repoDir = new File(rootDir_, repoDir_);
		_sourceDir = new File(_repoDir, sourceDir_);
		_classDir = new File(_repoDir, classDir_);
		logger.debug("compiler created;\n\tclassPath\t[" + _classPath + "],\n\trootDir\t\t[" + rootDir_ + "],\n\trepoDir\t\t[" + _repoDir + "],\n\tsrcDir\t\t[" + _sourceDir + "],\n\tclassDir\t[" + _classDir + "]");
		_compiler = ToolProvider.getSystemJavaCompiler();
	}
	
	@Override
	public int compile(File sourceFile_) {
		return CompileHelper.compile(_compiler, _classPath, _classDir.toString(), sourceFile_.toString());
	}

	@Override
	public File getSourceDir() {
		return _sourceDir;
	}
	
	@Override
	public File getClassDir() {
		return _classDir;
	}

	private static final Logger logger = Logger.getLogger(DefaultCompiler.class);

	private static final String REPOSITORY_DIR = ".repo";
	private static final String SOURCE_DIR = "src";
	private static final String CLASS_DIR = "class";
	
	private final String _classPath;
	private final File _repoDir;
	private final File _sourceDir;
	private final File _classDir;
	private final JavaCompiler _compiler;
}
