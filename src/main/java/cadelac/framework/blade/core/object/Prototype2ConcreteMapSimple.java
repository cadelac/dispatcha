package cadelac.framework.blade.core.object;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cadelac.framework.blade.Framework;
import cadelac.framework.blade.core.Utilities;
import cadelac.framework.blade.core.code.compiler.DynamicCompiler;
import cadelac.framework.blade.core.code.generator.CodeGenerator;
import cadelac.framework.blade.core.exception.SystemException;
import cadelac.framework.blade.core.message.Generated;
import cadelac.framework.blade.core.message.Message;

public class Prototype2ConcreteMapSimple implements Prototype2ConcreteMap {

	public Prototype2ConcreteMapSimple() {
		_map = new HashMap<Class<? extends Generated>,Class<? extends Generated>>();
		_reverseMap = new HashMap<Class<? extends Generated>,Class<? extends Generated>>();
	}
	

	@Override
	public Class<? extends Generated> prototypeClassOf(
			Class<? extends Generated> concreteClass_) {
		return _reverseMap.get(concreteClass_);
	}
	
	@Override
	public Class<? extends Generated> get(
			final Class<? extends Generated> prototypeClass_) 
					throws ClassNotFoundException, IOException, SystemException {
		final Class<? extends Generated> lookedupClass = _map.get(prototypeClass_);
		if (lookedupClass != null) {
			return lookedupClass;
		}
		// not found (i.e. not registered)
		// class not yet registered,
		return register(prototypeClass_);
	}

	@Override
	public void put(
			final Class<? extends Generated> prototypeClass_
			, final Class<? extends Generated> concreteClass_) {
		_map.put(prototypeClass_, concreteClass_);
		_reverseMap.put(concreteClass_, prototypeClass_);
	}

	private Class<? extends Generated> register(
			final Class<? extends Generated> protoClass_) 
					throws IOException, ClassNotFoundException, SystemException {
		
		final Package pkg = protoClass_.getPackage();
		final String packageName = pkg.getName();
		final String concreteClassName = protoClass_.getSimpleName() + "Simple";
		
		/* generate code */
		final CodeGenerator codeGenerator = new CodeGenerator();
		final String sourceCode = codeGenerator.generateSourceCode(protoClass_);
		logger.debug("generated code for concrete class " + packageName + "." + concreteClassName + " for prototype " + protoClass_.getSimpleName());
		
		/* create source file */
		final DynamicCompiler compiler = Framework.getCompiler();
		final File sourceFile = Utilities.writeSourceFile(compiler.getSourceDir(), compiler.getClassDir(), concreteClassName, sourceCode);		

		/* compile file */
		logger.debug("compiling concrete class " + packageName + "." + concreteClassName + " for prototype " + protoClass_.getSimpleName());
		compiler.compile(sourceFile);

		/* load the class */
		URL url = compiler.getClassDir().toURI().toURL();
		URLClassLoader classLoader = new URLClassLoader(new URL[] {url});
		@SuppressWarnings("unchecked")
		final Class<? extends Message> loadedClass = (Class<? extends Message>) classLoader.loadClass(packageName + "." + concreteClassName);
		classLoader.close();
		
		if (loadedClass == null) {
			// some kind of failure, throw exception...
			throw new SystemException(String.format("failed to register class: %s", protoClass_.getSimpleName()));
		}

		logger.debug("loaded concrete class " + packageName + "." + concreteClassName + " for prototype " + protoClass_.getSimpleName());
		
		/* install loaded class into map */
		this.put(protoClass_, loadedClass);

		return loadedClass;
	}
	
	
	private static final Logger logger = Logger.getLogger(Prototype2ConcreteMapSimple.class);
	
	private final Map<Class<? extends Generated>,Class<? extends Generated>> _map;
	private final Map<Class<? extends Generated>,Class<? extends Generated>> _reverseMap;
}
