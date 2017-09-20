package cadelac.framework.blade.core.code.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.tools.JavaCompiler;

import org.apache.log4j.Logger;

public class CompileHelper {

	public static int compile(final JavaCompiler compiler_, final String classPath_, final String classDir_, final String sourceText_) {
		ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		ByteArrayOutputStream stderr = new ByteArrayOutputStream();
		final String[] arguments = new String[] { 
				"-d", classDir_, 
				"-classpath", classPath_ + File.pathSeparatorChar + classDir_,
				sourceText_
				};
		
		int rc = compiler_.run(null, stdout, stderr, arguments);
		
		if (rc != 0) {
			final String stdoutStr = new String(stdout.toByteArray());
			final String stderrStr = new String(stderr.toByteArray());
			logger.error("\t*** compile error:\nstdout:\n" + stdoutStr + "\nstderr:\n" + stderrStr);
		}
		return rc;
	}
	
	private static final Logger logger = Logger.getLogger(CompileHelper.class);
}
